package com.example.dbtest

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.dbtest.data.ReservationRepository
import com.example.dbtest.map.Building
import com.example.dbtest.map.CampusBuildings
import com.example.dbtest.map.Pathway
import com.example.dbtest.map.SiteMapView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class CampusMapActivity : AppCompatActivity() {

    private val zoomScale = 1.8f
    private val animDuration = 300L

    private lateinit var siteMapView: SiteMapView
    private lateinit var slider: Slider
    private var currentBuilding: Building? = null
    private var currentFloor = 1

    // Names of buildings that have a reservation covering THIS exact moment, loaded from the DB.
    // A building can still be booked any number of times for other, non-overlapping time slots -
    // this set only drives the "In Use Now" vs "Available Now" status indicator.
    private val reservedBuildings = mutableSetOf<String>()

    private val reservationRepository by lazy { ReservationRepository() }

    // Launches ReservationActivity and reacts once it returns, instead of a bare startActivity
    // that had no way to tell CampusMapActivity a reservation/cancel/done just happened.
    private val reservationLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Re-check from the DB rather than assuming - the change might have been a new
            // booking for later today, a cancellation, or a "marked done", each of which
            // affects the "right now" status differently.
            refreshReservedBuildingsAndSlider()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.campus_map)

        siteMapView = findViewById(R.id.siteMapView)
        slider = Slider(rootView = findViewById(R.id.slider_root))

        slider.onClose = { resetZoom() }

        // Handle back press with modern API
        val callback = object : OnBackPressedCallback(true /* enabled */) {
            override fun handleOnBackPressed() {
                if (slider.isOpen) {
                    slider.close()
                } else {
                    // finish() here would destroy this activity, and since it's the only
                    // one left in the task (LoginActivity already finished itself after
                    // login), that kills the whole app - forcing a fresh LoginActivity
                    // launch next time, undoing the saved session. moveTaskToBack just
                    // backgrounds the app, same as pressing Home: the task and this
                    // activity stay alive, so reopening resumes right here, still logged in.
                    moveTaskToBack(true)
                }
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)

        // Load the tappable building polygons onto the map. designWidth/designHeight must
        // be set BEFORE buildings, since assigning buildings rebuilds the hit-test regions
        // against the current design space.
        //
        // No background photo for now - the new layout is just the traced outline shapes
        // themselves (SiteMapView draws each Building as a filled/stroked polygon on its
        // own, so leaving backgroundBitmap unset already renders exactly that look). Drop
        // in a real site photo later by setting siteMapView.backgroundBitmap again and
        // re-tracing the polygons to match it if the photo's proportions differ.
        siteMapView.designWidth = 1284f
        siteMapView.designHeight = 531f
        // Beige walkway network, traced from the reference floor plan and
        // drawn beneath every building (see Pathway / SiteMapView.pathwayOutline).
        siteMapView.pathwayColor = Pathway.COLOR
        siteMapView.pathwayOutline = Pathway.OUTLINE
        siteMapView.pathwayHoles = Pathway.HOLES
        // Load every building/room once. Which floor is "active" (full opacity
        // + tappable) vs "ghosted" (faded, reference-only) is controlled by
        // siteMapView.activeFloor below, not by which list gets assigned here.
        siteMapView.buildings = CampusBuildings.ALL
        siteMapView.activeFloor = currentFloor

        // Tapping this flips between floor 1 and floor 2. Both floors' buildings
        // are always drawn - the inactive floor just shows faded as a reference
        // overlay and isn't tappable (see SiteMapView.activeFloor).
        findViewById<com.google.android.material.button.MaterialButton>(R.id.floorToggleButton).setOnClickListener {
            currentFloor = if (currentFloor == 1) 2 else 1
            applyFloor(currentFloor)
        }

        siteMapView.onBuildingClick = { building ->
            currentBuilding = building
            zoomToBuilding(building.id)

            // Use the real, up-to-date "in use right now" status instead of a hardcoded false.
            slider.open(building.label, isReserved = reservedBuildings.contains(building.label))
        }

        // Handle the slider's reserve button click to open ReservationActivity.
        // Always allowed - even a building in use right now can be booked for a different time.
        slider.onReserveClick = {
            val building = currentBuilding
            if (building != null) {
                val intent = Intent(this, ReservationActivity::class.java)
                intent.putExtra("BUILDING_NAME", building.label)
                reservationLauncher.launch(intent)
            }
        }

        // The settings FAB (gear icon, bottom-right) had no click listener at all before -
        // tapping it did nothing. Wire it up to open the Settings screen.
        findViewById<FloatingActionButton>(R.id.floatingActionButton).setOnClickListener {
            startActivity(Intent(this, Settings::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        // Reload every time this screen becomes visible - covers a brand new reservation,
        // a cancellation, and a "marked as done" change, all in one place.
        loadReservedBuildings()
    }

    private fun loadReservedBuildings() {
        lifecycleScope.launch {
            reservedBuildings.clear()
            reservedBuildings.addAll(fetchBuildingsReservedRightNow())
        }
    }

    private fun refreshReservedBuildingsAndSlider() {
        lifecycleScope.launch {
            reservedBuildings.clear()
            reservedBuildings.addAll(fetchBuildingsReservedRightNow())

            // If the slider is still open on a building, update its status immediately
            // instead of waiting for the next time it's opened.
            val label = currentBuilding?.label
            if (label != null) {
                slider.setReservationState(isReserved = reservedBuildings.contains(label))
            }
        }
    }

    private suspend fun fetchBuildingsReservedRightNow(): List<String> {
        val now = Calendar.getInstance()
        val today = String.format(
            Locale.US, "%04d-%02d-%02d",
            now.get(Calendar.YEAR), now.get(Calendar.MONTH) + 1, now.get(Calendar.DAY_OF_MONTH)
        )
        val nowTime = String.format(
            Locale.US, "%02d:%02d",
            now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE)
        )
        return reservationRepository.getBuildingsReservedRightNow(today, nowTime)
    }

    private fun applyFloor(floor: Int) {
        // Both floors' buildings are always loaded (see onCreate); this just
        // switches which one is full-opacity/tappable and which is faded.
        siteMapView.activeFloor = floor
        findViewById<com.google.android.material.button.MaterialButton>(R.id.floorToggleButton).text =
            "Floor $floor"
        // Switching floors can leave a selection/slider pointing at a room that
        // just disappeared from the map - close it out rather than leave it stale.
        if (slider.isOpen) {
            slider.close()
        }
        currentBuilding = null
    }

    private fun zoomToBuilding(id: String) {
        // Zoom in, centered on the building that was tapped.
        val center = siteMapView.getBuildingCenterOnScreen(id) ?: return
        siteMapView.pivotX = center.x
        siteMapView.pivotY = center.y
        siteMapView.animate()
            .scaleX(zoomScale)
            .scaleY(zoomScale)
            .setDuration(animDuration)
            .start()
    }

    private fun resetZoom() {
        siteMapView.animate()
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(animDuration)
            .start()
    }
}