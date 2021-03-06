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
        //Displays???????????????????????????event loops????????????UI????????????????????????????????????
        Display display=new Display();
        //Shell??????????????????????????????????????????????????????????????????
        Shell shell=new Shell(display);
        //?????????????????????
        shell.setText("MyBrowser");
        shell.setSize(1300, 700);
//        Color myclor=new Color(null,238,7,7,1);
        //????????????
//        Color myclor=new Color(238,7,7,1);
//        shell.setBackground(myclor);
//        shell.setBackgroundMode(SWT.INHERIT_FORCE);

//        shell.setAlpha(10);

//        Canvas canvas = new Canvas(shell, SWT.BACKGROUND);
//        //??????paintlistener?????????????????????????????????
//        canvas.addPaintListener(new PaintListener() {
//            public void paintControl(PaintEvent e) {
//                GC gc = e.gc;
//                //?????????
//                ImageData imageData = new ImageData("bg.jpg");
//                //????????????????????????????????????Alpha???
//                byte[] alphaValues = new byte[imageData.height * imageData.width];
//                for (int j = 0; j < imageData.height; j++) {
//                    for (int i = 0; i < imageData.width; i++) {
//                        alphaValues[j * imageData.width + i] = (byte) (255 - 255
//                                * i / imageData.width);
//                    }
//                }
//                imageData.alphaData = alphaValues;
//                Image image = new Image(display, imageData);
//                //??????
//                gc.drawImage(image,0,0);
//            }
//        });


        //??????Shell???Layout manager
        shell.setLayout(new FormLayout());
        //?????????????????????????????????Shell???.??????Shell?????????Composite?????????.??????shell???????????????Composite????????????.
        //Composite????????????????????????SWT.NONE,??????Composite??????????????????????????????.
        Composite control = new Composite(shell, SWT.NONE);
//        control.setBackground(new Color(display,153,153,153,1));
        Image image=new Image(display,"bg4.jpg");
        control.setBackgroundImage(image);
        control.setBackgroundMode(SWT.INHERIT_FORCE);
        //FormData??????1??????form???????????????name???value?????????????????????????????????????????????????????????????????????????????????????????????????????????
        //2?????????????????????
        FormData data = new FormData();
        data.top = new FormAttachment(0, 0);
        data.left = new FormAttachment(0, 0);
        data.right = new FormAttachment(100, 0);
        control.setLayoutData(data);

        //Label?????????????????????
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
        //?????????????????????????????????
        Log log = new Log();
//     Download download = new Download(log);//???????????????
//        Download download = new Download();

        downloadfile dl=new downloadfile();
        fileUI fileui=new fileUI();
        ArrayList<savefile> filelist = new ArrayList<savefile>();
//        inifilelist(filelist);

        E_mail email = new E_mail(log);
        BookMark bookmark = new BookMark(log);
        History history = new History(log);
        //???????????????
        //????????????menu bar???????????????????????????:
        Menu menu = new Menu(shell, SWT.BAR);
        shell.setMenuBar(menu);
        //???????????????item???menu bar:
        MenuItem file = new MenuItem(menu, SWT.CASCADE);
        //style?????????SWT.CASCADE,??????????????????file???drop-down menu
        file.setText("??????");
        MenuItem histo = new MenuItem(menu, SWT.CASCADE);
        histo.setText("????????????");
        MenuItem downlo = new MenuItem(menu, SWT.CASCADE);
        downlo.setText("??????");
        MenuItem ema = new MenuItem(menu, SWT.CASCADE);
        ema.setText("??????");
        MenuItem book = new MenuItem(menu, SWT.CASCADE);
        book.setText("??????");
        MenuItem help = new MenuItem(menu, SWT.CASCADE);
        help.setText("??????");


        //?????????file??????menu,style?????????SWT.DROP_DOWN
        Menu filemenu = new Menu(shell, SWT.DROP_DOWN);
        file.setMenu(filemenu);
        //???file????????????
        MenuItem getHtml = new MenuItem(filemenu,SWT.CASCADE);
        getHtml.setText("????????????");
        MenuItem exit = new MenuItem(filemenu, SWT.CASCADE);
        exit.setText("??????");

        Menu historyMenu = new Menu(shell, SWT.DROP_DOWN);
        histo.setMenu(historyMenu);
        //???????????????????????????
        MenuItem check = new MenuItem(historyMenu, SWT.CASCADE);//??????????????????
        check.setText("????????????????????????");
        MenuItem delete = new MenuItem(historyMenu,SWT.CASCADE);
        delete.setText("????????????????????????");

        Menu bookMenu = new Menu(shell, SWT.DROP_DOWN);
        book.setMenu(bookMenu);
        //?????????????????????
        MenuItem addMark = new MenuItem(bookMenu, SWT.CASCADE);//????????????
        addMark.setText("???????????????");
        MenuItem checkMark = new MenuItem(bookMenu, SWT.CASCADE);//????????????
        checkMark.setText("????????????");

        //?????????????????????
        Menu downMenu = new Menu(shell, SWT.DROP_DOWN);
        downlo.setMenu(downMenu);
        MenuItem downloading = new MenuItem(downMenu, SWT.CASCADE);
        downloading.setText("????????????");
        MenuItem checkload = new MenuItem(downMenu, SWT.CASCADE);
        checkload.setText("??????????????????");

        //?????????????????????
        Menu mailMenu = new Menu(shell, SWT.DROP_DOWN);
        ema.setMenu(mailMenu);
        MenuItem sendMail = new MenuItem(mailMenu, SWT.CASCADE);
        sendMail.setText("????????????");

        //?????????????????????
        Menu helpMenu = new Menu(shell, SWT.DROP_DOWN);
        help.setMenu(helpMenu);
        MenuItem search = new MenuItem(helpMenu, SWT.CASCADE);
        search.setText("??????????????????");
        MenuItem about = new MenuItem(helpMenu, SWT.CASCADE);
        about.setText("????????????");
        Button button;
        //??????SWT????????????????????????????????????????????????
        button=new Button(control,SWT.ARROW|SWT.LEFT);

        //????????????
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                browser.back();
            }
        });

        //Forward********************************************

        //????????????
        button=new Button(control,SWT.ARROW|SWT.RIGHT);
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                browser.forward();
            }
        });

        //????????????????????????
        Label ipLocation = new Label(control, SWT.LEFT);
        ipLocation.setText("??????????????????");
        //???????????????????????????????????? x ???????????? y ???????????? width?????? height
        ipLocation.setBounds(105, 5, 100, 25);
        Text iptext = new Text(control, SWT.BORDER);
        iptext.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        iptext.setFocus();

        Button enter = new Button(control, SWT.PUSH);
        enter.setBounds(220,5,100,25);
        enter.setText("??????");

        //????????????
        button=new Button(control,SWT.PUSH);
        button.setText("??????");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                browser.refresh();
                String url=browser.getUrl();
                iptext.setText(url);
            }
        });

        //??????????????????
        button=new Button(control,SWT.PUSH);
        button.setText("????????????");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                browser.stop();
            }
        });

        //????????????????????????????????????
        iptext.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.keyCode==SWT.CR||e.keyCode==SWT.KEYPAD_CR)
                {
                    String input=iptext.getText().trim();
                    //??????????????????????????????
                    if(input.length()==0)
                    {
//                        JOptionPane.showMessageDialog(null, "???????????????","Error", JOptionPane.INFORMATION_MESSAGE);
                        MessageBox box=new MessageBox(shell,SWT.ICON_WARNING|SWT.YES);
                        box.setText("");
                        box.setMessage("??????????????????????????????");
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
                            input="http://"+input;//???????????????????????????????????????http://??????
                            iptext.setText(input);
                        }
                    }else{
                        String key= null;
                        try {
                            key = URLEncoder.encode(input,"UTF-8");
                        } catch (UnsupportedEncodingException ex) {
                            ex.printStackTrace();
                        }
                        input="https://www.baidu.com/s?ie=UTF-8&wd="+key;//???????????????????????????
                        iptext.setText(input);
                    }
                    browser.setUrl(input);
                }
            }
        });

        //????????????????????????
        enter.addListener(SWT.Selection, new Listener() {
            public void handleEvent(Event event) {
                String input=iptext.getText().trim();
                if(input.length()==0)
                {
//                    JOptionPane.showMessageDialog(null,"???????????????","Error",JOptionPane.INFORMATION_MESSAGE);
                    MessageBox box=new MessageBox(shell,SWT.ICON_WARNING|SWT.YES);
                    box.setText("");
                    box.setMessage("??????????????????????????????");
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
                        input="https://www.baidu.com/s?ie=UTF-8&wd="+key; //????????????????????????????????????
                        iptext.setText(input);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                }
                browser.setUrl(input);
            }
        });

        //?????????????????????????????????????????????
        browser.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {    //???????????????
                if(e.keyCode==SWT.CR||e.keyCode==SWT.KEYPAD_CR){
                    String str=browser.getUrl();
                    iptext.setText(str);
                }
            }
        });
        //?????????????????????????????????????????????
        browser.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseUp(MouseEvent e) {
                String str=browser.getUrl();
                iptext.setText(str);
            }
        });

        //???????????????
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
//                         download.createUI();//????????????

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
            public void changed(LocationEvent locationEvent) {  //???????????????
                String url=browser.getUrl();
                String name=browser.getText();
                iptext.setText(url);
//                ??????URL??????
                { Document doc = null;
                try {
                    trustAllHosts();
                    doc = Jsoup.parse(new URL(url),30000);
                } catch (IOException e) {
                    System.out.println(e);
                    e.printStackTrace();

                    {
                        MessageBox box = new MessageBox(shell, SWT.ICON_WARNING | SWT.YES);
                        box.setText("??????");
                        box.setMessage("??????????????????");
                        int choice = box.open();
                        if (choice == SWT.YES) {

                        }
                    }
                }}

                try{

                    //??????????????????????????????title??????????????????
//                    String title=doc.getElementsByTag("title").first().text();
                    //?????????????????????????????????
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
                System.out.println("????????????");

                MessageBox box=new MessageBox(shell,SWT.ICON_INFORMATION|SWT.YES|SWT.NO);
                box.setText("????????????");
                box.setMessage("???????????????????????????");
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
//                int result=JOptionPane.showConfirmDialog(null,"????????????????????????",
//                        "Warning",JOptionPane.YES_NO_OPTION);
//                if (result==JOptionPane.YES_OPTION){
//                    JOptionPane.showMessageDialog(null,"?????????????????????",
//                            "Message",JOptionPane.INFORMATION_MESSAGE);
//                    shell.close();
//                    shell.dispose();
//                }
                MessageBox box=new MessageBox(shell,SWT.ICON_INFORMATION|SWT.YES|SWT.NO);
                box.setText("???????????????");
                box.setMessage("??????????????????????????????");
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
            public void widgetSelected(SelectionEvent e) {  //????????????
                int result=CheckHistory(shell,history,browser,iptext,display);
                if(result == 0){
//                    JOptionPane.showMessageDialog(null,"??????????????????!",
//                            "Error",JOptionPane.INFORMATION_MESSAGE);
                    MessageBox box=new MessageBox(shell,SWT.ICON_WARNING|SWT.YES);
                    box.setText("");
                    box.setMessage("??????????????????");
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
            public void widgetSelected(SelectionEvent e) {  //????????????
                delHistory(shell,history);
            }
        });

        //********************************************

        addMark.addSelectionListener(new SelectionAdapter() {   //?????????????????????
            @Override
            public void widgetSelected(SelectionEvent e) {  //????????????
                String url=browser.getUrl();
                if(!url.isEmpty()){
                    bookmark.setNewMark(url);
                }
                else{

                    MessageBox box=new MessageBox(shell,SWT.ICON_WARNING|SWT.YES);
                    box.setText("");
                    box.setMessage("????????????");
                    int choice=box.open();
                    if(choice==SWT.YES)
                    {
                        System.out.println("Yes");

                    }
                }
            }
        });

        checkMark.addSelectionListener(new SelectionAdapter() {   //??????????????????
            @Override
            public void widgetSelected(SelectionEvent e) {  //????????????
                if(bookmark.list.size()==0){

                    MessageBox box=new MessageBox(shell,SWT.ICON_WARNING|SWT.YES);
                    box.setText("");
                    box.setMessage("???????????????");
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
                box.setText("??????");
                box.setMessage("????????? mybrowser 1.0");
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
                seashell.setText("??????");
                seashell.setSize(450,85);
                seashell.setLayout(new GridLayout(2,false));

                Text text=new Text(seashell, SWT.CENTER|SWT.BORDER);
                text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
                Button ok=new Button(seashell,SWT.PUSH);
                ok.setText("??????");

                text.addKeyListener(new KeyAdapter() {  //????????????
                    @Override
                    public void keyReleased(KeyEvent e) {
                        if (e.keyCode==SWT.CR||e.keyCode==SWT.KEYPAD_CR){
                            String str=text.getText();
                            while(str.equals("")){
                                MessageBox box=new MessageBox(shell,SWT.ICON_WARNING|SWT.YES);
                                box.setText("");
                                box.setMessage("??????");
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
                            str="https://www.baidu.com/s?ie=UTF-8&wd="+key; //????????????????????????????????????

                            browser.setUrl(str);
                            seashell.dispose();
                        }
                    }
                });

                ok.addSelectionListener(new SelectionAdapter() {
                    @Override
                    public void widgetSelected(SelectionEvent e) {  //????????????
                        String str=text.getText();
                        while(str.equals("")){
                             MessageBox box=new MessageBox(shell,SWT.ICON_WARNING|SWT.YES);
                            box.setText("");
                            box.setMessage("??????");
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
        hishell.setText("????????????");
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
        deleteAll.setText("??????????????????");

        deleteAll.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {

                MessageBox box=new MessageBox(browser.getShell(),SWT.ICON_INFORMATION|SWT.YES|SWT.NO);
                box.setText("????????????");
                box.setMessage("???????????????????????????");
                int choice=box.open();
                if(choice==SWT.YES)
                {
                    history.deleteAll();
                    MessageBox box2=new MessageBox(browser.getShell(),SWT.ICON_INFORMATION|SWT.YES);
                    box2.setText("????????????");
                    box2.setMessage("????????????");
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
        box.setText("????????????");
        box.setMessage("????????????????????????????????????");
        int choice=box.open();
        if(choice==SWT.YES)
        {
            history.deleteAll();

            MessageBox box2=new MessageBox(his_shell,SWT.ICON_INFORMATION|SWT.YES);
            box2.setText("????????????");
            box2.setMessage("??????");
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
        box.setText("????????????");
        box.setMessage("???????????????????????????");
        int choice=box.open();
        if(choice==SWT.YES)
        {
            bookmarks.deleteAll();
            MessageBox box2=new MessageBox(bmshell,SWT.ICON_INFORMATION|SWT.YES);
            box2.setText("????????????");
            box2.setMessage("????????????");
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
        index.setText("?????????????????????????????????: ");

        Text text=new Text(delshell,SWT.BORDER|SWT.SINGLE|SWT.CENTER);
        text.setFocus();

        Button check=new Button(delshell,SWT.PUSH);
        check.setText("???????????????");

        text.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if(e.keyCode==SWT.CR||e.keyCode==SWT.KEYPAD_CR){
                    String str=text.getText().trim();
                    int n=Integer.parseInt(str);
                    if(n<=0||n>bookmarks.list.size()){
//                        JOptionPane.showMessageDialog(null,"??????!",
//                                "Error",JOptionPane.INFORMATION_MESSAGE);
                        MessageBox box=new MessageBox(delshell,SWT.ICON_ERROR|SWT.YES);
                        box.setText("??????");
                        box.setMessage("??????");
                        int choice=box.open();
                        if(choice==SWT.YES)
                        {

                        }
                    }
                    else{
                        bookmarks.delete(n-1);
//                        JOptionPane.showMessageDialog(null,"??????!",
//                                "Message",JOptionPane.INFORMATION_MESSAGE);
                        MessageBox box=new MessageBox(delshell,SWT.ICON_INFORMATION|SWT.YES);
                        box.setText("");
                        box.setMessage("??????");
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
//                    JOptionPane.showMessageDialog(null,"??????????????????!",
//                            "Error",JOptionPane.INFORMATION_MESSAGE);
                    MessageBox box=new MessageBox(delshell,SWT.ICON_ERROR|SWT.YES);
                    box.setText("??????");
                    box.setMessage("??????????????????");
                    int choice=box.open();
                    if(choice==SWT.YES)
                    {

                    }
                }
                else{
                    bookmarks.delete(n-1);
//                    JOptionPane.showMessageDialog(null,"??????!",
//                            "Message",JOptionPane.INFORMATION_MESSAGE);
                    MessageBox box=new MessageBox(delshell,SWT.ICON_INFORMATION|SWT.YES);
                    box.setText("");
                    box.setMessage("??????");
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
        bmshell.setText("??????");
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

        //??????????????? ??????/????????????

        Menu deleteBar=new Menu(bmshell,SWT.BAR);
        bmshell.setMenuBar(deleteBar);

        MenuItem delOne=new MenuItem(deleteBar,SWT.CASCADE);
        delOne.setText("??????????????????");
        MenuItem delAll=new MenuItem(deleteBar,SWT.CASCADE);
        delAll.setText("??????????????????");

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
            System.out.println("???????????????"+sf.filesize);
            fl.add(sf);
        }
        br.close();
    }
}
