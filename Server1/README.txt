一.java文件介绍：
HTTPServer.java：主类，通过运行这个类运行服务器
Cache.java：管理缓存的类
Request.java：管理接受的请求的类
ResponseProcessor.java：发送响应的类

二.运行方式：
1.将Server1文件夹导入idea：
打开idea编译器，选择file->new->project form existing source；然后选择Server1文件夹导入即可，后面如果还有要选择的话就选择project form existing source。
2.导入后运行HTTPServer.java文件，就会启动服务器（已经注释掉https功能，需要的往下看）

三、.导入证书（如果不检验https功能可以不管这步）
在运行服务器，并且想运行https的功能前，必须先生成证书：
步骤如下：
1.在Server1根目录下打开控制台
2.依次输入以下几句命令：
keytool -genkey -alias serverkey -keystore kserver.keystore
keytool -export -alias serverkey -keystore kserver.keystore -file server.crt
keytool -import -alias serverkey -file server.crt -keystore tclient.keystore
keytool -genkey -alias clientkey -keystore kclient.keystore
keytool -export -alias clientkey -keystore kclient.keystore -file client.crt
keytool -import -alias clientkey -file client.crt -keystore tserver.keystore

在生成过程中就按照控制台的指令输入其他一些东西即可。
生成成功后，该目录底下就会多了几个keystore和证书

四、检验https功能
为了不报错，我已经将HTTPServer.java的42-65行注释，如果生成证书成功，可以将其取消注释就能尝试https功能。但也有可能因为证书不合格而无法访问服务器，是电脑的证书问题（我在重装电脑之前就有这个问题）
