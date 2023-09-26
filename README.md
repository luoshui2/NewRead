# NewRead
## 简介
android新闻列表阅读器
## 功能
能够获取网络上面的新闻并且可以查看新闻的详情
## 技术
1.构建了新闻类的适配器，加载listview：NewsAdapter    
2.创建了接口信息类，存储api接口的基本信息：Constants  
3.创建了新闻类，存储新闻的基本信息：News  
4.创建了新闻请求类，设置api接口的参数信息：NewsRequest  
5.创建子线程，利用request类进行url请求，并且进行网络同步（callback）：refreshData  
6.利用okhttp进行网络请求成功或者失败后的操作，请求成功后进行指定的json解析，获取新闻的基本信息，并且传入适配器：callback    
7.设置listview的点击事件，跳转到新闻的详情页面  
8.新闻详情页面用webview组件进行加载url显示    
9.webview设置js,并且设置url的intent被拒绝后的方法，保证能够正常打开网页不会出现空白    
10.利用js进行网页的信息屏蔽，利用selector筛选出指定的信息
## 效果显示
1.新闻列表界面：![Image](https://github.com/luoshui2/NewRead/blob/master/app/src/main/res/drawable/Screenshot_20230926_104456.png)  
2.新闻详情界面：![Image](https://github.com/luoshui2/NewRead/blob/master/app/src/main/res/drawable/Screenshot_20230926_104515.png)
