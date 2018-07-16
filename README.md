# CustomViews

View是屏幕上的一块巨型区域，自定义View就是自定义屏幕上的一块矩形区域。
要自定义这块区域，首先要确定整体View的位置，但是位置的确定是父布局通过layout
参数去确定的，所以自定义View的时候，不需要确定整体View的位置
然后就是确定大小，我们需要在自定义View的时候设置一个默认大小,使用者用warp_content
的时候用默认大小，或者根据实际绘制的大小，算出来比例进行缩放绘制。
确定整体大小之后，接下里就需要分析自定义View中的所有元素，这些元素都需要哪些绘制
方法，然后计算确定每个元素的位置和大小，最后绘制一个静态的View
假如需求需要添加动画，就使用属性动画，动态改变绘制的值，实现一个完美的自定义View

总结以上步骤：

1.开发者确定默认绘制区域

2.自定义View告诉父布局想要的绘制区域

3.获取父布局能给的最大绘制区域，然后去矫正默认绘制区域

4.分析所有元素，安排每个元素的大小位置，绘制出静态View

5.如果有动画，添加属性动画，动态改变绘制位置的值，达到动画效果

基础文章：
* [View动画](https://juejin.im/post/5b3583016fb9a00e9e59de93)

* [属性动画](https://juejin.im/post/5b39839bf265da5984520621)

* [View基础](https://juejin.im/post/5b3b2c986fb9a04fb212855d)

AnimationButton
-------------
AnimationButton讲解文章：[自我绘制一](https://juejin.im/post/5b3f2ee1518825196b01bde7)

![效果图](https://user-gold-cdn.xitu.io/2018/7/9/1647e81d16e4bae2?imageslim)

CircleProgress
-------------
AnimationButton讲解文章：[自我绘制二](https://juejin.im/post/5b446378e51d45195b335578)

![效果图](https://user-gold-cdn.xitu.io/2018/7/10/1648324fbbd8f353?imageslim)

HorizontalProgress
------------
HorizontalProgress讲解文章：[自我绘制三](https://juejin.im/post/5b455b736fb9a04fad3a0073)

![效果图](https://user-gold-cdn.xitu.io/2018/7/11/164881b37140c40f?imageslim)

BezierLine
------------
BezierLine讲解文章：[自我绘制四](https://juejin.im/post/5b45b80cf265da0f894b5588)

![效果图](https://user-gold-cdn.xitu.io/2018/7/12/1648d887f158f59a?imageslim)

CircleBezierProgress
------------
CircleBezierProgress讲解文章：[自我绘制五](https://juejin.im/post/5b4851b1e51d4519634fa95d)

![效果图](https://user-gold-cdn.xitu.io/2018/7/13/1649283363b86013?imageslim)

BitmapView
------------
BitmapView讲解文章：[自我绘制六](https://juejin.im/post/5b486a4ce51d4518e3117551)

![效果图](https://user-gold-cdn.xitu.io/2018/7/16/164a25583e7457e1?imageslim)