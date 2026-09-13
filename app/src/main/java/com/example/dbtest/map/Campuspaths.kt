package com.example.dbtest.map

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF

// A single walkway/corridor, drawn as a connected polyline (not a filled
// shape like Building). Traced from the hand-drawn "pathway" reference
// image and mapped onto the same coordinate system CampusBuildings uses,
// so these line up against the real building polygons without extra
// scaling/offsetting on your end.
data class CampusPath(
    val id: String,
    val points: List<PointF>
)

object CampusPaths {

    // Sampled from the reference image's tan/khaki pathway color.
    // Full alpha since it's meant to read as solid ground, drawn UNDER
    // the buildings (see drawPaths note below).
    private val PATH_COLOR = Color.rgb(235, 231, 191)

    // Thick + rounded to get the same soft, hand-drawn corridor look as
    // the reference image instead of a thin technical line.
    private const val PATH_STROKE_WIDTH = 18f

    val ALL: List<CampusPath> = listOf(
        // Long east-west spine running below the main building row, from
        // the Gray gym on the far left to the far-right cluster.
        CampusPath("spine_main", pts(63,375, 1297,375)),

        // Short spur connecting the spine up to the Gray gym's bottom edge.
        CampusPath("spur_gym", pts(69,375, 69,327)),

        // Corridor threading up through the actual gap between Open Court
        // (right edge x365) and the IT/HM room column (left edge x427),
        // then along the narrow open strip above HM Room 1 / the IT-1A
        // rooms, back down the gap on the far side of that column. Routed
        // through real empty space instead of a filled block, so it never
        // sits on top of a building's interior.
        CampusPath("loop_left", pts(191,375, 191,150)),
        CampusPath("loop_top", pts(191,150, 717,150)),
        CampusPath("loop_right", pts(717,150, 717,375)),

        // Spur down to the South Building row, with a small jog right to
        // the little door/square next to it.
        CampusPath("spur_south", pts(674,375, 674,429, 717,429, 717,485)),

        // Upper corridor across the right-hand cluster (Registrar / Building
        // 17 / small rooms / the highlighted building), plus its two legs
        // down to the main spine.
        CampusPath("corridor_right_upper", pts(686,247, 1230,247)),
        CampusPath("spur_right_down_left", pts(686,247, 686,375)),
        CampusPath("spur_right_down_right", pts(1230,247, 1230,375)),

        // Short spur up to the small building sitting above the right
        // corridor (around Registrar/Building 17).
        CampusPath("spur_building_upper_right", pts(925,247, 925,128)),

        // Small staircase jog that routes around the highlighted purple
        // building before rejoining the main spine.
        CampusPath("jog_around_building", pts(1029,247, 1029,309, 1084,309, 1084,375)),
    )

    /**
     * Draws every corridor as a thick, rounded stroke.
     *
     * Call this BEFORE you draw CampusBuildings.ALL in your onDraw, so the
     * pathways sit underneath the buildings like ground/walkways rather
     * than drawing on top of them.
     *
     * e.g. in your custom View's onDraw(canvas):
     *   CampusPaths.draw(canvas)
     *   CampusBuildings.ALL.forEach { drawBuilding(canvas, it) }
     */
    fun draw(canvas: Canvas, paint: Paint = defaultLinePaint()) {
        for (campusPath in ALL) {
            val pts = campusPath.points
            if (pts.size < 2) continue
            val path = Path()
            path.moveTo(pts[0].x, pts[0].y)
            for (i in 1 until pts.size) {
                path.lineTo(pts[i].x, pts[i].y)
            }
            canvas.drawPath(path, paint)
        }
    }

    private fun defaultLinePaint(): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = PATH_COLOR
        style = Paint.Style.STROKE
        strokeWidth = PATH_STROKE_WIDTH
        strokeCap = Paint.Cap.ROUND
        strokeJoin = Paint.Join.ROUND
    }

    private fun pts(vararg v: Int): List<PointF> {
        val list = mutableListOf<PointF>()
        var i = 0
        while (i < v.size) {
            list.add(PointF(v[i].toFloat(), v[i + 1].toFloat()))
            i += 2
        }
        return list
    }
}