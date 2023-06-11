package com.zzx.test

import android.content.Context
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Shader
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import com.zzx.mylibrary.Adapter
import com.zzx.mylibrary.Node
import com.zzx.mylibrary.link.ILinkNode
import java.util.Collections

/**
 *@描述：文字列表
 *@time：2023/6/8
 *@author:zhangzexin
 */
class TextAdapter: Adapter {

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
        val tv = TextView(context)
        tv.text = "No.$position"
        tv.setBackgroundColor(Color.RED)
        tv.gravity = Gravity.CENTER
        tv.setOnClickListener {
            Log.e("Click", "Tag $position clicked.")
            Toast.makeText(context, "Tag $position clicked", Toast.LENGTH_SHORT).show()
        }
        tv.setTextColor(Color.WHITE)
        return tv
    }

    override fun getItem(position: Int): Any? {
        return dataSet[position]
    }

    override fun getPopularity(position: Int): Int {
        return position % 7
    }

    override fun onThemeColorChanged(view: View?, themeColor: Int, alpha: Float) {
//        view?.setBackgroundColor(Color.parseColor("#000000"))
        view?.setBackgroundColor(themeColor)
//        view?.background?.alpha = (255*alpha).toInt()
//        val textColors = (view as TextView).textColors.defaultColor
//        (view as TextView).setTextColor(ColorUtils.setAlphaComponent(textColors,
//            (alpha*255).toInt()
//        ))
//        Log.d("TAG", "onThemeColorChanged: alpha=$alpha")
    }

    override fun buildLinkNode(node: Node, list: List<Node>): ILinkNode {
        return LinkNode(node,list)
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