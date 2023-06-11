# NodeBallView
本项目是基于[3dTagCloudAndroid](http://7fvfii.com1.z0.glb.clouddn.com/sample_qrcode.png)
由于原先项目性能不佳，所以才有这个项目产生
并添加了各节点关联性

### UI效果


### 使用
看app目录下的例子
```
#NodeBallView.kt
提供了差值器，用于应对各种旋转效果
 fun setInterpolator()

```

```agsl
/*构建节点*/  
fun getView(context: Context?, position: Int, parent: ViewGroup?): View 
/*返回Node数据*/  
abstract fun getItem(position: Int): Any?
/*针对每个Node返回一个权重值，该值与ThemeColor和Node初始大小有关；一个简单的权重值生成方式是对一个数N取余或使用随机数*/  
abstract fun getPopularity(position: Int): Int
/*Node主题色发生变化时会回调该方法*/ 
fun onThemeColorChanged(view: View?, themeColor: Int, alpha: Float)
/*节点间关系列表，最后连线的关系*/
fun buildLinkNode(node: Node, list: List<Node>): ILinkNode
/*构建线样式*/
fun buildLinkPaint(node: Node, target: Node, childX: Float, childY: Float, childX1: Float, childY1: Float): Paint
```
- 定制属性

| 属性        | xml           | 代码 |值类型|
|:------------: |:-------------:| :----:|:-:
| 自动滚动      | app:autoScrollMode | setAutoScrollMode(int mode) |enum [disable,uniform,decelerate]
| 半径百分比      | app:radiusPercent      |   setRadiusPercent(float percent) |float [0,1]
| 滚动速度 | app:scrollSpeed      |    setScrollSpeed(float scrollSpeed) |float [0,+]
|起始颜色|app:startColor|setLightColor(int color)|int
|终止颜色|app:endColor|setDarkColor(int color)|int  
