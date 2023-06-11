package com.zzx.mylibrary

import android.graphics.Color
import android.graphics.PointF
import android.view.View
import android.widget.TextView
import com.zzx.mylibrary.graphics.Point3DF


/**
 *
 *@描述：主要用来存放单个节点的信息
 *@time：2023/6/8
 *@author:zhangzexin
 */
class Node(
    x: Float = 0f,
    y: Float = 0f,
    z: Float = 0f,
    scale: Float = 1.0f,
    popularity: Int = 0
) :
    Comparable<Node?> {
    val popularity: Int
    var scale: Float
    private val mColor: FloatArray
    lateinit var view: View
        private set
    private val mFlatCenter: PointF
    var mSpatialCenter: Point3DF

    constructor(popularity: Int) : this(0f, 0f, 0f, 1.0f, popularity) {}
    constructor(x: Float, y: Float, z: Float) : this(x, y, z, 1.0f, DEFAULT_POPULARITY) {}
    constructor(x: Float, y: Float, z: Float, scale: Float) : this(
        x,
        y,
        z,
        scale,
        DEFAULT_POPULARITY
    ) {
    }

    init {
        mSpatialCenter = Point3DF(x, y, z)
        mFlatCenter = PointF(0f, 0f)
        mColor = floatArrayOf(1.0f, 0.5f, 0.5f, 0.5f)
        this.scale = scale
        this.popularity = popularity
    }

    var spatialX: Float
        get() = mSpatialCenter.x
        set(x) {
            mSpatialCenter.x = x
        }
    var spatialY: Float
        get() = mSpatialCenter.y
        set(y) {
            mSpatialCenter.y = y
        }
    var spatialZ: Float
        get() = mSpatialCenter.z
        set(z) {
            mSpatialCenter.z = z
        }

    fun bindingView(view: View) {
        this.view = view
    }

    var flatX: Float
        get() = mFlatCenter.x
        set(x) {
            mFlatCenter.x = x
        }
    var flatY: Float
        get() = mFlatCenter.y
        set(y) {
            mFlatCenter.y = y
        }

    fun setColorComponent(rgb: FloatArray?) {
        if (rgb != null) {
            System.arraycopy(rgb, 0, mColor, mColor.size - rgb.size, rgb.size)
        }
    }

    var alpha: Float
        get() = mColor[0]
        set(alpha) {
            mColor[0] = alpha
        }
    val color: Int
        get() {
            val result = IntArray(4)
            for (i in 0..3) {
                result[i] = (mColor[i] * 0xff).toInt()
            }
            return Color.argb(result[0], result[1], result[2], result[3])
        }


    companion object {
        private const val DEFAULT_POPULARITY = 5
    }

    override fun compareTo(other: Node?): Int {
        return if (scale > other?.scale!!) 1 else -1
    }

    override fun toString(): String {
        return "name:${(view as TextView).text} x:$spatialX y:$spatialY z:$spatialZ flatX:$flatX flatY:$flatY scale:$scale popularity:$popularity"
    }
}