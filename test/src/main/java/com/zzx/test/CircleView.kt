package com.zzx.test

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.RadialGradient
import android.graphics.Shader
import android.graphics.SweepGradient
import android.graphics.drawable.GradientDrawable
import android.util.AttributeSet
import android.view.View
import androidx.core.graphics.ColorUtils
import java.util.Random

/**
 *@描述：
 *@time：2023/6/11
 *@author:zhangzexin
 */
// CircleView.kt
class CircleView : View {

    private var isRandomColor: Boolean = false

    // 定义一些属性
    private var circleColor = Color.RED // 圆球的颜色

    private var circleRadius = 100f // 圆球的半径

    private var centerX = 0f // 圆球的中心x坐标
    private var centerY = 0f // 圆球的中心y坐标
    private var shadowColor = Color.GRAY // 阴影的颜色
    private var shadowRadius = 10f // 阴影的半径
    private var edgeWidth = 5f // 边缘的宽度

    // 定义一些画笔对象
    private val circlePaint = Paint() // 用来画圆球的画笔
    private val shadowPaint = Paint() // 用来画阴影的画笔
    private val edgePaint = Paint() // 用来画边缘的画笔

    constructor(context: Context):super(context) {
    }
    constructor(context: Context, attrs: AttributeSet?): super(context, attrs) {

    }


    fun setCircleRadius(radiu: Float) {
        circleRadius = radiu
    }

    fun setCirclePaint(circleColor: Int) {
        circlePaint.color = circleColor
        circlePaint.style = Paint.Style.FILL
        circlePaint.isAntiAlias = true
        isRandomColor = false
    }

    fun setRandomCirclePaint() {
        //        circlePaint.color = Color.TRANSPARENT
        isRandomColor = true
//        circlePaint.shader = SweepGradient(centerX,centerY,getRandomColor(), getRandomColor())
        circlePaint.style = Paint.Style.FILL
        circlePaint.isAntiAlias = true
    }

    private fun getRandomColor(): Int {
        val random = Random()
        val color = Color.argb(255, random.nextInt(256), random.nextInt(256), random.nextInt(256))
        return color
    }

    fun setShadow(shadowColor: Int, shadowRadius: Float) {
        this.shadowColor = ColorUtils.setAlphaComponent(shadowColor, (255*0.4).toInt())
        shadowPaint.color = this.shadowColor
        shadowPaint.style = Paint.Style.FILL
        shadowPaint.isAntiAlias = true
        this.shadowRadius = shadowRadius
        shadowPaint.setShadowLayer(shadowRadius, 0f, 0f, this.shadowColor) // 设置阴影效果
    }

    fun setEdgePaint(edgeWidth: Float){
        this.edgeWidth = edgeWidth
        edgePaint.color = Color.WHITE
        edgePaint.style = Paint.Style.STROKE
        edgePaint.strokeWidth = edgeWidth
        edgePaint.isAntiAlias = true
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // 根据传入的测量规范，计算出合适的宽高，并设置测量结果
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int
        val height: Int

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize
        } else {
            width = (paddingLeft + paddingRight + circleRadius * 2 + shadowRadius * 2).toInt()
            if (widthMode == MeasureSpec.AT_MOST) {
                width.coerceAtMost(widthSize)
            }
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize
        } else {
            height = (paddingTop + paddingBottom + circleRadius * 2 + shadowRadius * 2).toInt()
            if (heightMode == MeasureSpec.AT_MOST) {
                height.coerceAtMost(heightSize)
            }
        }

        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        // 根据圆球的属性，在canvas上绘制出圆球和阴影效果
        centerX = (width / 2).toFloat()
        centerY = (height / 2).toFloat()

        canvas.drawCircle(centerX, centerY, circleRadius + shadowRadius, shadowPaint) // 画阴影

        canvas.save() // 保存当前状态

        canvas.clipRect(centerX - circleRadius - edgeWidth / 2,
            centerY - circleRadius - edgeWidth / 2,
            centerX + circleRadius + edgeWidth / 2,
            centerY + circleRadius + edgeWidth / 2) // 裁剪出一个圆形区域

        if (isRandomColor) {
            val i = Random().nextInt(2) + 2
            val toIntArray = arrayOfNulls<Int>(i).map {
                getRandomColor()
            }.toIntArray()

            circlePaint.shader = RadialGradient(
                (Math.random() * (width)).toFloat(),
                (Math.random() * height).toFloat(),
                circleRadius,
                toIntArray,
                null,
                Shader.TileMode.REPEAT
            )
        }
        canvas.drawCircle(centerX, centerY, circleRadius, circlePaint) // 画圆球

        canvas.restore() // 恢复之前的状态

        canvas.drawCircle(centerX, centerY, circleRadius - edgeWidth / 2, edgePaint) // 画边缘

    }
}
