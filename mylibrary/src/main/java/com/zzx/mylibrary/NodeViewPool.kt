package com.zzx.mylibrary


import com.zzx.mylibrary.link.ILinkNode
import java.util.LinkedList

/**
 *@描述：
 *@time：2023/6/8
 *@author:zhangzexin
 */
class NodeViewPool {
    private var mNodeList: MutableList<Node>
    private val mLinkNodeList: MutableList<ILinkNode> = LinkedList()
    private var mRadius = 0.0f

    private var mMinPopularity = 0
    private  var mMaxPopularity: Int = 0
    private var isRandomFace = true

    companion object {
        private val DEFAULT_RADIUS = 3.0f


    }

    constructor(): this(DEFAULT_RADIUS){
    }

    constructor(radius: Float):this(ArrayList<Node>(), radius) {

    }

    constructor(tags: MutableList<Node>):this(tags, DEFAULT_RADIUS) {

    }

    constructor(tags: MutableList<Node>,radius: Float) {
        mNodeList = tags
        mRadius = radius
    }

    fun clear() {
        mNodeList.clear()
        reseatLinkNode()
    }

    fun getNodeList(): List<Node>? {
        return mNodeList
    }

    fun getLinkNodeList(): List<ILinkNode>? {
        return mLinkNodeList
    }

    operator fun get(position: Int): Node {
        return mNodeList[position]
    }



    fun add(newNode: Node) {
        mNodeList.add(newNode)
    }

    fun buildLinkNode(buildLinkNode: ILinkNode) {
        //计算相邻节点
        mLinkNodeList.add(buildLinkNode)
    }

    fun isInit():Boolean {
        if (getLinkNodeList().isNullOrEmpty()) {
            return false
        }
        return true
    }

    fun reseatLinkNode() {
        mLinkNodeList.forEach {
            it.list.clear()
        }
        mLinkNodeList.clear()
    }

}