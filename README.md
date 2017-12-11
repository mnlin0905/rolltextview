# RollTextView
[![License](https://img.shields.io/aur/license/yaourt.svg)](http://www.gnu.org/licenses/gpl-3.0.html)
[![Download](https://api.bintray.com/packages/lovingning/maven/rolltextview/images/download.svg) ](https://bintray.com/lovingning/maven/rolltextview/_latestVersion)

`
说明：一个可以滚动的布局，可以在首页显示新闻消息等，可自定义每次显示的数目，动画时间等...
`

`
原理：RollTextView继承RecyclerView，因此实际上并非是TextView，而是ViewGroup，在控件内容重写的定时器，保证按照某种约束进行滚动。
`

##1、准备步骤
在项目**build.gradle**中添加依赖：
```
compile 'com.knowledge.mnlin:rolltextview:0.0.1'
```

若项目依赖有冲突，则可以屏蔽该库中依赖的资源
```
compile ('com.knowledge.mnlin:rolltextview:0.0.1'){
    exclude group:'com.android.support', module:'recyclerview-v7'
    
    ...
    
}
```

如果提示找不到依赖文件，可能时jcenter未及时通过，可以依赖私人仓库
```
//Project的build.gradle文件
allprojects {
    repositories {
    
        ... 
        
        maven { url "https://dl.bintray.com/lovingning/maven"}
    }
}
```

## 2、简单使用
#### 1、在需要显示滚动信息的layout文件中添加控件；
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:id="@+id/activity_splash"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="false"
    android:orientation="vertical">

    <View
        android:layout_marginTop="100dp"
        android:background="@color/red"
        android:layout_width="match_parent"
        android:layout_height="1px"/>

    <com.knowledge.mnlin.RollTextView
        android:id="@+id/rtv_temp"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>

    <View
        android:background="@color/red"
        android:layout_width="match_parent"
        android:layout_height="1px"/>

</LinearLayout>
```

#### 2、在Activity或Fragment中初始化数据
```
RollTextView rollTextView = findViewById(R.id.rtv_temp);
rollTextView.refreshData(Arrays.asList("0000000"
                , "1111111111111111111111111111111111111111111111111111"
                , "22222"
                , "3333333"
                , "444444"
                , "55555"
                , "6666666"
                , "777777"
                , "888888"
                , "99999999"));
```

## 3、进阶属性

**可设属性总览：**

```
rollTextView.setAppearCount(3)//设置每次显示的数量
        .setInterval(2000)//设置滚动的间隔时间，以毫秒为单位
        .setOrderVisible(true)//设置显示序号的view是否可见
        .setEndText("查看", true)//设置尾部文字是否可见
        .setLayoutResource(R.layout.item_roll_text_view)//为item自定义layout,但必须遵循规定
        .setRollDirection(1)//设置滚动的方向，0为向上滚动，1为向下滚动，2为向右滚动，3为向左滚动
        //设置itemClick监听器
        .setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Logger.e("点击位置：" + position);
            }
        })
        //设置数据源，接收一个list，显示内容时调用其toString方法，因此数据内容不可为null
        .refreshData(Arrays.asList("0000000"
                , "1111111111111111111111111111111111111111111111111111"
                , "22222"
                , "3333333"
                , "444444"
                , "55555"
                , "6666666"
                , "777777"
                , "888888"
                , "99999999"));
```

#### 1、使用setAppearCount

该方法可以设置每次显示的条目，默认情况下，只显示一条信息。

#### 2、setInterval

设置滚动时间间隔，默认情况下为2000ms

#### 3、setOrderVisible与setEndText

默认情况下，会为每条信息添加序号，并在尾部添加lable：“更多”，可通过方法关闭显示效果

#### 4、setLayoutResource自定义item布局

如果不满足预设的item-layout文件，可以**自定义xml布局**；

 * 定义布局时尽量不要做过多修改，避免滑动失败；
 * 自定义布局中**必须有三个AppCompatTextView子view**，并且id分别为：**tv_left、tv_center、tv_right**。
 * 定义布局时，root布局上**不要使用margin***属性**，避免滚动时错位。


系统默认的xml文件为：
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:minHeight="@dimen/prefer_view_height"
    android:orientation="horizontal">

    <android.support.v7.widget.AppCompatTextView
        android:id="@+id/tv_left"
        android:layout_width="@dimen/prefer_view_height"
        android:layout_height="match_parent"
        android:gravity="center_vertical"
        android:paddingLeft="@dimen/prefer_view_padding_vertical"
        android:text="1."/>

    <android.support.v7.widget.AppCompatTextView
        android:id="@+id/tv_center"
        style="@style/TextViewStandard"
        android:layout_width="0dp"
        android:layout_height="match_parent"
        android:layout_weight="1"
        android:ellipsize="marquee"
        android:gravity="center_vertical"
        android:marqueeRepeatLimit="marquee_forever"
        android:singleLine="true"
        android:text="很多内容很多内容很多内容很多内容很多内容很多内容很多内容"/>

    <android.support.v7.widget.AppCompatTextView
        android:id="@+id/tv_right"
        style="@style/TextViewStandard"
        android:layout_width="wrap_content"
        android:layout_height="match_parent"
        android:text="详情"/>
</LinearLayout>
```

#### 5、使用setRollDirection设置滚动方向

默认情况下，控件从下向上滚动；当然可以通过该方法来设置滚动的方向：

 * 0 表示向上滚动
 * 1 表示向下滚动
 * 2 表示向右滚动
 * 3 表示向左滚动

#### 6、refreshData

refreshData方法需要在初始化的最后执行，该方法会将之前所有的设置进行apply，然后再次刷新动画。
因此务必保证：**refreshData必须调用并且最后调用**

#### 7、主动开启或关闭动画

库中已经对动画的运行与停止自定义了处理方法，在控件不可见时，动画会自动关闭，且控件可以手动滑动。若有特殊需求需要控件停止或启动动画，可以主动调用方法：
 * **startAnimation()** 开启动画
 * **stopAnimation()** 关闭动画
 
具体效果可参考博客：[RollTextView](http://blog.csdn.net/lovingning/article/details/78774436)


