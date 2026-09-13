//colors;
//it - gray
//hm - orange
//tm - yellow
//bse - blue
//canteens kakasjay - green
//faculties - darker color depende no niya department
//library, fishfishes, courts, dean, hostel, student center, ssc - red
//parkingan - brown
//balay ti uttogs - pink
//reserve-able lang jay it buildings, dadduma macklick ngem info lang agparang
//pag joinen yunto man agijay buildings ta maifill colors tu langnen

package com.example.dbtest.map

import android.graphics.Color
import android.graphics.PointF

data class Building(
    val id: String,
    val label: String,
    val points: List<PointF>,
    // Null = use SiteMapView's default fill color. Set this to give a
    // building/room group its own color (e.g. IT buildings are gray).
    val fillColor: Int? = null,
    // Which floor this room/building belongs to. Most entries are floor 1
    // (ground floor) by default; a handful of rooms only exist on floor 2
    // (see CampusBuildings.SECOND_FLOOR below).
    val floor: Int = 1,
    // Whether this building should still be drawn (faded) as a ghost/reference
    // overlay when the OTHER floor is active. Defaults to true, which is right
    // for rooms whose footprint matches 1:1 across floors. Set false for rooms
    // like the ground-floor IT-1A split (building_13a_gf1/gf2) whose footprint
    // is merged into a single differently-shaped room on the other floor -
    // ghosting those draws a stray dividing line/color blend over the merged
    // room instead of a useful reference outline.
    val showOnOtherFloor: Boolean = true,
    // Whether this room responds to taps/selection at all. Defaults to true.
    // Set false for non-room spaces like storage/utility nooks that are drawn
    // for visual completeness but shouldn't be selectable (e.g. the two
    // "X" storage rooms flanking the Training Center's middle block).
    val clickable: Boolean = true,
    // Whether this building/room can be reserved. Defaults to false - most
    // buildings are just tappable for info (name shown in the slider panel,
    // no Reserve button). Only BSIT (IT), BSE, and BSHM (HM) department
    // rooms are reservable, per request.
    val reservable: Boolean = false
)


object CampusBuildings {

    // Bumped up from the default alpha (90) so IT buildings read as clearly
    // gray instead of a faint tint. SiteMapView auto-darkens this when an
    // IT building is pressed/selected, so no separate "pressed" color needed.
    private val IT_GRAY = Color.argb(170, 105, 105, 105)

    // Same treatment for the HM building group, per the "hm - orange" scheme
    // noted at the top of this file. Matches hex #FF7900.
    private val HM_ORANGE = Color.argb(170, 255, 121, 0)

    // Same treatment for the TM building group, per the "tm - yellow" scheme
    // noted at the top of this file.
    private val TM_YELLOW = Color.argb(170, 255, 235, 59)

    // Same treatment for the BSE building group, per the "bse - blue" scheme
    // noted at the top of this file. Alpha was 0 (fully invisible) before -
    // bumped to 170 to match the other groups so these rooms actually render.
    private val BSE_BLUE = Color.argb(170, 33, 150, 243)

    // Per the "canteens kakasjay - green" scheme noted at the top of this file.
    private val CANTEEN_GREEN = Color.argb(170, 76, 175, 80)

    // Per the "library, fishfishes, courts, dean, hostel, student center,
    // ssc - red" scheme noted at the top of this file. Also covers CR
    // (comfort room) entries per request - all CRs are red.
    private val LANDMARK_RED = Color.argb(170, 244, 67, 54)

    // Parking area's own color, matching the pathway's beige tone per
    // request - no longer using the general "brown" that now applies to
    // every uncolored building below.
    private val PARKING_AREA_COLOR = Color.rgb(235, 231, 191)

    // Per request: every building that previously had no fillColor (was
    // relying on SiteMapView's default purple tint) is now brown, matching
    // the "parkingan - brown" scheme's color noted at the top of this file
    // (hex #795C34) - repurposed here as the catch-all default.
    private val UNCOLORED_BROWN = Color.argb(170, 121, 92, 52)

    // Per the "balay ti uttogs - pink" scheme noted at the top of this file.
    // Matches hex #FFC0CB.
    private val GUARD_PINK = Color.argb(170, 255, 192, 203)

    //bot right, bot left, top left, top right
    val OTHER_BUILDINGS: List<Building> = listOf(
        Building("building_1", "CR", pts(247,156, 226,156, 226,132, 247,132), fillColor = LANDMARK_RED),
        Building("building_2", "CR", pts(437,99, 463,98, 464,134, 438,135), fillColor = LANDMARK_RED),
        Building("building_3a", "SSC Building", pts(556,89, 474,88, 473,128, 558,129), fillColor = LANDMARK_RED),
        Building("building_3b", "Chapel", pts(594,45, 549,51, 560,108, 564,125, 608,118), fillColor = UNCOLORED_BROWN),
        // Training Center, split into rooms per the hand-drawn floor plan:
        // a top row of 3 rooms, then a bottom band with a big middle block
        // (split into an upper room + two lower rooms) flanked by two
        // storage/utility rooms (the ones marked "X" - not clickable).
        Building("building_4a", "Training Center - Room 1", pts(619,74, 618,42, 664,40, 665,72), fillColor = UNCOLORED_BROWN),
        Building("building_4b", "Training Center - Room 2", pts(665,72, 664,40, 711,39, 712,70), fillColor = UNCOLORED_BROWN),
        Building("building_4c", "Training Center - Room 3", pts(712,70, 711,39, 757,37, 758,68), fillColor = UNCOLORED_BROWN),
        Building("building_4d", "Training Center - Storage 1", pts(621,147, 619,74, 659,72, 662,146), fillColor = UNCOLORED_BROWN, clickable = false),
        // Storage 2 shrunk to the top portion only (matching Room 4's height,
        // y~70-113) - it used to run the full column height, but per the
        // hand-drawn plan there's a room below it, next to Room 6, that was
        // missing. See building_4i/4j below for that room.
        Building("building_4e", "Training Center - Storage 2", pts(760,112, 729,113, 728,70, 758,68), fillColor = UNCOLORED_BROWN, clickable = false),
        Building("building_4f", "Training Center - BSE", pts(661,114, 659,72, 728,70, 729,112), fillColor = BSE_BLUE, reservable = true),
        Building("building_4g", "Training Center - Room 5", pts(662,146, 661,114, 695,113, 696,144), fillColor = UNCOLORED_BROWN),
        Building("building_4h", "Training Center - Room 6", pts(696,144, 695,113, 729,112, 730,143), fillColor = UNCOLORED_BROWN),
        // New room next to Room 6, directly below the (now-shortened) Storage 2
        // X. Kept as a single room (not split) per request.
        Building("building_4i", "Training Center - Room 7", pts(760,142, 729,143, 729,112, 760,112), fillColor = UNCOLORED_BROWN),
        Building("building_5", "Fishery", pts(993,147, 909,147, 909,44, 993,44), fillColor = LANDMARK_RED),
        // Registrar's top edge nudged from y139 -> y147 to stop it overlapping Fishery
        // (they shared x909-928, y139-147 before this fix).
        Building("building_8", "Registrar", pts(928,218, 867,218, 867,147, 928,147), fillColor = UNCOLORED_BROWN),
        Building("building_9", "Parking Area", pts(1263,222, 1088,222, 1088,107, 1263,107), fillColor = PARKING_AREA_COLOR),
        Building("building_10", "Gray gym", pts(134,327, 17,327, 17,158, 134,158), fillColor = UNCOLORED_BROWN),
        Building("building_11", "Open Court", pts(365,346, 265,346, 265,194, 365,194), fillColor = LANDMARK_RED),
        Building("building_14", "Canteen 2", pts(795,270, 757,269, 758,241, 796,242), fillColor = CANTEEN_GREEN),
        Building("building_15", "Canteen 3", pts(835,266, 807,266, 807,246, 835,246), fillColor = CANTEEN_GREEN),
        // Building 16 shifted +5 in x (830->835, 855->860) so its left edge meets
        // Building 15's right edge (835) instead of overlapping it by 5px.
        Building("building_16", "Printing Services", pts(860,264, 835,264, 835,245, 860,245), fillColor = CANTEEN_GREEN),
        // Resized to exactly match Building 16's footprint (25 x 19),
        // kept centered on Building 17's original spot (old center was
        // ~885.5, 264 - the original corners were 866,294 / 863,236 /
        // 905,234 / 908,292).
        Building("building_17", "Canteen 4", pts(898,274, 873,274, 873,255, 898,255), fillColor = CANTEEN_GREEN),
        // New building the same size Building 17 used to be (45 x 60),
        // centered between Building 17's old center and the Hostel's center.
        Building("building_27", "HM Building", pts(975,313, 930,313, 930,253, 975,253), fillColor = HM_ORANGE, reservable = true),
        Building("building_20", "School Van Parking Area", pts(750,356, 713,356, 713,325, 750,325), fillColor = PARKING_AREA_COLOR),
        Building("building_21", "Canteen 1", pts(814,330, 778,330, 778,283, 814,283), fillColor = CANTEEN_GREEN),
        Building("building_22", "Hostel", pts(1002,335, 999,271, 1037,270, 1040,334), fillColor = LANDMARK_RED, floor = 2),
        // Ground-floor duplicate of building_22 above, per request: floor 1 should
        // also show the floor-2 buildings (same shape/spot), just with a different
        // id so it doesn't collide with the floor=2 original.
        Building("building_22_gf", "Hostel", pts(1002,335, 999,271, 1037,270, 1040,334), fillColor = LANDMARK_RED),
        Building("building_23", "Guard's House Main", pts(1206,383, 1178,383, 1178,356, 1206,356), fillColor = GUARD_PINK),
        Building("building_25", "Guard's House", pts(666,492, 646,492, 646,471, 666,471), fillColor = GUARD_PINK),
        Building("building_26", "Ancient", pts(763,464, 679,464, 679,402, 763,402), fillColor = UNCOLORED_BROWN),
    )

    // -----------------------------------------------------------------
    // IT BUILDING COMPLEX - kept separate from OTHER_BUILDINGS above so
    // its polygons/labels are easy to find and debug on their own.
    // Building 12 + 13 = "IT Building 1" (rooms 1A/1B, comp lab, internet
    // room). Building 19 = "IT Building 2" (rooms 2A/2B, faculty).
    // All filled gray to match the "it - gray" scheme noted above.
    // -----------------------------------------------------------------
    val IT_BUILDINGS: List<Building> = listOf(
        // IT Building 1 (Building 12 side)
        Building("building_12a", "IT 1B Room", pts(427,218, 427,181, 473,181, 474,218), fillColor = IT_GRAY, reservable = true),

        // IT Building 1 (Building 13 side) - all on the 2nd floor
        Building("building_13a", "IT 1A Room", pts(538,198, 615,198, 615,229, 538,229), fillColor = IT_GRAY, floor = 2, reservable = true),
        Building("building_13b", "Computer Lab/IT4", pts(587,229, 615,229, 615,280, 587,280), fillColor = IT_GRAY, floor = 2, reservable = true),
        Building("building_13c", "Internet Room/IT3", pts(587,280, 615,280, 615,330, 587,330), fillColor = IT_GRAY, floor = 2, reservable = true),

        // Ground-floor duplicates of the three 2nd-floor IT rooms above, same
        // shapes/labels, just renamed ids so floor 1 also shows them (see the
        // Hostel duplicate above for why). IT 1A Room used to be split here too,
        // but those two halves are now HM rooms - see building_13a_gf1/gf2 in
        // HM_BUILDINGS below. Keeping them here too would double-draw them
        // (gray under orange), which is what caused the off-shade orange.
        Building("building_13b_gf", "Computer Lab/IT4", pts(587,229, 615,229, 615,280, 587,280), fillColor = IT_GRAY, reservable = true),
        Building("building_13c_gf", "Internet Room/IT3", pts(587,280, 615,280, 615,330, 587,330), fillColor = IT_GRAY, reservable = true),

        // IT Building 2 (Building 19)
        Building("building_19a", "IT 2B Room", pts(533,373, 533,340, 570,338, 570,373), fillColor = IT_GRAY, reservable = true),
        Building("building_19b", "IT 2A Room", pts(570,372, 570,338, 610,338, 610,372), fillColor = IT_GRAY, reservable = true),
        Building("building_19c", "IT Faculty", pts(611,372, 611,347, 637,349, 639,371), fillColor = IT_GRAY),
    )

    // -----------------------------------------------------------------
    // HM BUILDING GROUP - separated out the same way as IT_BUILDINGS,
    // filled orange per the "hm - orange" scheme noted at the top.
    // -----------------------------------------------------------------
    val HM_BUILDINGS: List<Building> = listOf(
        // Bottom edge pulled up from y195 -> y180 (15 shorter) to open a
        // pathway between this room and building_13a/13a_gf1/13a_gf2 below
        // it, which start at y198 - was nearly touching before.
        Building("building_7", "HM Room 1", pts(590,180, 524,180, 524,150, 590,150), fillColor = HM_ORANGE, reservable = true),
        Building("building_12b", "HM Room 2", pts(427,255, 427,218, 474,218, 474,255), fillColor = HM_ORANGE, reservable = true),
        Building("building_12c", "HM Room 3", pts(427,293, 427,255, 474,255, 475,293), fillColor = HM_ORANGE, reservable = true),
        Building("building_12d", "HM Room 4", pts(427,330, 427,293, 475,293, 475,330), fillColor = HM_ORANGE, reservable = true),
        // showOnOtherFloor = false: floor 2 has this footprint as one merged
        // room (building_13a, IT 1A Room), not two - ghosting these split
        // halves onto floor 2 was drawing a stray dividing line/color blend
        // across that merged room.
        Building("building_13a_gf1", "HM Room 5", pts(538,198, 577,198, 577,229, 538,229), fillColor = HM_ORANGE, showOnOtherFloor = false, reservable = true),
        Building("building_13a_gf2", "HM Room 6", pts(577,198, 615,198, 615,229, 577,229), fillColor = HM_ORANGE, showOnOtherFloor = false, reservable = true),
        Building("building_12e", "Faculty", pts(427,368, 427,330, 475,330, 476,368), fillColor = HM_ORANGE),

        // Ground-floor duplicates of South Building - same shapes/labels,
        // just renamed ids, so floor 1 shows these too (same treatment as the
        // Hostel/IT duplicates above; South is no longer excluded).
        Building("building_24a_gf", "South - CR 1", pts(333,476, 308,476, 308,416, 333,416), fillColor = LANDMARK_RED),
        Building("building_24b_gf", "South - Room 1", pts(389,476, 333,476, 333,416, 389,416), fillColor = HM_ORANGE, reservable = true),
        Building("building_24c_gf", "South - Room 2", pts(445,476, 389,476, 389,416, 445,416), fillColor = HM_ORANGE, reservable = true),
        Building("building_24d_gf", "South - Room 3", pts(502,476, 445,476, 445,416, 502,416), fillColor = HM_ORANGE, reservable = true),
        Building("building_24e_gf", "South - Room 4", pts(558,476, 502,476, 502,416, 558,416), fillColor = HM_ORANGE, reservable = true),
        Building("building_24f_gf", "South - CR 2", pts(583,476, 558,476, 558,416, 583,416), fillColor = LANDMARK_RED),
    )

    // -----------------------------------------------------------------
    // TM BUILDING GROUP - separated out the same way as IT_BUILDINGS,
    // filled yellow per the "tm - yellow" scheme noted at the top.
    // Building 6 = the TM room cluster. Building 18's rooms 5/6 moved to
    // BSE_BUILDINGS below (see BSE note there) - keeping them here too
    // was the duplicate-id/double-draw bug.
    // -----------------------------------------------------------------
    val TM_BUILDINGS: List<Building> = listOf(
        // Building 6 side (Room 1's left edge nudged from x240 -> x247 so it
        // stops overlapping the CR at building_1, which ends at x247).
        Building("building_6a", "TM - Room 1", pts(283,176, 247,176, 247,131, 283,131), fillColor = TM_YELLOW, reservable = true),
        Building("building_6b", "TM - Room 2", pts(325,176, 283,176, 283,131, 325,131), fillColor = TM_YELLOW, reservable = true),
        Building("building_6c", "TM - Room 3", pts(368,176, 325,176, 325,131, 368,131), fillColor = TM_YELLOW, reservable = true),

        // Building 18 side
        Building("building_18a", "TM - Room 4", pts(163,294, 162,255, 206,255, 207,294), fillColor = TM_YELLOW, reservable = true),

        Building("building_24a", "South - CR 1", pts(333,476, 308,476, 308,416, 333,416), fillColor = LANDMARK_RED, floor = 2),
        Building("building_24b", "South - Room 1", pts(389,476, 333,476, 333,416, 389,416), fillColor = TM_YELLOW, floor = 2, reservable = true),
        Building("building_24c", "South - Room 2", pts(445,476, 389,476, 389,416, 445,416), fillColor = TM_YELLOW, floor = 2, reservable = true),
        Building("building_24d", "South - Room 3", pts(502,476, 445,476, 445,416, 502,416), fillColor = TM_YELLOW, floor = 2, reservable = true),
        Building("building_24e", "South - Room 4", pts(558,476, 502,476, 502,416, 558,416), fillColor = TM_YELLOW, floor = 2, reservable = true),
        Building("building_24f", "South - CR 2", pts(583,476, 558,476, 558,416, 583,416), fillColor = LANDMARK_RED, floor = 2),
    )

    // -----------------------------------------------------------------
    // BSE BUILDING GROUP - filled blue per the "bse - blue" scheme noted
    // at the top. Building 18's rooms 5/6 moved here from TM_BUILDINGS.
    // -----------------------------------------------------------------
    val BSE_BUILDINGS: List<Building> = listOf(
        Building("building_18b", "BSE - Room 5", pts(163,334, 163,294, 207,294, 208,334), fillColor = BSE_BLUE, reservable = true),
        Building("building_18c", "BSE - Room 6", pts(164,373, 163,334, 208,334, 209,373), fillColor = BSE_BLUE, reservable = true),
    )

    val ALL: List<Building> = OTHER_BUILDINGS + IT_BUILDINGS + HM_BUILDINGS + TM_BUILDINGS + BSE_BUILDINGS

    // -----------------------------------------------------------------
    // FLOOR TOGGLE - ground floor is everything NOT marked floor=2. The 2nd
    // floor view is IT 1A Room, Computer Lab/IT4, Internet Room/IT3, Hostel,
    // and all South rooms (the entries marked floor=2 above). All of these
    // also have a floor=1 "_gf" duplicate further up so they show on the
    // ground floor too. These two lists are no longer used for filtering
    // what's drawn (SiteMapView.activeFloor now handles that by dimming the
    // inactive floor instead of hiding it) - kept for reference.
    // -----------------------------------------------------------------
    val GROUND_FLOOR: List<Building> = ALL.filter { it.floor != 2 }
    val SECOND_FLOOR: List<Building> = ALL.filter { it.floor == 2 }
}

// pts() is used above by CampusBuildings.
private fun pts(vararg v: Int): List<PointF> {
    val list = mutableListOf<PointF>()
    var i = 0
    while (i < v.size) {
        list.add(PointF(v[i].toFloat(), v[i + 1].toFloat()))
        i += 2
    }
    return list
}