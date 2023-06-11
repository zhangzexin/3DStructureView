package com.zzx.mylibrary.link

import com.zzx.mylibrary.Node

/**
 *@描述：实际关联节点的绘制
 *@time：2023/6/10
 *@author:zhangzexin
 */
public abstract class ILinkNode {
    lateinit var node: Node
    lateinit var list: MutableList<Node>
    constructor(node: Node, list: List<Node>) {
        this.node = node
        this.list = buildList(list)

    }



    abstract fun buildList(list: List<Node>): MutableList<Node>
}
