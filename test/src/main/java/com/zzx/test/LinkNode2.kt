package com.zzx.test

import com.zzx.mylibrary.NodeProxy
import com.zzx.mylibrary.Node
import com.zzx.mylibrary.link.ILinkNode
import java.util.LinkedList

/**
 *@描述：
 *@time：2023/6/10
 *@author:zhangzexin
 */
public class LinkNode2(node: Node, list: List<Node>) : ILinkNode(node, list) {



    override fun buildList(list: List<Node>): MutableList<Node> {
        val tagProxies = arrayListOf<NodeProxy>()
        var total = 0.0
        for (i in list.indices) {
            val tag1 = list[i]
            if (tag1 != this.node) {
                val nodeProxy =
                    NodeProxy(tag1, this.node.mSpatialCenter.getDistance(tag1.mSpatialCenter))
                tagProxies.add(nodeProxy)
                total += nodeProxy.distance
            }
        }
        tagProxies.sort()
        val templist = LinkedList<Node>()
        templist.add(tagProxies[0].tag)
        return templist
    }
}