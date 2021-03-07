package com.company;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Properties;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.*;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.*;
import org.eclipse.swt.events.*;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import org.eclipse.swt.*;
import org.eclipse.swt.graphics.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.events.*;

public class Demo {
    public static void main(String args[]) throws Exception {
        Demo d=new Demo();
    }

    /**
     * Trust every server - dont check for any certificate
     */
    public  void trustAllHosts() {
        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return new java.security.cert.X509Certificate[] {};
            }
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                Log.i(TAG, "checkClientTrusted");
            }
            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
//                Log.i(TAG, "checkServerTrusted");
            }
        } };
        // Install the all-trusting trust manager
        try {
            SSLContext sc = SSLContext.getInstance("TLS");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Demo() throws Exception {
        //Displays用于管理事件循环（event loops）和控制UI线程和其他线程之间的通讯
        Display display=new Display();
        //Shell是应用程序中由操作系统窗体管理器来管理的窗体
        Shell shell=new Shell(display);
        //浏览器设置名字
        shell.setText("MyBrowser");
        shell.setSize(1300, 700);
//        Color myclor=new Color(null,238,7,7,1);
        //设置颜色
//        Color myclor=new Color(238,7,7,1);
//        shell.setBackground(myclor);
//        shell.setBackgroundMode(SWT.INHERIT_FORCE);

//        shell.setAlpha(10);

//        Canvas canvas = new Canvas(shell, SWT.BACKGROUND);
//        //使用paintlistener，保证每次均重新绘制。
//        canvas.addPaintListener(new PaintListener() {
//            public void paintControl(PaintEvent e) {
//                GC gc = e.gc;
//                //读图像
//                ImageData imageData = new ImageData("bg.jpg");
//                //这里是建立从左到右的渐进Alpha。
//                byte[] alphaValues = new byte[imageData.height * imageData.width];
//                for (int j = 0; j < imageData.height; j++) {
//                    for (int i = 0; i < imageData.width; i++) {
//                        alphaValues[j * imageData.width + i] = (byte) (255 - 255
//                                * i / imageData.width);
//                    }
//                }
//                imageData.alphaData = alphaValues;
//                Image image = new Image(display, imageData);
//                //绘制
//                gc.drawImage(image,0,0);
//            }
//        });


        //设置Shell的Layout manager
        shell.setLayout(new FormLayout());
        //这里第一个参数还是用了Shell类.因为Shell类属于Composite的子类.所以shell也可以当做Composite类型来用.
        //Composite的式样一般都是用SWT.NONE,这时Composite在界面是不显示出来的.
        Composite control = new Composite(shell, SWT.NONE);
//        control.setBackground(new Color(display,153,153,153,1));
        Image image=new Image(display,"bg4.jpg");
        control.setBackgroundImage(image);
        control.setBackgroundMode(SWT.INHERIT_FORCE);
        //FormData作用1、将form表单元素的name与value进行组合，实现表单数据的序列化，从而减少表单元素的拼接，提高工作效率。
        //2、异步上传文件
        FormData data = new FormData();
        data.top = new FormAttachment(0, 0);
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        control.setLayoutData(data);

        //Label是一个标签组件
        Label status = new Label(shell, SWT.NONE);
        data = new FormData();
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(0, 0);
        data.bottom = new FormAttachment(100, 0);
        status.setLayoutData(data);

        final Browser browser = new Browser(shell, SWT.BORDER);
        data = new FormData();
        data.top = new FormAttachment(control);
        data.bottom = new FormAttachment(status);
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        browser.setLayoutData(data);

        control.setLayout(new GridLayout(7, false));
        //先将按钮所需的类实例化
        Log log = new Log();
//     Download download = new Download(log);//计入日志中
//        Download download = new Download();

        downloadfile dl=new downloadfile();
        fileUI fileui=new fileUI();
        ArrayList<savefile> filelist = new ArrayList<savefile>();
//        inifilelist(filelist);

        E_mail email = new E_mail(log);
        BookMark bookmark = new BookMark(log);
        History history = new History(log);
        //设置主菜单
        //新建一个menu bar要以下面的格式开头:
        Menu menu = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menu);
        //下面加一个item到menu bar:
        MenuItem file = new MenuItem(menu, SWT.CASCADE);
        //style必须是SWT.CASCADE,否则就不能跟file加drop-down menu
        file.setText("文件");
        MenuItem histo = new MenuItem(menu, SWT.CASCADE);
        histo.setText("历史记录");
        MenuItem downlo = new MenuItem(menu, SWT.CASCADE);
        downlo.setText("下载");
        MenuItem ema = new MenuItem(menu, SWT.CASCADE);
        ema.setText("邮件");
        MenuItem book = new MenuItem(menu, SWT.CASCADE);
        book.setText("书签");
        MenuItem help = new MenuItem(menu, SWT.CASCADE);
        help.setText("帮助");


        //现在给file加个menu,style必须是SWT.DROP_DOWN
        Menu filemenu = new Menu(shell, SWT.DROP_DOWN);
        file.setMenu(filemenu);
        //给file添加选项
        MenuItem getHtml = new MenuItem(filemenu,SWT.CASCADE);
        getHtml.setText("保存网页");
        MenuItem exit = new MenuItem(filemenu, SWT.CASCADE);
        exit.setText("退出");

        Menu historyMenu = new Menu(shell, SWT.DROP_DOWN);
        histo.setMenu(historyMenu);
        //给历史记录添加选项
        MenuItem check = new MenuItem(historyMenu, SWT.CASCADE);//查看历史记录
        check.setText("查看过往网页记录");
        MenuItem delete = new MenuItem(historyMenu,SWT.CASCADE);
        delete.setText("删除过往网页记录");

        Menu bookMenu = new Menu(shell, SWT.DROP_DOWN);
        book.setMenu(bookMenu);
        //给书签添加选项
        MenuItem addMark = new MenuItem(bookMenu, SWT.CASCADE);//添加书签
        addMark.setText("添加为书签");
        MenuItem checkMark = new MenuItem(bookMenu, SWT.CASCADE);//查看书签
        checkMark.setText("查看书签");

        //给下载添加选项
        Menu downMenu = new Menu(shell, SWT.DROP_DOWN);
        downlo.setMenu(downMenu);
        MenuItem downloading = new MenuItem(downMenu, SWT.CASCADE);
        downloading.setText("下载资源");
        MenuItem checkload = new MenuItem(downMenu, SWT.CASCADE);
        checkload.setText("查看下载情况");

        //给邮件添加选项
        Menu mailMenu = new Menu(shell, SWT.DROP_DOWN);
        ema.setMenu(mailMenu);
        MenuItem sendMail = new MenuItem(mailMenu, SWT.CASCADE);
        sendMail.setText("发送邮件");

        //为帮助添加选项
        Menu helpMenu = new Menu(shell, SWT.DROP_DOWN);
        help.setMenu(helpMenu);
        MenuItem search = new MenuItem(helpMenu, SWT.CASCADE);
        search.setText("查找解决方案");
        MenuItem about = new MenuItem(helpMenu, SWT.CASCADE);
        about.setText("关于版本");
        Button button;
        //使用SWT内部的按钮来实现后退，前进和刷新
        button=new Button(control,SWT.ARROW|SWT.LEFT);

        //后退按钮
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                browser.back();
            }
        });

        //Forward********************************************

        //前进按钮
        button=new Button(control,SWT.ARROW|SWT.RIGHT);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                browser.forward();
            }
        });

        //显示网址的输入框
        Label ipLocation = new Label(control, SWT.LEFT);
        ipLocation.setText("请输入网址：");
        //四个参数分别代表组件的新 x 坐标，新 y 坐标，新 width，新 height
        ipLocation.setBounds(105, 5, 100, 25);
        Text iptext = new Text(control, SWT.BORDER);
        iptext.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        iptext.setFocus();

        Button enter = new Button(control, SWT.PUSH);
        enter.setBounds(220,5,100,25);
        enter.setText("搜索");

        //刷新按钮
        button=new Button(control,SWT.PUSH);
        button.setText("刷新");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                browser.refresh();
                String url=browser.getUrl();
                iptext.setText(url);
            }
        });

        //停止加载按钮
        button=new Button(control,SWT.PUSH);
        button.setText("停止加载");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                browser.stop();
            }
        });

        //在输入网址并且敲回车键后
        iptext.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.keyCode==SWT.CR||e.keyCode==SWT.KEYPAD_CR)
                {
                    String input=iptext.getText().trim();
                    //对输入的网址进行判断
                    if(input.length()==0)
                    {
//                        JOptionPane.showMessageDialog(null, "网址为空！","Error", JOptionPane.INFORMATION_MESSAGE);
                        MessageBox box=new MessageBox(shell,SWT.ICON_WARNING|SWT.YES);
                        box.setText("");
                        box.setMessage("网址为空，请重新输入");
                        int choice=box.open();
                        if(choice==SWT.YES)
                        {
                            System.out.println("Yes");

                        }
                        return;
                    }

                    if (input.endsWith(".com")||input.endsWith(".net")||input.endsWith(".cn")
                            ||input.contains("/")||input.contains(".com")||input.contains(".net")
                            ||input.contains(".cn"))
                    {
                        if(!input.startsWith("http://")&&!input.startsWith("https://"))
                        {
                            input="http://"+input;//当输入有效后缀时，自动添加http://前缀
                            iptext.setText(input);
                        }
                    }else{
                        String key= null;
                        try {
                            key = URLEncoder.encode(input,"UTF-8");
                        } catch (UnsupportedEncodingException ex) {
                            ex.printStackTrace();
                        }
                        input="https://www.baidu.com/s?ie=UTF-8&wd="+key;//不是网页改百度搜索
                        iptext.setText(input);
                    }
                    browser.setUrl(input);
                }
            }
        });

        //点击按钮进入网页
        enter.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                String input=iptext.getText().trim();
                if(input.length()==0)
                {
//                    JOptionPane.showMessageDialog(null,"网址为空！","Error",JOptionPane.INFORMATION_MESSAGE);
                    MessageBox box=new MessageBox(shell,SWT.ICON_WARNING|SWT.YES);
                    box.setText("");
                    box.setMessage("网址为空，请重新输入");
                    int choice=box.open();
                    if(choice==SWT.YES)
                    {
                        System.out.println("Yes");

                    }
                    return;
                }
                if (input.endsWith(".com")||input.endsWith(".net")||input.endsWith(".cn")
                        ||input.contains("/")||input.contains(".com")||input.contains(".net")
                        ||input.contains(".cn")){
                    if(!input.startsWith("http://")&&!input.startsWith("https://")) {
                        input="http://"+input;
                        iptext.setText(input);
                    }
                }else{
                    try {
                        String key= URLEncoder.encode(input,"UTF-8");
                        input="https://www.baidu.com/s?ie=UTF-8&wd="+key; //不是网页的话转为百度搜索
                        iptext.setText(input);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
                browser.setUrl(input);
            }
        });

        //监听键盘，点击超链接时及时跳转
        browser.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {    //跳转超链接
                if(e.keyCode==SWT.CR||e.keyCode==SWT.KEYPAD_CR){
                    String str=browser.getUrl();
                    iptext.setText(str);
                }
            }
        });
        //监听鼠标，点击超链接时及时跳转
        browser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                String str=browser.getUrl();
                iptext.setText(str);
            }
        });

        //打开新网页
        browser.addOpenWindowListener(new OpenWindowListener() {
            public void open(WindowEvent windowEvent) {
                final Shell shell_1= new Shell(shell);
                final Browser brow= new Browser(shell_1,SWT.NONE);
                windowEvent.browser= brow;
                windowEvent.display.asyncExec(new Runnable() {
                    public void run() {
                        String str= brow.getUrl();
                        if (str.endsWith(".exe")||str.endsWith(".zip")||str.endsWith(".rar")
                                ||str.endsWith(".txt")||str.endsWith(".pdf")||str.endsWith(".mp3")
                                ||str.endsWith(".doc")||str.endsWith(".docx")||str.endsWith(".jpg")
                                ||str.endsWith(".png")||str.endsWith(".gif")||str.endsWith(".swf")
                                ||str.endsWith(".mp4")||str.endsWith(".avi")||str.endsWith(".mkv")){
//                         download.createUI();//下载资源

                        }else{
                            browser.setUrl(str);
                            iptext.setText(str);
                            shell_1.close();
                        }
                    }

                });
            }
        });

        browser.addLocationListener(new LocationListener() {
            @Override
            public void changing(LocationEvent locationEvent) {
                //iptext.setText("Loading.......");
            }

            @Override
            public void changed(LocationEvent locationEvent) {  //改变地址栏
                String url=browser.getUrl();
                String name=browser.getText();
                iptext.setText(url);
//                解析URL地址
                { Document doc = null;
                try {
                    trustAllHosts();
                    doc = Jsoup.parse(new URL(url),30000);
                } catch (IOException e) {
                    System.out.println(e);
                    e.printStackTrace();

                    {
                        MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES);
                        box.setText("错误");
                        box.setMessage("网页加载超时");
                        int choice = box.open();
                        if (choice == SWT.YES) {

                        }
                    }
                }}

                try{

                    //使用标签选择器，获取title标签中的内容
//                    String title=doc.getElementsByTag("title").first().text();
                    //将获取到的信息进行打印
//                    System.out.println(title);
                    String title="unkonwn";
                    history.setNewRecord(url,title);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });

        getHtml.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                System.out.println("保存网页");

                MessageBox box=new MessageBox(shell,SWT.ICON_INFORMATION|SWT.YES|SWT.NO);
                box.setText("保存网页");
                box.setMessage("是否确认保存该网页");
                int choice=box.open();
                if(choice==SWT.YES)
                {
                    System.out.println("Yes");
                    savePage savepage=new savePage(browser,iptext,log);
                }
                else if(choice==SWT.NO){
                    System.out.println("no");
                }
            }
        });

        exit.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
//                int result=JOptionPane.showConfirmDialog(null,"确认关闭浏览器？",
//                        "Warning",JOptionPane.YES_NO_OPTION);
//                if (result==JOptionPane.YES_OPTION){
//                    JOptionPane.showMessageDialog(null,"祝您今日愉快！",
//                            "Message",JOptionPane.INFORMATION_MESSAGE);
//                    shell.close();
//                    shell.dispose();
//                }
                MessageBox box=new MessageBox(shell,SWT.ICON_INFORMATION|SWT.YES|SWT.NO);
                box.setText("关闭浏览器");
                box.setMessage("是否确认关闭浏览器？");
                int choice=box.open();
                if(choice==SWT.YES)
                {
                    shell.close();
                    shell.dispose();
                }
                else if(choice==SWT.NO){
                    System.out.println("no");
                }
            }
        });

        //********************************************

        check.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {  //查看历史
                int result=CheckHistory(shell,history,browser,iptext,display);
                if(result == 0){
//                    JOptionPane.showMessageDialog(null,"没有历史记录!",
//                            "Error",JOptionPane.INFORMATION_MESSAGE);
                    MessageBox box=new MessageBox(shell,SWT.ICON_WARNING|SWT.YES);
                    box.setText("");
                    box.setMessage("没有历史记录");
                    int choice=box.open();
                    if(choice==SWT.YES)
                    {
                        System.out.println("Yes");

                    }
                }
            }
        });

        delete.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {  //删除历史
                delHistory(shell,history);
            }
        });

        //********************************************

        addMark.addSelectionListener(new SelectionAdapter() {   //增加新书签事件
            @Override
            public void widgetSelected(SelectionEvent e) {  //添加书签
                String url=browser.getUrl();
                if(!url.isEmpty()){
                    bookmark.setNewMark(url);
                }
                else{

                    MessageBox box=new MessageBox(shell,SWT.ICON_WARNING|SWT.YES);
                    box.setText("");
                    box.setMessage("书签为空");
                    int choice=box.open();
                    if(choice==SWT.YES)
                    {
                        System.out.println("Yes");

                    }
                }
            }
        });

        checkMark.addSelectionListener(new SelectionAdapter() {   //查看书签事件
            @Override
            public void widgetSelected(SelectionEvent e) {  //查看书签
                if(bookmark.list.size()==0){

                    MessageBox box=new MessageBox(shell,SWT.ICON_WARNING|SWT.YES);
                    box.setText("");
                    box.setMessage("书签为空！");
                    int choice=box.open();
                    if(choice==SWT.YES)
                    {
                        System.out.println("Yes");

                    }
                }else{
                    viewBookmarks(shell,bookmark,browser,iptext,display);
                }
            }
        });


        downloading.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                dl.createUI(filelist,display);
            }
        });

        checkload.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent selectionEvent) {
                fileui.createUI(filelist,display);
            }

        });

        sendMail.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                email.createUI(display);
            }
        });

        about.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                MessageBox box=new MessageBox(shell,SWT.ICON_INFORMATION|SWT.YES);
                box.setText("关于");
                box.setMessage("关麒晖 mybrowser 1.0");
                int choice=box.open();
                if(choice==SWT.YES)
                {
                    System.out.println("Yes");

                }
            }
        });

        search.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                Shell seashell=new Shell(shell);
                seashell.setText("搜索");
                seashell.setSize(450,85);
                seashell.setLayout(new GridLayout(2,false));

                Text text=new Text(seashell, SWT.CENTER|SWT.BORDER);
                text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                Button ok=new Button(seashell,SWT.PUSH);
                ok.setText("完成");

                text.addKeyListener(new KeyAdapter() {  //回车触发
                    @Override
                    public void keyReleased(KeyEvent e) {
                        if (e.keyCode==SWT.CR||e.keyCode==SWT.KEYPAD_CR){
                            String str=text.getText();
                            while(str.equals("")){
                                MessageBox box=new MessageBox(shell,SWT.ICON_WARNING|SWT.YES);
                                box.setText("");
                                box.setMessage("为空");
                                int choice=box.open();
                                if(choice==SWT.YES)
                                {
                                    System.out.println("Yes");

                                }
                                str=text.getText();
                            }

                            String key= null;
                            try {
                                key = URLEncoder.encode(str,"UTF-8");
                            } catch (UnsupportedEncodingException ex) {
                                ex.printStackTrace();
                            }
                            str="https://www.baidu.com/s?ie=UTF-8&wd="+key; //不是网页的话转为百度搜索

                            browser.setUrl(str);
                            seashell.dispose();
                        }
                    }
                });

                ok.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {  //点击触发
                        String str=text.getText();
                        while(str.equals("")){
                             MessageBox box=new MessageBox(shell,SWT.ICON_WARNING|SWT.YES);
                            box.setText("");
                            box.setMessage("为空");
                            int choice=box.open();
                            if(choice==SWT.YES)
                            {
                                System.out.println("Yes");

                            }
                            str=text.getText();
                        }

                        String key= null;
                        try {
                            key = URLEncoder.encode(str,"UTF-8");
                        } catch (UnsupportedEncodingException ex) {
                            ex.printStackTrace();
                        }
                        str="https://www.baidu.com/s?ie=UTF-8&wd="+key;
                        browser.setUrl(str);

                        seashell.dispose();
                    }
                });

                seashell.open();

            }
        });


        shell.open();
        while (!shell.isDisposed()) {
            if (!display.readAndDispatch())
                display.sleep();
        }

        display.dispose();
    }

    public static int CheckHistory(Shell parent,History history,
                                   Browser browser,Text text,Display display){
        int num=history.getNum();
        if (num==0)
            return 0;
        Shell hishell=new Shell(parent,SWT.RESIZE|SWT.MAX);
        hishell.setText("历史记录");
        Image image=new Image(display,"bg4.jpg");
        hishell.setBackgroundImage(image);
        hishell.setBackgroundMode(SWT.INHERIT_FORCE);
        RowLayout layout=new RowLayout(SWT.VERTICAL);
        layout.marginTop = 10;
        layout.marginBottom = 10;
        layout.marginLeft = 5;
        layout.marginRight = 5;
        layout.spacing = 10;
        hishell.setLayout(layout);

        Button bm[]=new Button[num];
        for(int i=0;i<num;i++){
            int index=i;
            bm[i]=new Button(hishell,SWT.PUSH);
            bm[i].setText(history.list.get(i).name);
            bm[i].setToolTipText(history.list.get(i).url);
            bm[i].setLayoutData(new RowData(250,30));

            bm[i].addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    String url=history.list.get(index).url;
                    browser.setUrl(url);
                    text.setText(url);
                    hishell.dispose();
                }
            });
        }

        Menu deleteBar=new Menu(hishell,SWT.BAR);
        hishell.setMenuBar(deleteBar);

        MenuItem deleteAll=new MenuItem(deleteBar,SWT.CASCADE);
        deleteAll.setText("删除历史记录");

        deleteAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                MessageBox box=new MessageBox(browser.getShell(),SWT.ICON_INFORMATION|SWT.YES|SWT.NO);
                box.setText("删除历史");
                box.setMessage("确认删除历史记录？");
                int choice=box.open();
                if(choice==SWT.YES)
                {
                    history.deleteAll();
                    MessageBox box2=new MessageBox(browser.getShell(),SWT.ICON_INFORMATION|SWT.YES);
                    box2.setText("删除历史");
                    box2.setMessage("删除成功");
                    int choice2=box.open();
                    if(choice2==SWT.YES)
                    {

                    }
                    hishell.dispose();
                }
            }
        });

        hishell.pack();
        hishell.open();

        return 1;
    }

    public static void delHistory(Shell his_shell,History history){
        MessageBox box=new MessageBox(his_shell,SWT.ICON_INFORMATION|SWT.YES|SWT.NO);
        box.setText("删除历史");
        box.setMessage("确认删除所有历史记录吗？");
        int choice=box.open();
        if(choice==SWT.YES)
        {
            history.deleteAll();

            MessageBox box2=new MessageBox(his_shell,SWT.ICON_INFORMATION|SWT.YES);
            box2.setText("删除历史");
            box2.setMessage("成功");
            int choice2=box.open();
            if(choice2==SWT.YES)
            {

            }
        }
        if(choice==SWT.NO)
        {

        }

    }

    public static void delAllBookmarks(Shell bmshell,BookMark bookmarks){

        MessageBox box=new MessageBox(bmshell,SWT.ICON_INFORMATION|SWT.YES|SWT.NO);
        box.setText("删除书签");
        box.setMessage("确认删除所有书签？");
        int choice=box.open();
        if(choice==SWT.YES)
        {
            bookmarks.deleteAll();
            MessageBox box2=new MessageBox(bmshell,SWT.ICON_INFORMATION|SWT.YES);
            box2.setText("删除书签");
            box2.setMessage("删除成功");
            int choice2=box.open();
            if(choice2==SWT.YES)
            {

            }
        }
    }


    public static Integer delOneBookmark(Shell parent,BookMark bookmarks){

        Shell delshell=new Shell(parent,SWT.RESIZE|SWT.MAX);
        delshell.setLayout(new RowLayout());

        Label index=new Label(delshell,SWT.LEFT);
        index.setText("请输入要删除的书签序号: ");

        Text text=new Text(delshell,SWT.BORDER|SWT.SINGLE|SWT.CENTER);
        text.setFocus();

        Button check=new Button(delshell,SWT.PUSH);
        check.setText("删除该书签");

        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.keyCode==SWT.CR||e.keyCode==SWT.KEYPAD_CR){
                    String str=text.getText().trim();
                    int n=Integer.parseInt(str);
                    if(n<=0||n>bookmarks.list.size()){
//                        JOptionPane.showMessageDialog(null,"为空!",
//                                "Error",JOptionPane.INFORMATION_MESSAGE);
                        MessageBox box=new MessageBox(delshell,SWT.ICON_ERROR|SWT.YES);
                        box.setText("错误");
                        box.setMessage("为空");
                        int choice=box.open();
                        if(choice==SWT.YES)
                        {

                        }
                    }
                    else{
                        bookmarks.delete(n-1);
//                        JOptionPane.showMessageDialog(null,"成功!",
//                                "Message",JOptionPane.INFORMATION_MESSAGE);
                        MessageBox box=new MessageBox(delshell,SWT.ICON_INFORMATION|SWT.YES);
                        box.setText("");
                        box.setMessage("成功");
                        int choice=box.open();
                        if(choice==SWT.YES)
                        {

                        }
                        delshell.dispose();
                        parent.dispose();

                    }
                }
            }
        });

        check.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                String str=text.getText().trim();
                int n=Integer.parseInt(str);
                if(n<=0||n>bookmarks.list.size()){
//                    JOptionPane.showMessageDialog(null,"该序号不存在!",
//                            "Error",JOptionPane.INFORMATION_MESSAGE);
                    MessageBox box=new MessageBox(delshell,SWT.ICON_ERROR|SWT.YES);
                    box.setText("错误");
                    box.setMessage("该序号不存在");
                    int choice=box.open();
                    if(choice==SWT.YES)
                    {

                    }
                }
                else{
                    bookmarks.delete(n-1);
//                    JOptionPane.showMessageDialog(null,"成功!",
//                            "Message",JOptionPane.INFORMATION_MESSAGE);
                    MessageBox box=new MessageBox(delshell,SWT.ICON_INFORMATION|SWT.YES);
                    box.setText("");
                    box.setMessage("成功");
                    int choice=box.open();
                    if(choice==SWT.YES)
                    {

                    }
                    delshell.dispose();
                    parent.dispose();
                }
            }
        });

        delshell.pack();
        delshell.open();

        return 0;
    }

    public static Shell viewBookmarks(Shell parent,BookMark bookmarks
            ,Browser browser,Text text,Display display){

        Shell bmshell=new Shell(parent,SWT.RESIZE|SWT.MAX);
        bmshell.setText("书签");
        Image image=new Image(display,"bg4.jpg");
        bmshell.setBackgroundImage(image);
        bmshell.setBackgroundMode(SWT.INHERIT_FORCE);
        int num=bookmarks.getNum();
        RowLayout layout=new RowLayout(SWT.VERTICAL);
        layout.marginTop = 10;
        layout.marginBottom = 10;
        layout.marginLeft = 5;
        layout.marginRight = 5;
        layout.spacing = 10;
        bmshell.setLayout(layout);
        Button bm[]=new Button[num];
        for(int i=0;i<num;i++){
            int index=i;
            bm[i]=new Button(bmshell,SWT.PUSH);
            bm[i].setText(bookmarks.list.get(i).record);
            bm[i].setToolTipText(bookmarks.list.get(i).url);
            bm[i].setLayoutData(new RowData(250,30));   //Button size

            bm[i].addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    String url=bookmarks.list.get(index).url;
                    browser.setUrl(url);
                    text.setText(url);
                    bmshell.dispose();
                }
            });
        }

        //顶部菜单栏 删除/删除全部

        Menu deleteBar=new Menu(bmshell,SWT.BAR);
        bmshell.setMenuBar(deleteBar);

        MenuItem delOne=new MenuItem(deleteBar,SWT.CASCADE);
        delOne.setText("删除一个书签");
        MenuItem delAll=new MenuItem(deleteBar,SWT.CASCADE);
        delAll.setText("删除所有书签");

        delOne.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                int flag=delOneBookmark(bmshell,bookmarks);
                if(flag==1){
                    bmshell.dispose();
                }
            }
        });

        delAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                delAllBookmarks(bmshell,bookmarks);
                bmshell.dispose();
            }
        });

        bmshell.pack();
        bmshell.open();

        return bmshell;
    }

    public static void inifilelist(ArrayList<savefile> fl) throws Exception {
        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File("download.txt")),
                "UTF-8"));
        String lineTxt = null;
        while ((lineTxt = br.readLine()) != null) {
            String[] names = lineTxt.split(" ");
            String url=names[0];
            String name=names[1];
            String path=names[2];
            double size=Double.parseDouble(names[3]);
            File file=new File(path);
            double len=(int)file.length();
            double ratio=len*1.0/size*100;
            breakpointResume BRP=new breakpointResume(url,name,path,fl);
            BRP.ratio=ratio;
            savefile sf=new savefile(name,path,BRP);
            sf.fileratio=ratio;
            sf.filesize=(int)len;
            sf.bpr.totalsize=(int)size;
            System.out.println("文件大小："+sf.filesize);
            fl.add(sf);
        }
        br.close();
    }
}
