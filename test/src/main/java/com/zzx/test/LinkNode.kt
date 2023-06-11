package com.zzx.test

import android.util.Log
import com.zzx.mylibrary.NodeProxy
import com.zzx.mylibrary.Node
import com.zzx.mylibrary.link.ILinkNode
import java.util.LinkedList

/**
 * @描述：节点间的关联性类
 * @time：2023/6/8
 * @author:zhangzexin
 */
public class LinkNode(node: Node, list: List<Node>): ILinkNode(node, list),Comparable<LinkNode?> {


    override fun compareTo(other: LinkNode?): Int {
        return if (node.scale > other!!.node.scale) 1 else -1
    }

    override fun buildList(list: List<Node>): MutableList<Node> {
        val currentTimeMillis = System.currentTimeMillis()
        val tagProxies = arrayListOf<NodeProxy>()
        var total = 0.0
        var min = -1.0
        for (i in list.indices) {
            val tag1 = list[i]
            if (tag1 != node) {
                val nodeProxy =
                    NodeProxy(tag1, this.node.mSpatialCenter.getDistance(tag1.mSpatialCenter))
                tagProxies.add(nodeProxy)
                total += nodeProxy.distance
                if (min == -1.0) {
                    min = nodeProxy.distance
                } else{
                    min = if(min >nodeProxy.distance) nodeProxy.distance else min
                }
            }
        }

//        quickSort(tagProxies, 0, tagProxies.size - 1)
        tagProxies.sort()
        this.list = LinkedList()
        var i = 0
//        val average = tagProxies.map { it!!.distance }.average()*0.485
//        val average = total/tagProxies.size * 0.485
        val average = min * 1.3
        while (tagProxies.size > i && (tagProxies[i]?.distance!! <= average)) {
            if (tagProxies[i]!!.tag != node) {
                this.list.add(tagProxies[i]!!.tag)
            }
            i++
        }
//        for (i in 0..5) {
//            if (tagProxies[i]!!.tag != node) {
//                this.list.add(tagProxies[i]!!.tag)
//            }
//        }
        Log.d("TAG", "quickSort: ${System.currentTimeMillis() - currentTimeMillis}")
        return this.list
    }
}