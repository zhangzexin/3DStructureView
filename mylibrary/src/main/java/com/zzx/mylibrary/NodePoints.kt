package com.zzx.mylibrary


/**
 *@描述：
 *@time：2023/6/10
 *@author:zhangzexin
 */
class NodePoints {
    val node1:Node
    val node2:Node

    constructor(node1: Node, node2: Node) {
        this.node1 = node1
        this.node2 = node2
    }

    override fun hashCode(): Int {
        return (this.node1.hashCode() + this.node2.hashCode())
    }

    override fun equals(other: Any?): Boolean {
        return hashCode() == other.hashCode()
    }


}