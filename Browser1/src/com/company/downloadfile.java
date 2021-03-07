package com.company;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Label;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class downloadfile {

    /**
     * Trust every server - dont check for any certificate
     */
    public static void trustAllHosts() {
        final String TAG = "trustAllHosts";
        // Create a trust manager that does not validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
            public X509Certificate[] getAcceptedIssuers() {
                return new X509Certificate[] {};
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

    public void createUI(ArrayList<savefile> filelist,Display display){

        Shell downloadshell=new Shell(SWT.RESIZE|SWT.MAX);
        downloadshell.setText("下载");
        downloadshell.setSize(750,300);
        Image image=new Image(display,"bg4.jpg");
        downloadshell.setBackgroundImage(image);
        downloadshell.setBackgroundMode(SWT.INHERIT_FORCE);
        RowLayout rowLayout = new RowLayout();
        rowLayout.marginLeft = 50;
        rowLayout.marginRight = 50;
        rowLayout.marginTop = 40;
        rowLayout.spacing = 30;
        downloadshell.setLayout(rowLayout);
        RowData rowData=new RowData();
        Label urlceiver=new Label(downloadshell,SWT.NONE);
        urlceiver.setText("文件链接：");
        Text urltext=new Text(downloadshell,SWT.BORDER);
        rowData=new RowData();
        rowData.width=513;
        urltext.setLayoutData(rowData);


        Label l_path=new Label(downloadshell,SWT.NONE);
        l_path.setText("选择路径：");
        Text pathtext=new Text(downloadshell,SWT.BORDER);
        rowData=new RowData();
        rowData.width=450;
        pathtext.setLayoutData(rowData);
        Button filebutton=new Button(downloadshell,SWT.PUSH);
        filebutton.setText("· · ·");
        filebutton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog folderdlg=new DirectoryDialog(downloadshell);
//设置文件对话框的标题
                folderdlg.setText("文件选择");
//设置初始路径
                folderdlg.setFilterPath("D://");
//设置对话框提示文本信息
                folderdlg.setMessage("请选择相应的文件夹");
//打开文件对话框，返回选中文件夹目录
                String selecteddir=folderdlg.open();
                if(selecteddir==null){
                    return ;
                }
                else{
                    pathtext.setText(selecteddir);
                    System.out.println("您选中的文件夹目录为："+selecteddir);
                }
            }
        });

        Label rename=new Label(downloadshell,SWT.NONE);
        rename.setText("重命名为：");
        Text renametext=new Text(downloadshell,SWT.BORDER);
        rowData=new RowData();
        rowData.width=513;
        renametext.setLayoutData(rowData);

        //输入框修改事件
        urltext.addModifyListener(new ModifyListener() {
            @Override
            public void modifyText(ModifyEvent modifyEvent) {
                String tempname=urltext.getText();
                String realname="";
                for(int i=tempname.length()-1;i>=0;i--)
                {
                    if(tempname.charAt(i)=='/'||tempname.charAt(i)=='\\'||tempname.charAt(i)==':'||tempname.charAt(i)=='?'||tempname.charAt(i)=='"'||tempname.charAt(i)=='<'||tempname.charAt(i)=='>'||tempname.charAt(i)=='|')
                    {
                        break;
                    }
                    realname=tempname.charAt(i)+realname;
                }
                System.out.println("输入的："+tempname);
                System.out.println(realname);
                renametext.setText(realname);
            }
        });

        Button button=new Button(downloadshell,SWT.PUSH|SWT.CENTER);
        button.setText("下载文件");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                System.out.println(pathtext.getText());
                System.out.println(renametext.getText());
                String actualpath=pathtext.getText()+"\\"+renametext.getText();
                breakpointResume br=new breakpointResume(urltext.getText(),renametext.getText(),actualpath,filelist);
                savefile sf=new savefile(renametext.getText(),actualpath,br);
                filelist.add(sf);
                new Thread(br).start();
            }
        });

        downloadshell.open();
    }

    void openfile(Shell downloadshell)
    {
        DirectoryDialog folderdlg=new DirectoryDialog(downloadshell);
//设置文件对话框的标题
        folderdlg.setText("文件选择");
//设置初始路径
        folderdlg.setFilterPath("D://");
//设置对话框提示文本信息
        folderdlg.setMessage("请选择相应的文件夹");
//打开文件对话框，返回选中文件夹目录
        String selecteddir=folderdlg.open();
        if(selecteddir==null){
            return ;
        }
        else{
            System.out.println("您选中的文件夹目录为："+selecteddir);
        }
    }

    public void send(String weburl,String path,breakpointResume brnow) throws IOException {
//        Display.getDefault().syncExec(new Runnable() {
//            public void run() {
//                // TODO Auto-generated method stub
//                try {
//                    URL url = new URL(weburl);
//                    System.out.println(weburl);
//                    trustAllHosts();
//                    URLConnection connection = url.openConnection();
//                    connection.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
//                    // 创建BufferedInputStream输入流对象
//                    InputStream is = connection.getInputStream();
//                    // 创建文件输出流对象
//                    File file = new File(path);
//                    System.out.println(path);
//                    FileOutputStream fos = new FileOutputStream(file);
//                    byte[] buffer = new byte[4096];
//                    int b = 0;
//                    // 写入文件
//                    while ((b = is.read(buffer)) != -1) {
//                        fos.write(buffer, 0, b);
//                    }
//                    // 关闭流
//                    fos.close();
//                    is.close();
////                    JOptionPane.showMessageDialog(null, "下载完成", "成功!", JOptionPane.INFORMATION_MESSAGE);
//                    Shell dlshell=new Shell();
//                    MessageBox box=new MessageBox(dlshell,SWT.ICON_INFORMATION|SWT.YES);
//                    box.setText("下载");
//                    box.setMessage("下载成功");
//                    int choice=box.open();
//                    if(choice==SWT.YES)
//                    {
//                    }
//                }catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        new Thread().start();
//        MultiTheradDownLoad multiTheradDownLoad=new MultiTheradDownLoad(weburl,path);
//        multiTheradDownLoad.downloadPart();
//        breakpointResume br=new breakpointResume();

    }

//    class MultiTheradDownLoad
//    {
//
//
//        public String urlStr = null;//网址字符串
//        public String filename = null;//最终文件
//        public String tmpfilename = null;//中间缓存文件
//        public long fileLength = 0;//要下载的文件资源大小
//        public long threadLength = 0;//每一个线程要下载的数量
//        public int threadNum = 3;//线程数量
//        public CountDownLatch latch = null;//设置一个计数器，代码内主要用来完成对缓存文件的删除
//
//        public long[] startPos;//保留每个线程下载数据的起始位置。
//        public long[] endPos;//保留每个线程下载数据的截止位置。
//        public boolean bool = false;
//        public URL url = null;
//
//        //有参构造函数，先构造需要的数据
//        public MultiTheradDownLoad(String urlStr,String filename)
//        {
//            this.urlStr = urlStr;
////            this.threadNum = threadNum;
//            this.filename=filename;
//            startPos = new long[this.threadNum];
//            endPos = new long[this.threadNum];
//            latch = new CountDownLatch(this.threadNum);
//        }
//
//        //组织断点续传功能的方法
//        public void downloadPart() throws IOException {
//            tmpfilename = filename + "_tmp";
//            File file =  new File(filename);
//            File tmpfile = new File(tmpfilename);
//
//            if(!file.exists())
//            {
//                file.createNewFile();
//            }
//            if(!tmpfile.exists())
//            {
//                tmpfile.createNewFile();
//            }
//            //从文件链接中获取文件名，此种情况可能需使用UUID来生成一个唯一数来代表文件名。
////            filename = urlStr.substring(urlStr.lastIndexOf('/')+ 1, urlStr.contains("?") ? urlStr.lastIndexOf('?') : urlStr.length());
////            tmpfilename = filename + "_tmp";
//
//            try
//            {
//                //创建url
//                url = new URL(urlStr);
//                trustAllHosts();
//                //打开下载链接，并且得到一个HttpURLConnection的一个对象httpcon
//                HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
//                httpcon.setRequestProperty("Accept-Encoding", "identity");
//
//                //获取请求资源的总长度。
//                fileLength =httpcon.getContentLengthLong();//getContentSize(httpcon);
//                System.out.println(fileLength);
//
//                //每个线程需下载的资源大小；由于文件大小不确定，为避免数据丢失
//                threadLength = fileLength%threadNum == 0 ? fileLength/threadNum : fileLength/threadNum+1;
//                //打印下载信息
//                System.out.println("fileName: " + filename + " ," + "fileLength= "
//                        + fileLength + " the threadLength= " + threadLength);
//
//                //各个线程在exec线程池中进行，起始位置--结束位置
//                if (file.exists() && file.length() == fileLength)
//                {
//                    System.out.println("文件已存在!!");
//                    return;
//                }
//                else
//                {
//                    setBreakPoint(startPos, endPos, tmpfile);
//                    ExecutorService exec = Executors.newCachedThreadPool();//创建可缓存的线程池
//                    for (int i = 0; i < threadNum; i++)
//                        exec.execute(new DownLoadThread(startPos[i], endPos[i],this, i, tmpfile, latch));
//
//                    latch.await();//当你的计数器减为0之前，会在此处一直阻塞
//                    exec.shutdown();//关闭 ExecutorService，这将导致其拒绝新任务
//                }
//            }
//            catch (MalformedURLException e) {e.printStackTrace();}
//            catch (IOException e) {e.printStackTrace();}
//            catch (InterruptedException e) {e.printStackTrace();}
//
//            //下载完成后，判断文件是否完整，并删除临时文件
//            if (file.length() == fileLength)
//                if (tmpfile.exists())
//                {
//                    System.out.println("删除临时文件!!");
//                    tmpfile.delete();
//                }
//        }
//
//
//        private void setBreakPoint(long[] startPos, long[] endPos, File tmpfile)
//        {
//            RandomAccessFile rantmpfile = null;//支持跳到文件任意位置读写数据
//            //void seek(long pos)：将文件记录指针定位到pos位置
//            //rw：以读取、写入方式打开指定文件。如果该文件不存在，则尝试创建文件
//            try
//            {
//                if (tmpfile.exists())
//                {
//                    System.out.println("继续下载!!");
//                    rantmpfile = new RandomAccessFile(tmpfile, "rw");
//                    for (int i = 0; i < threadNum; i++)
//                    {
//                        rantmpfile.seek(8 * i + 8);
//                        startPos[i] = rantmpfile.readLong();
//                        //读取文件中一个有符号的64位整数 public final long readLong(){}
//
//                        rantmpfile.seek(8 * (i + 1000) + 16);
//                        endPos[i] = rantmpfile.readLong();
//
//                        System.out.println("the Array content in the exit file: ");
//                        System.out.println("the thread" + (i + 1) + " startPos:"
//                                + startPos[i] + ", endPos: " + endPos[i]);
//                    }
//                }
//                else //如果没有tmpfile
//                {
//                    System.out.println("the tmpfile is not available!!");
//                    rantmpfile = new RandomAccessFile(tmpfile, "rw");
//
//                    //最后一个线程的截止位置大小为请求资源的大小
//                    for (int i = 0; i < threadNum; i++)
//                    {
//                        startPos[i] = threadLength * i;
//                        if (i == threadNum - 1)
//                            endPos[i] = fileLength;
//                        else
//                            endPos[i] = threadLength * (i + 1) - 1;
//
//                        rantmpfile.seek(8 * i + 8);
//                        rantmpfile.writeLong(startPos[i]);
//
//                        rantmpfile.seek(8 * (i + 1000) + 16);
//                        rantmpfile.writeLong(endPos[i]);
//
//                        System.out.println("the Array content: ");
//                        System.out.println("the thread" + (i + 1) + " startPos:" + startPos[i] + ", endPos: " + endPos[i]);
//                    }
//                }
//            }
//            catch (FileNotFoundException e) { e.printStackTrace(); }
//            catch (IOException e) { e.printStackTrace(); }
//            finally
//            {
//                try
//                {
//                    if (rantmpfile != null)
//                        rantmpfile.close();
//                }
//                catch (IOException e) { e.printStackTrace(); }
//            }
//        }
//        //实现下载功能的内部类，通过读取断点来设置向服务器
//        class DownLoadThread implements Runnable
//        {
//            private long startPos;
//            private long endPos;
//            private MultiTheradDownLoad task = null;
//            private RandomAccessFile downloadfile = null;
//            private int id;
//            private File tmpfile = null;
//            private RandomAccessFile rantmpfile = null;
//            private CountDownLatch latch = null;
//
//            public DownLoadThread(long startPos, long endPos,MultiTheradDownLoad task, int id, File tmpfile, CountDownLatch latch)
//            {
//                this.startPos = startPos;
//                this.endPos = endPos;
//                this.task = task;
//                this.tmpfile = tmpfile;
//                try
//                {
//                    this.downloadfile = new RandomAccessFile(this.task.filename,"rw");
//                    this.rantmpfile = new RandomAccessFile(this.tmpfile, "rw");
//                }
//                catch (FileNotFoundException e) { e.printStackTrace(); }
//                this.id = id;
//                this.latch = latch;
//            }
//
//            @Override
//            public void run()
//            {
//                HttpURLConnection httpcon = null;
//                InputStream is = null;
//                int length = 0;
//                System.out.println("线程" + id + " 正在下载......");
//
//                while (true)
//                {
//                    try
//                    {
//                        trustAllHosts();
//                        httpcon = (HttpURLConnection) task.url.openConnection();
//                        httpcon.setRequestMethod("GET");
//                        httpcon.setReadTimeout(20000);//读取数据的超时设置
//                        httpcon.setConnectTimeout(20000);//连接的超时设置
//
//                        if (startPos < endPos)
//                        {
//                            //向服务器请求指定区间段的数据，这是实现断点续传的根本。
//                            httpcon.setRequestProperty("Range", "bytes=" + startPos+ "-" + endPos);
//                            System.out.println("线程 " + id+ " 长度:---- "+ (endPos - startPos));
//                            downloadfile.seek(startPos);
//
//                            if (httpcon.getResponseCode() != HttpURLConnection.HTTP_OK
//                                    && httpcon.getResponseCode() != HttpURLConnection.HTTP_PARTIAL)
//                            {
//                                this.task.bool = true;
//                                httpcon.disconnect();
//                                downloadfile.close();
//                                System.out.println("线程 ---" + id + " 下载完成!!");
//                                latch.countDown();//计数器自减
//                                break;
//                            }
//                            is = httpcon.getInputStream();//获取服务器返回的资源流
//                            long count = 0l;
//                            byte[] buf = new byte[1024];
//
//                            while (!this.task.bool && (length = is.read(buf)) != -1)
//                            {
//                                count += length;
//                                downloadfile.write(buf, 0, length);
//
//                                //不断更新每个线程下载资源的起始位置，并写入临时文件；为断点续传做准备
//                                startPos += length;
//                                rantmpfile.seek(8 * id + 8);
//                                rantmpfile.writeLong(startPos);
//                            }
//                            System.out.println("线程 " + id + " 总下载大小: " + count);
//
//                            //关闭流
//                            is.close();
//                            httpcon.disconnect();
//                            downloadfile.close();
//                            rantmpfile.close();
//                        }
//                        latch.countDown();//计数器自减
//                        System.out.println("线程 " + id + " 下载完成!!");
//                        break;
//                    }
//                    catch (IOException e) { e.printStackTrace(); }
//                    finally
//                    {
//                        try
//                        {
//                            if (is != null)
//                                is.close();
//                        }
//                        catch (IOException e) { e.printStackTrace(); }
//                    }
//                }
//            }
//        }
//    }


}

class breakpointResume implements Runnable {

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
    public int downflag=1;
    public  String url = "https://dldir1.qq.com/weixin/Windows/WeChatSetup.exe";
    public double ratio=0;
    public String name;
    public String path;
    public int totalsize;
    public ArrayList<savefile> filelist;
    public breakpointResume(String url,String name,String path,ArrayList<savefile> filelist)
    {
        this.url=url;
        this.name=name;
        this.path=path;
        this.filelist=filelist;
    }
    public void run()
    {
        downflag=1;
        try {
            trustAllHosts();
            File file = new File(path);
            //放入所要下载的文件对应的url
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            //根据请求的需要设置参数
            connection.setRequestMethod("GET");
            long sum = 0;//记录每次文件下载到的位置

            //如果已经下载一部分了
            if (file.exists()) {
                sum = file.length();
                // 设置断点续传的开始位置，设置一般请求属性
                connection.setRequestProperty("Range", "bytes=" + file.length() + "-");
            }

            //code是HTTP响应消息获取状态码
            int code = connection.getResponseCode();
            System.out.println("code = " + code);
            if (code == 200 || code == 206) {
                int contentLength = connection.getContentLength();
                System.out.println("文件大小 = " + contentLength);
                contentLength += sum;
                totalsize=contentLength;

                File file2 = new File("download.txt");
                BufferedWriter bw3 = null;
                try {
                    bw3 = new BufferedWriter(new FileWriter(file2));
                    for(savefile sff: filelist) {
                        bw3.write(sff.bpr.url + " " + sff.filename + " "+sff.filepath+" "+sff.bpr.totalsize);
                        bw3.newLine();
                    }
                    bw3.flush();
                    bw3.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }

                InputStream is = connection.getInputStream();

                // 创建一个向具有指定 name 的文件中写入数据的输出文件流。true表示当文件在下载过程中出现中断， 当再次链接网络时，将会从断点处追加。
                FileOutputStream fs = new FileOutputStream(file, true);

                byte[] buffer = new byte[102400];
                int length;

//                while ((length = is.read(buffer)) != -1) {
//                    fs.write(buffer, 0, length);
//                    sum += length;
//                    float percent = sum * 100.0f / contentLength;
//
//                    System.out.printf("\t%.2f%%", percent);
//                    System.out.println();
//
//                }

                long startTime = System.currentTimeMillis();
                while ((length = is.read(buffer)) != -1) {
                    if(downflag==1)
                    {
                        fs.write(buffer, 0, length);
                        sum += length;
                        float percent = sum * 100.0f / contentLength;
                        System.out.print("\r[");
                        int p = (int) percent / 2;

                        //实现进度条
                        {
                            for (int i = 0; i < 50; i++) {
                                if (i < p) {
                                    System.out.print('=');
                                } else if (i == p) {
                                    System.out.print('>');
                                } else {
                                    System.out.print(' ');
                                }
                            }
                            System.out.print(']');
                            System.out.printf("\t%.2f%%", percent);
                            ratio=percent;
                            long speed = sum * 1000 / (System.currentTimeMillis() - startTime);
                            if (speed > (1 << 20)) {
                                System.out.printf("\t%d MB/s", speed >> 20);
                            } else if (speed > (1 << 10)) {
                                System.out.printf("\t%d KB/s", speed >> 10);
                            } else {
                                System.out.printf("\t%d B/s", speed);
                            }
                        }
                    }
                    else {
                        fs.close();
                        break;
                    }
                }
                if(downflag==1)
                {
                    fs.close();
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void stop()
    {
        downflag=0;
    }

    public void savelist(ArrayList<savefile> filelist) throws IOException {
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File("download.txt")), "UTF-8"));
        for(int i=0;i<filelist.size();i++)
        {
            String name=filelist.get(i).filename;
            String path=filelist.get(i).filepath;
            double size=filelist.get(i).filesize;
            String res=name+" "+path+" "+size;
            bw.write(res);
            bw.newLine();
        }
        System.out.println("write");
    }
}
