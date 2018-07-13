# CustomViews

学习自定义View练手Demo

第一步：开发者确定使用者在用包裹内容时候的默认View的整体大小

第二步：把这个默认的宽高在onMeasure中告诉父布局

第三步：为了防止使用者空间不够，需要在onSizeChanged方法中去矫正可以绘制的宽高。
或者不去矫正，而是计算一个缩放比例，通过canvas.scale(scal, scal)去整体缩放绘制。怎么方便怎么来

第四步：分析需要绘制的内部所有元素需要用哪些绘制方法去绘制，开发者设置每个元素的默认大小，
然后也去矫正一下，根据矫正后的整体大小和每个元素的大小，计算出每个元素的位置坐标（这里的计算全部以控件左上为坐标原点），
然后绘制一个静态的效果。

以上四步：开发者确定了整体View的大小，内部元素的大小，通过这些确定的值，算出来内部元素的坐标。并且通过绘制方法绘制了静态的View。

第五步：添加属性动画（本质就是对值改变的操作），然后根据这些值的变化去实现动态的效果。到此一个完美的自定义View出来了！


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