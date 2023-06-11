package com.zzx.mylibrary

import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt


/**
 *@描述：用于计算圆面上点的位置，三维转化成二维
 *@time：2023/6/8
 *@author:zhangzexin
 */
class NodeHelper {
    private val mInertiaZ = 0f
    private var mInertiaX = 0f
    private var mInertiaY = 0f

    private var mSinX: Float = 0f
    private var mCosX: Float = 0f
    private var mSinY: Float = 0f
    private var mCosY: Float = 0f
    private var mSinZ: Float = 0f
    private var mCosZ: Float = 0f

    val mPhi = Math.random() * Math.PI
    val mTheta = Math.random() * (2 * Math.PI)
    public final val DEFAULT_COLOR_END = floatArrayOf(0.886f, 0.725f, 0.188f, 1f)
    public final val DEFAULT_COLOR_START = floatArrayOf(0.3f, 0.3f, 0.3f, 1f)
    var mStartColor: FloatArray = DEFAULT_COLOR_START
    var mEndColor: FloatArray = DEFAULT_COLOR_END

    private var maxDelta = 0f
    private  var minDelta: Float = 0f

    fun recalculateAngle() {
        val degToRad = Math.PI / 180
        mSinX = sin(mInertiaX * degToRad).toFloat()
        mCosX = cos(mInertiaX * degToRad).toFloat()
        mSinY = sin(mInertiaY * degToRad).toFloat()
        mCosY = cos(mInertiaY * degToRad).toFloat()
        mSinZ = sin(mInertiaZ * degToRad).toFloat()
        mCosZ = cos(mInertiaZ * degToRad).toFloat()

    }

    fun updateNode(tag: Node, mRadius: Float) {
        val x = tag.spatialX
        val y = tag.spatialY
        val z = tag.spatialZ

        //There exists two options for this part:
        // multiply positions by a x-rotation matrix
        val ry1 = y * mCosX + z * -mSinX
        val rz1 = y * mSinX + z * mCosX
        // multiply new positions by a y-rotation matrix
        val rx2 = x * mCosY + rz1 * mSinY
        val rz2 = x * -mSinY + rz1 * mCosY
        // multiply new positions by a z-rotation matrix
        val rx3 = rx2 * mCosZ + ry1 * -mSinZ
        val ry3 = rx2 * mSinZ + ry1 * mCosZ
        // set arrays to new positions
        tag.spatialX = rx3
        tag.spatialY = ry3
        tag.spatialZ = rz2

        // add perspective
        val diameter: Float = 2 * mRadius
        val per = diameter / 1.0f / (diameter + rz2)
        // let's set position, scale, alpha for the tag;
        tag.flatX = rx3 * per
        tag.flatY = ry3 * per
        tag.scale = per

        // calculate alpha value
        val delta = diameter + rz2
        maxDelta = Math.max(maxDelta, delta)
        minDelta = Math.min(minDelta, delta)
        val alpha: Float = (delta - minDelta) / (maxDelta - minDelta)
        tag.alpha = 1 - alpha
    }

    fun buildNodePosition(
        newTag: Node,
        mRadius: Float,
        position: Int,
        max: Int,
        isRandomFace: Boolean
    ) {
        var temp_i = 1 + position
//        var temp_i = position
        var phi = mPhi
        var theta = mTheta
        if (!isRandomFace) {
            phi = acos(-1.0 + (2.0 * temp_i - 1.0) / max)
            theta = sqrt(max * Math.PI) * phi
//            val G = Math.PI * (3 - Math.sqrt(5.0))
//            theta = G*temp_i
//            phi = Math.acos(1-2*(temp_i+0.5)/max)
        }
        newTag.spatialX = (mRadius * cos(theta) * sin(phi)).toFloat()
        newTag.spatialY = (mRadius * sin(theta) * sin(phi)).toFloat()
        newTag.spatialZ = (mRadius * cos(phi)).toFloat()
    }

    fun setInertia(mInertiaX: Float, mInertiaY: Float) {
        this.mInertiaY = mInertiaY
        this.mInertiaX = mInertiaX
    }

    fun setEndColor(mEndColor: FloatArray) {
        this.mEndColor = mEndColor
    }

    fun setStartColor(mStartColor: FloatArray) {
        this.mStartColor = mStartColor
    }

    fun initNode(node: Node) {
        val percentage = getPercentage(node)
        val argb = getColorFromGradient(percentage, mEndColor, mStartColor)
        node.setColorComponent(argb)
        mMaxPopularity = Math.max(mMaxPopularity, node.popularity)
        mMinPopularity = Math.min(mMinPopularity, node.popularity)
    }

    private var mMinPopularity = 0
    private var mMaxPopularity: Int = 0
    private fun getPercentage(tag: Node): Float {
        val p = tag.popularity
        return if (mMinPopularity == mMaxPopularity) 1.0f else (p.toFloat() - mMinPopularity) / (mMaxPopularity.toFloat() - mMinPopularity)
    }

    private fun getColorFromGradient(
        percentage: Float,
        mEndColor: FloatArray,
        mStartColor: FloatArray
    ): FloatArray {
        val rgba = FloatArray(4)
        rgba[0] = 1f
        rgba[1] = percentage * mEndColor[0] + (1f - percentage) * mStartColor[0]
        rgba[2] = percentage * mEndColor[1] + (1f - percentage) * mStartColor[1]
        rgba[3] = percentage * mEndColor[2] + (1f - percentage) * mStartColor[2]
        return rgba
    }


}