package com.zzx.mylibrary

import android.content.Context
import android.graphics.Paint
import android.view.View
import android.view.ViewGroup
import com.zzx.mylibrary.link.ILinkNode

/**
 *@描述：
 *@time：2023/6/8
 *@author:zhangzexin
 */
abstract class Adapter {

    private var onDataSetChangeListener: OnDataSetChangeListener? = null

    abstract fun getCount(): Int
    abstract fun getView(context: Context?, position: Int, parent: ViewGroup?): View
    abstract fun getItem(position: Int): Any?
    abstract fun getPopularity(position: Int): Int
    abstract fun onThemeColorChanged(view: View?, themeColor: Int, alpha: Float)
    abstract fun buildLinkNode(node: Node, list: List<Node>): ILinkNode
    abstract fun buildLinkPaint(node: Node, target: Node, childX: Float, childY: Float, childX1: Float, childY1: Float): Paint


    fun notifyDataSetChanged() {
        onDataSetChangeListener?.onAdapterChanged()
    }

    interface OnDataSetChangeListener {
        fun onAdapterChanged()
    }

    fun setOnDataSetChangeListener(listener: OnDataSetChangeListener?) {
        onDataSetChangeListener = listener
    }

}