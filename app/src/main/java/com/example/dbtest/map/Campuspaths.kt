package com.example.dbtest.map

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.CornerPathEffect
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF

// -----------------------------------------------------------------
// PATHWAY - built from a corridor "skeleton" (centerline segments
// connecting building clusters), buffered to a uniform road width and
// unioned into one shape. Every segment goes somewhere specific - no
// dead-end spurs poking into open ground with nothing to walk to.
//
// Outline is the outer boundary of the walkway network; HOLES are the
// courtyard/open-ground gaps enclosed by corridor loops. Drawn as ONE
// FILLED Path using EVEN_ODD fill rule (outline + holes all added to
// the same path), which gives the solid, blocky "paved walkway" look.
//
// A CornerPathEffect rounds every vertex so the walkway reads as a
// real paved path (soft corners), and a thin darker border stroke is
// drawn on top of the fill so the path edge stays crisp/defined even
// after rounding.
// -----------------------------------------------------------------
object Pathway {

    val COLOR: Int = Color.argb(255, 236, 231, 192)
    val BORDER_COLOR: Int = Color.argb(255, 200, 193, 150)

    private const val CORNER_RADIUS = 10f

    val OUTLINE: List<PointF> = pts(
        613,135, 615,131, 614,127, 612,124, 608,123, 506,123, 500,121, 494,123,
        437,123, 432,125, 430,130, 431,134, 436,137, 491,137, 491,169, 243,169,
        242,153, 240,150, 236,149, 232,150, 230,153, 229,169, 161,169, 161,103,
        158,97, 154,95, 146,95, 141,99, 139,105, 139,379, 17,379, 9,385,
        7,392, 8,397, 13,403, 20,405, 441,405, 442,423, 445,427, 450,429,
        455,427, 458,423, 459,405, 662,405, 662,480, 664,486, 670,488, 676,486,
        678,480, 678,405, 761,405, 761,470, 764,474, 770,477, 776,474, 779,470,
        779,405, 1320,405, 1327,403, 1332,397, 1333,389, 1331,385, 1323,379, 1159,379,
        1159,376, 1227,376, 1232,372, 1233,368, 1232,364, 1227,360, 1159,360, 1159,186,
        1161,180, 1159,174, 1159,107, 1157,102, 1153,99, 1148,98, 1145,100, 1141,105,
        1141,169, 943,169, 943,145, 939,140, 935,139, 931,140, 927,145, 927,169,
        686,169, 686,144, 682,139, 678,138, 674,139, 670,144, 670,169, 509,169,
        509,137, 608,137
    )

    val HOLES: List<List<PointF>> = listOf(
        pts(1029,379, 1029,244, 1141,244, 1141,379),
        pts(779,379, 779,289, 1011,289, 1011,379),
        pts(
            584,379, 584,191, 761,191, 761,226, 752,228, 750,230, 748,235, 750,240,
            752,242, 761,244, 761,379
        ),
        pts(927,191, 927,226, 779,226, 779,191),
        pts(1011,191, 1011,226, 943,226, 943,191),
        pts(1141,191, 1141,226, 1029,226, 1029,191),
        pts(371,379, 161,379, 161,191, 371,191),
        pts(566,379, 389,379, 389,191, 566,191),
        pts(
            945,253, 947,258, 953,260, 958,256, 959,244, 1011,244, 1011,271, 779,271,
            779,244, 945,244
        )
    )

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

    fun draw(canvas: Canvas, paint: Paint = defaultFillPaint(), borderPaint: Paint = defaultBorderPaint()) {
        val path = buildPath()
        canvas.drawPath(path, paint)
        canvas.drawPath(path, borderPaint)
    }

    private fun defaultFillPaint(): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = COLOR
        style = Paint.Style.FILL
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        pathEffect = CornerPathEffect(CORNER_RADIUS)
    }

    private fun defaultBorderPaint(): Paint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = BORDER_COLOR
        style = Paint.Style.STROKE
        strokeWidth = 2f
        strokeJoin = Paint.Join.ROUND
        strokeCap = Paint.Cap.ROUND
        pathEffect = CornerPathEffect(CORNER_RADIUS)
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