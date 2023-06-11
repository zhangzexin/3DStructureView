package com.zzx.mylibrary



/**
 *@描述：
 *@time：2023/6/9
 *@author:zhangzexin
 */
class NodeProxy(tag: Node, distance: Double): Comparable<NodeProxy> {
    var tag: Node
    var distance: Double

    init {
        this.tag = tag
        this.distance = distance
    }

    override fun compareTo(other: NodeProxy): Int {
        return if (distance > other.distance) 1 else -1
    }
}
