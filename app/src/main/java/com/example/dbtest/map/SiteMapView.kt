package com.example.dbtest.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.PointF
import android.graphics.Rect
import android.graphics.Region
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewConfiguration
import kotlin.math.atan2
import kotlin.math.hypot


class SiteMapView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {


    var designWidth: Float = 700f
    var designHeight: Float = 1000f

    var buildings: List<Building> = emptyList()
        set(value) {
            field = value
            rebuildPaths()
            invalidate()
        }

    var backgroundBitmap: Bitmap? = null
        set(value) {
            field = value
            invalidate()
        }


    var onBuildingClick: ((Building) -> Unit)? = null

    var onBuildingSelected: ((Building?) -> Unit)? = null

    var minZoom = 0.75f
    var maxZoom = 6f

    var panEnabled = true

    var zoomEnabled = true

    var rotateEnabled = true


    // Default fill color used by any building that doesn't set its own Building.fillColor.
    private var defaultFillColor = Color.argb(90, 187, 134, 252) // subtle purple_200 tint, matches app theme

    private val fillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = defaultFillColor
    }
    private var defaultPressedFillColor = Color.argb(160, 98, 0, 238) // purple_500 highlight while pressed/selected

    private val pressedFillPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = defaultPressedFillColor
    }
    private val strokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 2f
        color = Color.argb(200, 0, 0, 0)
    }
    private val selectedStrokePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.STROKE
        strokeWidth = 3f
        color = Color.rgb(98, 0, 238)
    }
    private val bitmapPaint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)


    fun setBuildingColors(
        fill: Int? = null,
        pressedFill: Int? = null,
        stroke: Int? = null,
        selectedStroke: Int? = null
    ) {
        fill?.let { defaultFillColor = it }
        pressedFill?.let { defaultPressedFillColor = it }
        stroke?.let { strokePaint.color = it }
        selectedStroke?.let { selectedStrokePaint.color = it }
        invalidate()
    }


    private data class Entry(val building: Building, val path: Path, val region: Region, val bounds: RectF)

    private var entries: List<Entry> = emptyList()
    private var pressedId: String? = null
    private var selectedId: String? = null

    private val designClipRect = Rect()

    init {
        isClickable = true
        isFocusable = true
        contentDescription = "Site map"
    }

    private fun rebuildPaths() {
        designClipRect.set(0, 0, designWidth.toInt(), designHeight.toInt())
        entries = buildings.map { b ->
            val path = Path()
            b.points.forEachIndexed { i, p ->
                if (i == 0) path.moveTo(p.x, p.y) else path.lineTo(p.x, p.y)
            }
            path.close()

            val region = Region()
            region.setPath(path, Region(designClipRect))

            val bounds = RectF()
            path.computeBounds(bounds, true)

            Entry(b, path, region, bounds)
        }
    }


    private val baseMatrix = Matrix()
    private val mapMatrix = Matrix()
    private val inverseMatrix = Matrix()
    private var baseFitScale = 1f

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        val scaleX = w / designWidth
        val scaleY = h / designHeight

        baseFitScale = minOf(scaleX, scaleY)
        val offsetX = (w - designWidth * baseFitScale) / 2f
        val offsetY = (h - designHeight * baseFitScale) / 2f

        baseMatrix.reset()
        baseMatrix.postScale(baseFitScale, baseFitScale)
        baseMatrix.postTranslate(offsetX, offsetY)


        if (mapMatrix.isIdentity) {
            mapMatrix.set(baseMatrix)
        }
        updateInverse()
    }

    private fun updateInverse() {
        mapMatrix.invert(inverseMatrix)
    }


    fun resetView() {
        mapMatrix.set(baseMatrix)
        updateInverse()
        invalidate()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val desiredHeight = (widthSize * (designHeight / designWidth)).toInt()
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        val finalHeight = when (heightMode) {
            MeasureSpec.EXACTLY -> heightSize
            MeasureSpec.AT_MOST -> minOf(desiredHeight, heightSize)
            else -> desiredHeight
        }
        setMeasuredDimension(widthSize, finalHeight)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.save()
        canvas.concat(mapMatrix)

        backgroundBitmap?.let { bmp ->
            val src = Rect(0, 0, bmp.width, bmp.height)
            val dst = RectF(0f, 0f, designWidth, designHeight)
            canvas.drawBitmap(bmp, src, dst, bitmapPaint)
        }

        for (e in entries) {
            val isPressed = e.building.id == pressedId
            val isSelected = e.building.id == selectedId
            if (isPressed || isSelected) {
                pressedFillPaint.color = e.building.fillColor?.let { darkenColor(it) } ?: defaultPressedFillColor
                canvas.drawPath(e.path, pressedFillPaint)
                canvas.drawPath(e.path, selectedStrokePaint)
            } else {
                fillPaint.color = e.building.fillColor ?: defaultFillColor
                canvas.drawPath(e.path, fillPaint)
                canvas.drawPath(e.path, strokePaint)
            }
        }

        canvas.restore()
    }

    // Multiplies RGB toward black (keeping alpha, boosted slightly) to get a
    // "pressed" shade of a building's own fillColor, e.g. gray -> darker gray.
    private fun darkenColor(color: Int, factor: Float = 0.55f): Int {
        val a = (Color.alpha(color) * 1.3f).toInt().coerceIn(0, 255)
        val r = (Color.red(color) * factor).toInt().coerceIn(0, 255)
        val g = (Color.green(color) * factor).toInt().coerceIn(0, 255)
        val b = (Color.blue(color) * factor).toInt().coerceIn(0, 255)
        return Color.argb(a, r, g, b)
    }

    private fun screenToDesign(x: Float, y: Float): PointF {
        val pts = floatArrayOf(x, y)
        inverseMatrix.mapPoints(pts)
        return PointF(pts[0], pts[1])
    }

    private fun findBuildingAt(viewX: Float, viewY: Float): Entry? {
        val p = screenToDesign(viewX, viewY)
        // iterate in reverse so buildings drawn last (on top) win hit-testing ties
        for (e in entries.asReversed()) {
            if (e.bounds.contains(p.x, p.y) && e.region.contains(p.x.toInt(), p.y.toInt())) {
                return e
            }
        }
        return null
    }


    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    private val scaleGestureDetector = ScaleGestureDetector(context,
        object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
            override fun onScale(detector: ScaleGestureDetector): Boolean {
                if (!zoomEnabled) return true
                var factor = detector.scaleFactor
                val currentScale = currentMapScale()
                val newScale = currentScale * factor
                // Clamp so pinching past the min/max zoom doesn't keep scaling.
                val clamped = newScale.coerceIn(baseFitScale * minZoom, baseFitScale * maxZoom)
                if (currentScale != 0f) factor = clamped / currentScale
                mapMatrix.postScale(factor, factor, detector.focusX, detector.focusY)
                updateInverse()
                invalidate()
                return true
            }
        })


    private fun currentMapScale(): Float {
        val values = FloatArray(9)
        mapMatrix.getValues(values)
        return hypot(values[Matrix.MSCALE_X].toDouble(), values[Matrix.MSKEW_Y].toDouble()).toFloat()
    }


    private var lastTouchX = 0f
    private var lastTouchY = 0f
    private var isDragging = false


    private var rotatePointerId1 = -1
    private var rotatePointerId2 = -1
    private var previousAngle = 0f

    private fun angleBetween(e: MotionEvent, id1: Int, id2: Int): Float {
        val i1 = e.findPointerIndex(id1)
        val i2 = e.findPointerIndex(id2)
        val dx = e.getX(i1) - e.getX(i2)
        val dy = e.getY(i1) - e.getY(i2)
        return Math.toDegrees(atan2(dy.toDouble(), dx.toDouble())).toFloat()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        scaleGestureDetector.onTouchEvent(event)

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val hit = findBuildingAt(event.x, event.y)
                pressedId = hit?.building?.id
                if (hit != null) {
                    selectedId = hit.building.id
                    onBuildingSelected?.invoke(hit.building)
                    invalidate()
                }
                lastTouchX = event.x
                lastTouchY = event.y
                isDragging = false
                return true
            }

            MotionEvent.ACTION_POINTER_DOWN -> {

                if (event.pointerCount == 2) {
                    rotatePointerId1 = event.getPointerId(0)
                    rotatePointerId2 = event.getPointerId(1)
                    previousAngle = angleBetween(event, rotatePointerId1, rotatePointerId2)
                    if (pressedId != null) {
                        pressedId = null
                        invalidate()
                    }
                    isDragging = true
                }
            }

            MotionEvent.ACTION_MOVE -> {

                if (rotateEnabled && rotatePointerId1 != -1 && rotatePointerId2 != -1 &&
                    event.findPointerIndex(rotatePointerId1) != -1 &&
                    event.findPointerIndex(rotatePointerId2) != -1
                ) {
                    val newAngle = angleBetween(event, rotatePointerId1, rotatePointerId2)
                    val delta = newAngle - previousAngle
                    val i1 = event.findPointerIndex(rotatePointerId1)
                    val i2 = event.findPointerIndex(rotatePointerId2)
                    val pivotX = (event.getX(i1) + event.getX(i2)) / 2f
                    val pivotY = (event.getY(i1) + event.getY(i2)) / 2f
                    mapMatrix.postRotate(delta, pivotX, pivotY)
                    previousAngle = newAngle
                    updateInverse()
                    invalidate()
                } else if (panEnabled && event.pointerCount == 1) {
                    val dx = event.x - lastTouchX
                    val dy = event.y - lastTouchY
                    if (!isDragging && hypot(dx.toDouble(), dy.toDouble()) > touchSlop) {
                        isDragging = true
                        if (pressedId != null) {
                            pressedId = null
                            invalidate()
                        }
                    }
                    if (isDragging) {
                        mapMatrix.postTranslate(dx, dy)
                        updateInverse()
                        invalidate()
                    }
                    lastTouchX = event.x
                    lastTouchY = event.y
                } else if (pressedId != null) {

                    val stillInside = findBuildingAt(event.x, event.y)?.building?.id == pressedId
                    if (!stillInside) {
                        pressedId = null
                        invalidate()
                    }
                }
            }

            MotionEvent.ACTION_POINTER_UP -> {
                rotatePointerId1 = -1
                rotatePointerId2 = -1
            }

            MotionEvent.ACTION_UP -> {
                if (!isDragging) {
                    val hit = findBuildingAt(event.x, event.y)
                    if (hit != null && hit.building.id == pressedId) {
                        performClick()
                        onBuildingClick?.invoke(hit.building)
                        announceForAccessibility("${hit.building.label} selected")
                    }
                }
                pressedId = null
                isDragging = false
                rotatePointerId1 = -1
                rotatePointerId2 = -1
                invalidate()
            }

            MotionEvent.ACTION_CANCEL -> {
                pressedId = null
                isDragging = false
                rotatePointerId1 = -1
                rotatePointerId2 = -1
                invalidate()
            }
        }
        return true
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }


    fun selectBuilding(id: String?) {
        selectedId = id
        onBuildingSelected?.invoke(entries.find { it.building.id == id }?.building)
        invalidate()
    }


    fun getBuildingCenterOnScreen(id: String): PointF? {
        val e = entries.find { it.building.id == id } ?: return null
        val centerX = (e.bounds.left + e.bounds.right) / 2f
        val centerY = (e.bounds.top + e.bounds.bottom) / 2f
        val pts = floatArrayOf(centerX, centerY)
        mapMatrix.mapPoints(pts)
        return PointF(pts[0], pts[1])
    }
}