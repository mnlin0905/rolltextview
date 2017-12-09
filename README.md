# RollTextView
[![License](https://img.shields.io/aur/license/yaourt.svg)](http://www.gnu.org/licenses/gpl-3.0.html)

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
    exclude group:'com.jakewharton', module:'butterknife'
    exclude group:'com.jakewharton', module:'butterknife-compiler'    
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
#### 1、在需要显示滚动信息的xml文件中添加控件；
```
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout
    xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:id="@+id/activity_splash"
    android:orientation="vertical"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fitsSystemWindows="false">

    <com.knowledge.mnlin.rolltextview.RollTextView
        android:id="@+id/rtv_temp"
        android:layout_marginTop="100dp"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"/>

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

#### 3、查看效果

## 3、自定义属性
#### 1、 可设属性总览


## 2、注意事项
在进行数据初始化时，必须保证最后调用**refreshData**方法，否则可能设置会不起作用

_注意：由于ListView与RecyclerView的子View复用，因此展开内容在滑动到不可见区域后会自动折叠。_


