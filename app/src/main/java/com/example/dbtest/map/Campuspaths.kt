package com.example.dbtest.map

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF

// -----------------------------------------------------------------
// PATHWAY - traced from the reference image (tan/khaki walkway color,
// RGB 236,231,192), then rescaled from the image's native pixel space
// into the SAME 17-1329 x / 37-529 y design space that CampusBuildings
// uses, so the walkway lines up under the actual building polygons
// instead of floating off in a different scale.
//
// Outline is the outer boundary of the walkway network; HOLES are the
// building footprints / open ground that sit inside that boundary and
// must be cut out. Drawn as ONE FILLED Path using EVEN_ODD fill rule
// (outline + holes all added to the same path), which is what gives
// it the solid, blocky "paved walkway" look instead of thin pointed
// line segments.
// -----------------------------------------------------------------
object Pathway {

    val COLOR: Int = Color.argb(255, 236, 231, 192)

    val OUTLINE: List<PointF> = pts(
        17,291, 28,381, 668,381, 668,414, 592,414, 592,434, 669,434, 677,529,
        680,373, 1329,378, 1285,356, 1278,165, 972,160, 971,82, 1004,69,
        968,72, 956,158, 770,155, 778,87, 619,69, 618,37, 489,47, 485,80,
        438,55, 437,82, 354,104, 223,104, 204,80, 184,102, 188,352, 70,352, 58,288
    )

    val HOLES: List<List<PointF>> = listOf(
        pts(
            667,313, 694,313, 695,314, 695,325, 693,326, 693,356, 693,357,
            665,357, 664,356, 664,334, 665,333, 666,333, 666,314
        ),
        pts(
            525,301, 530,300, 531,297, 609,297, 610,304, 613,307, 625,308,
            625,310, 637,310, 638,313, 648,313, 649,353, 646,357, 535,357,
            534,352, 532,351, 532,344, 525,343
        ),
        pts(
            1168,279, 1168,345, 1165,346, 1164,359, 978,358, 977,326, 980,324,
            1014,322, 1082,326, 1084,265, 1118,267, 1119,270, 1134,270,
            1134,273, 1164,275
        ),
        pts(
            966,274, 965,359, 926,359, 925,353, 908,357, 719,356, 715,281,
            738,280, 744,273, 784,273, 784,255, 772,254, 772,236, 784,235,
            785,230, 826,230, 827,235, 890,232, 891,224, 916,224, 917,213,
            942,214, 943,273
        ),
        pts(946,194, 995,194, 996,195, 996,270, 995,271, 946,271, 945,270, 945,195),
        pts(1100,175, 1270,174, 1273,356, 1183,359, 1182,253, 1106,248),
        pts(
            973,171, 1093,172, 1094,245, 1092,248, 1077,252, 1075,257,
            1063,260, 1064,265, 1076,265, 1077,269, 1076,316, 1006,318,
            1004,314, 976,317, 975,274, 998,273, 998,193, 973,193
        ),
        pts(
            962,172, 962,192, 961,193, 943,193, 943,206, 942,207, 925,207,
            921,206, 917,206, 916,205, 916,195, 891,195, 890,194, 890,188,
            892,187, 892,175, 893,174, 947,174, 947,171, 961,171
        ),
        pts(
            762,180, 778,179, 778,172, 781,169, 877,171, 878,174, 886,175,
            884,209, 885,209, 886,194, 890,194, 891,197, 914,197, 915,222,
            884,225, 883,213, 874,215, 873,218, 868,220, 763,218
        ),
        pts(531,127, 536,127, 537,124, 652,125, 646,291, 583,290, 583,172, 531,168),
        pts(
            208,124, 236,116, 399,123, 399,352, 322,354, 313,343, 310,311,
            289,314, 282,357, 207,351
        ),
        pts(
            465,101, 468,174, 468,328, 472,340, 460,355, 421,353, 418,351,
            418,336, 414,335, 414,107, 422,106, 423,103
        ),
        pts(514,69, 515,68, 596,68, 597,69, 597,106, 590,107, 589,110, 515,110, 514,109)
    )

    /**
     * Builds one Path: the outer boundary + every hole subpath, using
     * EVEN_ODD fill so the holes render as gaps in the filled tan shape.
     */
    fun buildPath(): Path {
        val path = Path()
        path.fillType = Path.FillType.EVEN_ODD

        addSubPath(path, OUTLINE)
        for (hole in HOLES) {
            addSubPath(path, hole)
        }
        return path
    }

    private fun addSubPath(path: Path, points: List<PointF>) {
        if (points.isEmpty()) return
        path.moveTo(points[0].x, points[0].y)
        for (i in 1 until points.size) {
            path.lineTo(points[i].x, points[i].y)
        }
        path.close()
    }

    /**
     * Draws the pathway as a solid FILLED shape (not a stroked line),
     * which is what makes it look like a paved walkway rather than a
     * series of pointed line segments. Call this BEFORE drawing your
     * buildings so the walkway sits underneath them.
     */
    fun draw(canvas: Canvas, paint: Paint = defaultFillPaint()) {
        canvas.drawPath(buildPath(), paint)
    }

    private fun defaultFillPaint(): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = COLOR
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
    }
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