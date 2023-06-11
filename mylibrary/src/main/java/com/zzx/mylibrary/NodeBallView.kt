package com.zzx.mylibrary

import android.animation.FloatEvaluator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Point
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.WindowManager
import android.view.animation.AnticipateOvershootInterpolator
import android.view.animation.Interpolator
import androidx.annotation.IntDef
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.util.LinkedList
import kotlin.math.abs
import kotlin.math.min

/**
 *@描述：
 *@time：2023/6/8
 *@author:zhangzexin
 */
class NodeBallView : ViewGroup, Adapter.OnDataSetChangeListener, Runnable {
    companion object {
        const val MODE_DISABLE = 0
        const val MODE_DECELERATE = 1
        const val MODE_UNIFORM = 2

        private const val TOUCH_SCALE_FACTOR = 0.8f
    }

    @IntDef(*[MODE_DISABLE, MODE_DECELERATE, MODE_UNIFORM])
    @Retention(
        RetentionPolicy.SOURCE
    )
    annotation class Mode {}

    private var mInertiaX_Def: Float = 0.0f
    private var mInertiaY_Def: Float = 0.0f

    private var mInertiaX_U: Float = 0.0f
    private var mInertiaY_U: Float = 0.0f
    private lateinit var mAdapter: Adapter
    private val mNodeViewPool: NodeViewPool by lazy {
        NodeViewPool()
    }

    @Mode
    var mMode: Int = MODE_DISABLE

    private var mInertiaX = 0f
    private var mInertiaY = 0f

    private var _mInertiaX = 0f
    private var _mInertiaY = 0f
    private var manualScroll = false

    private var isRandomFace = false

    //    private var mLightColor = TagCloud.DEFAULT_COLOR_DARK
    private var mStartColor = floatArrayOf(0.9412f, 0.7686f, 0.2f, 1f)

    //    private var mDarkColor = TagCloud.DEFAULT_COLOR_LIGHT
    private var mEndColor = floatArrayOf(1f, 0f, 0f, 1f)
    private var mRadiusPercent = 0.9f
    private var mSpeed = 2f
    private var mMinSize = 0

    private var mCenterX: Float = -1f
    private var mCenterY: Float = -1f

    private var mRadius = 0f
    private val mLayoutParams: MarginLayoutParams by lazy {
        layoutParams as MarginLayoutParams
    }
    var mNodeHelper = NodeHelper()
    private var downX: Float = 0f
    private var downY: Float = 0f
    private var mIsOnTouch = false
    @Volatile
    private var mfirstEvent = false
    private var tempList: MutableList<Node> = LinkedList()
    var mInterpolator: Interpolator? = AnticipateOvershootInterpolator()
    var mFloatEvaluator: FloatEvaluator = FloatEvaluator()
    var durationAnimation = 5
    private var lastTime: Long = 0

    private fun isValidMove(dx: Float, dy: Float): Boolean {
        val minDistance = ViewConfiguration.get(context).scaledTouchSlop
        return abs(dx) > minDistance || abs(dy) > minDistance
    }

    private fun updateUi() {
        if (mNodeViewPool != null) {
            mNodeHelper.setInertia(mInertiaX, mInertiaY)
            mNodeHelper.recalculateAngle()
        }
        requestLayout()
    }

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : this(
        context,
        attrs,
        defStyleAttr,
        0
    )

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        isFocusableInTouchMode = true
        isChildrenDrawingOrderEnabled = true
        initAttrs(context, attrs)
        initWindow(context)
    }

    private fun initWindow(context: Context) {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val point = Point()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            wm.defaultDisplay.getSize(point)
        } else {
            point.x = wm.defaultDisplay.width
            point.y = wm.defaultDisplay.height
        }
        val screenWidth = point.x
        val screenHeight = point.y
        mMinSize = if (screenHeight < screenWidth) screenHeight else screenWidth
    }

    private fun initAttrs(context: Context, attrs: AttributeSet?) {
        if (attrs != null) {
            val typedArray = context.obtainStyledAttributes(attrs, R.styleable.NodeCloudView)
            val m = typedArray.getString(R.styleable.NodeCloudView_autoScrollMode)
            mMode = Integer.valueOf(m)
            when (mMode) {
                MODE_DECELERATE -> {
                    mInterpolator = AnticipateOvershootInterpolator()
                }

                MODE_DISABLE -> {
                    mInterpolator = null
                }

                MODE_UNIFORM -> {
                    mInertiaX_Def = typedArray.getFloat(R.styleable.NodeCloudView_startAngleX, 0.5f)
                    mInertiaY_Def = typedArray.getFloat(R.styleable.NodeCloudView_startAngleY, 0.5f)
                    mInertiaX = mInertiaX_Def
                    mInertiaY = mInertiaY_Def
                    _mInertiaX = mInertiaX_Def
                    _mInertiaY = mInertiaY_Def
                    mInertiaX_U = mInertiaX_Def
                    mInertiaY_U = mInertiaY_Def
                }
            }
            setManualScroll(typedArray.getBoolean(R.styleable.NodeCloudView_manualScroll, true))
            val light = typedArray.getColor(R.styleable.NodeCloudView_startColor, Color.WHITE)
            initStartColor(light)
            val dark = typedArray.getColor(R.styleable.NodeCloudView_endColor, Color.BLACK)
            initEndColor(dark)
            val p = typedArray.getFloat(R.styleable.NodeCloudView_radiusPercent, mRadiusPercent)
            initRadiusPercent(p)
            val s = typedArray.getFloat(R.styleable.NodeCloudView_scrollSpeed, 2f)
            setScrollSpeed(s)
            typedArray.recycle()
        }
    }

    private fun setScrollSpeed(scrollSpeed: Float) {
        mSpeed = scrollSpeed
    }

    /**
     * 用于设置旋转动画效果
     */
    public fun setInterpolator(interpolator: Interpolator) {
        this.mInterpolator = interpolator
        mAdapter?.notifyDataSetChanged()
    }


    private fun initRadiusPercent(percent: Float) {
        mRadiusPercent = percent
    }

    fun setEndColor(color: Int) {
        initEndColor(color)
        mAdapter?.notifyDataSetChanged()
    }

    fun setStartColor(color: Int) {
        initStartColor(color)
        mAdapter?.notifyDataSetChanged()
    }

    fun setRandomFace(isRandomFace: Boolean) {
        this.isRandomFace = isRandomFace
        mAdapter?.notifyDataSetChanged()
    }


    private fun initEndColor(color: Int) {
        val argb = FloatArray(4)
        argb[3] = Color.alpha(color) / 1.0f / 0xff
        argb[0] = Color.red(color) / 1.0f / 0xff
        argb[1] = Color.green(color) / 1.0f / 0xff
        argb[2] = Color.blue(color) / 1.0f / 0xff

        mEndColor = argb.clone()
        mNodeHelper.setEndColor(mEndColor)
    }

    fun setRadiusPercent(percent: Float) {
        require(!(percent > 1f || percent < 0f)) { "percent value not in range 0 to 1" }
        initRadiusPercent(percent)
        mAdapter?.notifyDataSetChanged()
    }

    private fun initStartColor(color: Int) {
        val argb = FloatArray(4)
        argb[3] = Color.alpha(color) / 1.0f / 0xff
        argb[0] = Color.red(color) / 1.0f / 0xff
        argb[1] = Color.green(color) / 1.0f / 0xff
        argb[2] = Color.blue(color) / 1.0f / 0xff

        mStartColor = argb.clone()
        mNodeHelper.setStartColor(mStartColor)
    }

    fun setManualScroll(manualScroll: Boolean) {
        this.manualScroll = manualScroll
    }

    fun setAutoScrollMode(@Mode mode: Int) {
        this.mMode = mode
    }


    @Mode
    fun getAutoScrollMode(): Int {
        return mMode
    }

    fun setAdapter(adapter: Adapter) {
        this.mAdapter = adapter
        mAdapter.setOnDataSetChangeListener(this)
        removeAllViews()
        mAdapter.notifyDataSetChanged()
    }

    var mTargetNode: Node? = null
    private fun buildAdapterView() {
        mNodeViewPool.clear()
        for (i in 0 until mAdapter.getCount()) {
            val view = mAdapter.getView(context, i, this@NodeBallView)
            val nodekt = Node(mAdapter.getPopularity(i))
            nodekt.bindingView(view = view)
            mNodeViewPool.add(nodekt)
            mNodeHelper.initNode(nodekt)
            addView(view)
        }
        mTargetNode = mNodeViewPool.getNodeList()?.get(1)
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        if (mMode == MODE_UNIFORM) {
            post(this)
        }
    }

    override fun onDetachedFromWindow() {
        removeCallbacks(this)
        super.onDetachedFromWindow()
    }


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        val contentWidth = MeasureSpec.getSize(widthMeasureSpec)
        val contentHeight = MeasureSpec.getSize(heightMeasureSpec)
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val measuredWidth = if (widthMode == MeasureSpec.EXACTLY) contentWidth else (mMinSize
                - mLayoutParams.leftMargin - mLayoutParams.rightMargin)
        val measuredHeight = if (heightMode == MeasureSpec.EXACTLY) contentHeight else (mMinSize
                - mLayoutParams.leftMargin - mLayoutParams.rightMargin)
        setMeasuredDimension(measuredWidth, measuredHeight)
        measureChildren(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
    }

    override fun onLayout(changed: Boolean, l: Int, t: Int, r: Int, b: Int) {
        if (mCenterX == -1f) mCenterX = (right - left).toFloat() / 2
        if (mCenterY == -1f) mCenterY = (bottom - top).toFloat() / 2
        if (mRadius == 0f) mRadius = min(mCenterX * mRadiusPercent, mCenterY * mRadiusPercent)
        if (mNodeViewPool.isInit()) {
            mNodeHelper.setInertia(mInertiaX, mInertiaY)
        }

        mNodeHelper.recalculateAngle()
        for (i in 0 until childCount) {
            //计算节点的位置
            val child = getChildAt(i)
            var node: Node? = null
            if (!mNodeViewPool.getNodeList()
                    .isNullOrEmpty() && mNodeViewPool.getNodeList()!!.size > i
            ) {
                node = mNodeViewPool[i]
            }
            node?.let {
                if (!mNodeViewPool.isInit()) {
                    mNodeHelper.buildNodePosition(node, mRadius, i, childCount, isRandomFace)
                }
                if (child != null && child.visibility != GONE) {
                    mNodeHelper.updateNode(node, mRadius)
                    mAdapter.onThemeColorChanged(child, node.color, node.alpha)
                    child.scaleX = node.scale
                    child.scaleY = node.scale
                    var left: Int = (mCenterX + node.flatX - child.measuredWidth / 2).toInt()
                    var top: Int = (mCenterY + node.flatY - child.measuredHeight / 2).toInt()
                    child.layout(left, top, left + child.measuredWidth, top + child.measuredHeight)
                }
            }
        }

        if (!mNodeViewPool.isInit()) {
            mNodeViewPool.reseatLinkNode()
            mNodeViewPool.getNodeList()?.let {
                it.forEach { node ->
                    mNodeViewPool.buildLinkNode(mAdapter.buildLinkNode(node, it))
                }
            }
        }
        //以缩放大小排序，其实也就是Z轴深度排序，为后面绘制顺序做准备
        tempList.clear()
        mNodeViewPool.getNodeList()?.sorted()?.let {
            tempList.addAll(it)
        }

    }

    override fun onAdapterChanged() {
        removeCallbacks(this)
        mNodeViewPool.clear()
        buildAdapterView()
        requestLayout()
    }

    override fun dispatchDraw(canvas: Canvas) {
        tempSet.clear()
        //统一在绘制子view前绘制连接线的话会把线遮挡，看个人取舍

//        val nodeList = mNodeViewPool.getLinkNodeList()?.asReversed()
//        if (!nodeList.isNullOrEmpty()) {
//            val paint = initPaint()
//            for (node in nodeList) {
//                val view = node.node.view
//                val x = ((view.left + view.right) / 2).toFloat()
//                val y = ((view.top + view.bottom) / 2).toFloat()
//                for (i in node.list.indices) {
//                    val childNode = node.list[i].view
//                    val child_x = ((childNode.left + childNode.right) / 2).toFloat()
//                    val child_y = ((childNode.top + childNode.bottom) / 2).toFloat()
//                    mPaint!!.shader = LinearGradient(
//                        x, y, child_x, child_y,
//                        Color.RED, Color.BLUE,
//                        Shader.TileMode.CLAMP
//                    )
//                    canvas.drawLine(x, y, child_x, child_y, paint)
//                }
//            }
//        }
//        if (isBuilding) {
//
//        } else {
            super.dispatchDraw(canvas)
//        }
    }

    private val tempSet: HashSet<NodePoints> = HashSet()
    override fun drawChild(canvas: Canvas, child: View, drawingTime: Long): Boolean {
        var paint: Paint? = null
        val linkNode = mNodeViewPool.getLinkNodeList()?.find { it -> it.node.view == child }
            ?: return super.drawChild(canvas, child, drawingTime)
        val x = ((child.left + child.right) / 2).toFloat()
        val y = ((child.top + child.bottom) / 2).toFloat()
        val beforeList = linkNode?.list?.filter { it -> (it.scale <= linkNode.node.scale) }
        val afterList = linkNode?.list?.filter { it -> it.scale > linkNode.node.scale }
        if (!beforeList.isNullOrEmpty()) {
            for (i in beforeList.indices) {
                if (tempSet.add(NodePoints(linkNode.node, beforeList[i]))) {
                    val childNode = beforeList[i].view
                    val child_x = ((childNode.left + childNode.right) / 2).toFloat()
                    val child_y = ((childNode.top + childNode.bottom) / 2).toFloat()
                    paint = mAdapter.buildLinkPaint(linkNode.node,beforeList[i],x, y, child_x, child_y)
                    if (paint != null) {
                        canvas.drawLine(x, y, child_x, child_y, paint)
                    }
                }
            }
        }
        val drawChild: Boolean = super.drawChild(canvas, child, drawingTime)
        if (!afterList.isNullOrEmpty()) {
            for (i in afterList.indices) {
                if (tempSet.add(NodePoints(linkNode.node, afterList[i]))) {
                    val childNode = afterList[i].view
                    val child_x = ((childNode.left + childNode.right) / 2).toFloat()
                    val child_y = ((childNode.top + childNode.bottom) / 2).toFloat()
                    paint = mAdapter.buildLinkPaint(linkNode.node,afterList[i],x, y, child_x, child_y)
                    if (paint != null) {
                        canvas.drawLine(x, y, child_x, child_y, paint)
                    }
                }
            }
        }
        return drawChild
    }


    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (manualScroll) {
            handleTouchEvent(event)
            return true
        }
        return false
    }

    private fun handleTouchEvent(e: MotionEvent) {
        when (e.action) {
            MotionEvent.ACTION_DOWN -> {
                downX = e.x
                downY = e.y
                mIsOnTouch = true
                mfirstEvent = true
            }

            MotionEvent.ACTION_MOVE -> {
                val dx: Float = e.x - downX
                val dy: Float = e.y - downY
                if (isValidMove(dx, dy)) {
                    _mInertiaX = dy / mRadius * mSpeed * TOUCH_SCALE_FACTOR
                    _mInertiaY = -dx / mRadius * mSpeed * TOUCH_SCALE_FACTOR
                    if (mfirstEvent) {
                        mfirstEvent = false
                        postDelayed(this, 16)
                    }
                }
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsOnTouch = false
                mfirstEvent = false
                lastTime = System.currentTimeMillis()
            }
        }
    }

    override fun getChildDrawingOrder(childCount: Int, drawingPosition: Int): Int {
        if (!tempList.isNullOrEmpty() && tempList.size > drawingPosition) {
            val indexOfChild = indexOfChild(tempList[drawingPosition].view)
            if (indexOfChild >= 0 && indexOfChild < mAdapter.getCount()) {
                return indexOfChild
            }
        }
        return super.getChildDrawingOrder(childCount, drawingPosition)

    }

    //处理滑动后的滚动效果
    override fun run() {
        when (mMode) {
            MODE_DISABLE -> {
                if (mIsOnTouch) {
                    mInertiaY = _mInertiaY
                    mInertiaX = _mInertiaX
                } else {
                    buildInertia(_mInertiaX, 0, _mInertiaY, 0)
                }
            }

            MODE_DECELERATE -> {
                if (mIsOnTouch) {
                    mInertiaY = _mInertiaY
                    mInertiaX = _mInertiaX
                } else {
                    if (buildInertia(_mInertiaX, 0, _mInertiaY, 0) < 1f) {
                        updateUi()
                        postDelayed(this, 16)
                    } else {
                        removeCallbacks(this)
                    }
                    return
                }
            }

            MODE_UNIFORM -> {
                if (mIsOnTouch) {
                    mInertiaY = _mInertiaY
                    mInertiaX = _mInertiaX
                } else {
//                    if (abs(_mInertiaX) > abs(_mInertiaY)) {
//                        mInertiaX_U = if (abs(_mInertiaX) > abs(mInertiaX_Def)) {
//                            if (_mInertiaX > 0) {
//                                abs(mInertiaX_Def)
//                            } else {
//                                abs(mInertiaX_Def)*-1
//                            }
//                        } else {
//                            if (_mInertiaX > 0) {
//                                abs(_mInertiaX)
//                            } else {
//                                abs(_mInertiaX)*-1
//                            }
//                        }
//                        mInertiaY_U = (abs(_mInertiaY) * abs(mInertiaX_U)) / abs(_mInertiaX)
//                    } else {
//                        mInertiaY_U = if (abs(_mInertiaY) > abs(mInertiaY_Def)) {
//                            if (_mInertiaY > 0) {
//                                abs(mInertiaY_Def)
//                            } else {
//                                abs(mInertiaY_Def)*-1
//                            }
//                        } else {
//                            if (_mInertiaY > 0) {
//                                abs(_mInertiaY)
//                            } else {
//                                abs(_mInertiaY)*-1
//                            }
//                        }
//                        mInertiaX_U = (abs(mInertiaY_U) * abs(_mInertiaX)) / abs(_mInertiaY)
//                    }
//                    buildInertia(_mInertiaX,mInertiaX_U,_mInertiaY,mInertiaY_U)
                }
            }
        }


        if (mInertiaY != 0.0f || mInertiaX != 0.0f) {
            updateUi()
            postDelayed(this, 16)
        } else {
            removeCallbacks(this)
        }

    }

    private fun buildInertia(startX: Number, endX: Number, startY: Number, endY: Number): Float {
        val currentTimeMillis = System.currentTimeMillis()
        if (lastTime <= 0) {
            lastTime = currentTimeMillis
        }
        val input: Float =
            (currentTimeMillis - lastTime).toFloat() / (durationAnimation * 1000).toFloat()
        var interpolation = 1f
        interpolation = if (mInterpolator == null) {
            1f
        } else {
            mInterpolator?.getInterpolation(if (input >= 1f) 1f else input) ?: 1f
        }
        mInertiaX = mFloatEvaluator.evaluate(interpolation, startX, endX)
        mInertiaY = mFloatEvaluator.evaluate(interpolation, startY, endY)
//        Log.d(
//            "TAG",
//            "mInertiaX=$mInertiaX mInertiaY=$mInertiaY interpolation: $interpolation  input:$input"
//        )
        return input
    }


}