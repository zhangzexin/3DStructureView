package com.zzx.test

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.view.View
import android.view.ViewGroup
import com.zzx.mylibrary.Adapter
import com.zzx.mylibrary.Node
import com.zzx.mylibrary.link.ILinkNode
import java.util.Collections

/**
 *@描述：球形
 *@time：2023/6/8
 *@author:zhangzexin
 */
class CircleAdapter: Adapter {

    private val dataSet: MutableList<String?> = ArrayList()
    var mPaint: Paint? = null

    constructor(data: Array<String?>) {
        dataSet.clear()
        Collections.addAll(dataSet, *data)
    }


    override fun getCount(): Int {
        return dataSet.size
    }

    override fun getView(context: Context?, position: Int, parent: ViewGroup?): View {
        val circleView = context?.let { CircleView(it) }
        circleView?.apply {
            setCircleRadius(30.0f)
//            setCirclePaint(Color.BLACK)
            setRandomCirclePaint()
            setShadow(Color.GRAY,1.0f)
            setEdgePaint(0.1f)
        }
        return circleView!!
    }

    override fun getItem(position: Int): Any? {
        return dataSet[position]
    }

    override fun getPopularity(position: Int): Int {
        return position % 7
    }

    override fun onThemeColorChanged(view: View?, themeColor: Int, alpha: Float) {
//        (view as CircleView).setCirclePaint(themeColor)
//        view?.setBackgroundColor(Color.parseColor("#000000"))
//        Log.d("TAG", "onThemeColorChanged: alpha=$alpha")
    }

    override fun buildLinkNode(node: Node, list: List<Node>): ILinkNode {
        return LinkNode2(node,list)
    }

    override fun buildLinkPaint(node: Node, target: Node, x: Float, y: Float, childX: Float, childY: Float): Paint {
        val paint = initPaint()
        if (node.scale > target.scale) {
            paint.shader = LinearGradient(
                x, y, childX, childY,
                Color.GREEN, Color.YELLOW,
                Shader.TileMode.CLAMP
            )
        } else {
            paint.shader = LinearGradient(
                x, y, childX, childY,
                Color.RED, Color.BLUE,
                Shader.TileMode.CLAMP
            )
        }
        return paint
    }

    private fun initPaint(): Paint {
        if (mPaint == null) {
            mPaint = Paint()
            mPaint?.color = Color.parseColor("#000000")
            mPaint?.strokeWidth = 10f
            mPaint?.isAntiAlias = true

        }
        return mPaint!!
    }

}