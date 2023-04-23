# 乱写


- API文档(不全？)：[oracle  JavaFX 2.2](https://docs.oracle.com/javafx/2/api/index.html)	[JavaFX Introduction](https://www.demo2s.com/java/javafx-introduction.html)
- 继承类`this.Function()`为父类方法



抄的项目在：[ZeroTwo-CHEN/EIMA: 基于JavaFx的电子图片管理程序，SCAU，java课设 (github.com)](https://github.com/ZeroTwo-CHEN/EIMA)



枚举类型可以用`==`判相等，`KeyCode`和`MouseButton`都是枚举类型

点击缩略图事件在`ThumbnailPane()`中，其他在`MainViewController.Handler()`中



`ImageViewController`中按钮触发事件传参`ActionEvent event`好像可以去掉，软件生成的



javafx的一个扩展`controlsfx`用于弹出提示窗口		[Notifications (ControlsFX Project 11.1.0)](https://controlsfx.github.io/javadoc/11.1.0/org.controlsfx.controls/org/controlsfx/control/Notifications.html)





点击目录树的文件夹时，触发`Handler()`中的Listiner

缩略图的组合键控制在`Handler()`中的`previewScrollPane.setOnKeyPressed()`





---

```java
Notifications.create()
	.text("TEXT")
    .owner(stage)
    .position(Pos.TOP_CENTER)
    .hideAfter(Duration.seconds(3))
    .show();
```





---



# 包/类说明

## Controller

一个`fxml`对一个`Controller`类

### 	MainViewController & MainView.fxml & Main

控制主窗口的类，`Main`函数加载FXML时调用它的`initialize`需要初始化的事情全丢这里边了，变量上一行加了`@FXML`为fxml文件中的控件，

后面添加按钮事件都放在这里



### 	ImageViewController & ImageView.fxml & ImageMain

主窗口双击缩略图，新建了一个窗口，先调用`Main`加载`fxml`，再调用`fxml`的`init`，没有和`MainViewController`一样使用`initialize`初始化，便于后面添加功能使用不同的初始化方法，~~抄的项目也是这样的~~

#### ImageView.fxml

现布局为`BorderPane`，中间为放`ImageView`的`StackPane`，底部放`Hbox`。感觉将`BorderPane`改成`Hbox`，更合理。

## things

### 	myTreeItem

继承JavaFx的TreeItem类，实现目录树

### 	ThumbnailPane

继承JavaFx的BorderPane，显示内容由`ImageView`和`Text`组成，`imageview`放了一个图片(缩略图)，加载`Image`时需要压缩质量加载，`ImageView`控制显示出来的缩略图大小，`Text`存图片名字，当名字长度过长时截取并在末尾加`...`，初始化类时加载了鼠标点击事件，处理对图片点击的一坨事件

当图片被选择时有一个底色在`Select()`中调

有一个叫`textField`的正常情况不显示，重命名时，底部改成`textField`，按回车确定，**如果不按就会一直这样**，问题解决不了就将重命名方式改成弹出窗口。

### 	myFlowPane

继承了JavaFx自带的FlowPane布局

记录打开文件夹中的图片信息


## utils

工具包

### 	Tools

中放静态方法，完成指定事情

### 	ConstValue

放常量或者全局变量，要完成TODO中的[__[1]__](#jmp1)，可能需要对变量`ROOT_FILE`进行修改

`ROOT_FILE`：主窗口左侧目录树的根目录

`LEGAL_TYPE`：支持显示的文件类型

`STORAGE_SIZE`：。。



# TODO

## BUGS:

- [x] 主窗口`FlowPane`和`ScrollPane`同大小

- [x] 自适应（主窗口都没做）

- [x] 缩略图比例问题

- [ ] 双击打开图片后在文件夹删除(重命名)图片，会出问题，e.g. 图片被删除后，再经过被删除图片会报错，重命名也是，而且不会将命名后的文件更新进图片窗口中。

    ​	**可能的解决办法**：对每个文件夹记一个时间戳，`ImageViewController`记一个更新时的时间戳，文件夹被修改时，更新文件夹时间戳，图片窗口上下一张图片时，查一下时间戳是否对的上。<u>做了没测</u>

    ​	**方法二**：`System.currentTimeMillis()`获得当前时间戳，和`Controller`比较

    

- [ ] 在外部文件夹中移除图片，再在程序双击图片会报错~~(不打算解决)~~

- [ ] `ChoseImage`数组的顺序是乱的，需要添加排序，让两个Image数组顺序一致。

- [ ] 查一下字符串`.length()`的地方需不需要换成`Tools.getStrLength()`，将一个中文字符长度当成2。

- [ ] 看图窗口鼠标调整窗口大小太快时，图片大小调整不及时~~(不打算解决)~~

- [ ] 奇妙bug：图片缩放，不断放大缩小，图片的最小会一直变小

## list

1. 图片预览
    - [x] 目录树
        - [ ] __(add)可以通过上方选择文件夹打开__		<span id="jmp1">___[1]___</span>
    - [ ] 图片预览，缩略图
        - [x] 操作提示信息（图片个数和总大小，选中图片个数）
        - [x] 显示目录名称
        - [x] 左击图片高亮选中
            - [ ] __(add)如果是名字太长导致压缩了名字，<u>选中后显示全</u>__
        - [ ] 选择多张照片（至少1种）
            - [x] 鼠标矩形框选(上下滚没做好)
            - [x] ctrl+左键
            - [ ] __(add)shift连续选__
            - [x] __(add)ctrl+A全选__
        - [ ] 右键菜单实现以下功能
            - [x] 图片删除，删除选中的图片
            - [x] 图片复制
                - [x] 右键菜单栏
                - [x] __(add)ctrl+C__
            - [x] 图片粘贴
                - [x] 粘贴同名文件要重命名
                - [x] 右键菜单栏
                - [x] __(add)ctrl+V__
            - [ ] 重命名
                - [x] 单选
                - [ ] __(add)选中图片后点文本可以重命名__
                - [x] 多选：输入前缀，起始编号，编号位数
            - [ ] __(add)撤销__
            - [ ] __(add)剪切__
            - [ ] __(add)幻灯片播放选中的图片__
        - [x] 取消选中
            - [x] 点空白区域取消选中
            - [x] __(add)ctrl下点击选中的取消选中__
    - [ ] **(add)最上方文本框，通过修改路径更新**
        - [ ] **(add)可返回上一个访问的文件夹，访问序列的下一个文件夹，当前文件夹的上一级**
        - [ ] **(add)右侧搜索框，根据关键字搜索图片**
    - [ ] _(优化)当鼠标靠近窗口右侧/下侧右键时菜单出现在鼠标左侧/上侧_
2. 幻灯片播放
    - [x] 主窗口幻灯片播放按钮
    - [ ] __(add)播放当前文件夹下的所有图片(含子文件夹内的)__
    - [x] 双击图片，新窗口显示图片
    - [ ] 幻灯片窗口
        - [x] 图片展示
        - [ ] 操作功能
            - [ ] 前后一张图片
                - [x] 按钮
                - [ ] __(add)左右键__
            - [ ] 放大缩小按钮
                - [x] 按钮
                - [ ] __(add)滚轮__
            - [ ] __(add)鼠标拖动图片__
            - [x] 幻灯片播放按钮
                - [x] 设置时间间隔，有退出功能
    - [ ] __(add)播放幻灯片时左右键可以强行切换图片__
    - [ ] __(add)幻灯片播放选中的图片(好像要大改ImageMain)__
    - [ ] _(优化)新建窗口时图片显示的怪问题_

