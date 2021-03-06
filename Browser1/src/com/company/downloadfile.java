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
        downloadshell.setText("??????");
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
        urlceiver.setText("???????????????");
        Text urltext=new Text(downloadshell,SWT.BORDER);
        rowData=new RowData();
        rowData.width=513;
        urltext.setLayoutData(rowData);


        Label l_path=new Label(downloadshell,SWT.NONE);
        l_path.setText("???????????????");
        Text pathtext=new Text(downloadshell,SWT.BORDER);
        rowData=new RowData();
        rowData.width=450;
        pathtext.setLayoutData(rowData);
        Button filebutton=new Button(downloadshell,SWT.PUSH);
        filebutton.setText("?? ?? ??");
        filebutton.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                DirectoryDialog folderdlg=new DirectoryDialog(downloadshell);
//??????????????????????????????
                folderdlg.setText("????????????");
//??????????????????
                folderdlg.setFilterPath("D://");
//?????????????????????????????????
                folderdlg.setMessage("???????????????????????????");
//???????????????????????????????????????????????????
                String selecteddir=folderdlg.open();
                if(selecteddir==null){
                    return ;
                }
                else{
                    pathtext.setText(selecteddir);
                    System.out.println("?????????????????????????????????"+selecteddir);
                }
            }
        });

        Label rename=new Label(downloadshell,SWT.NONE);
        rename.setText("???????????????");
        Text renametext=new Text(downloadshell,SWT.BORDER);
        rowData=new RowData();
        rowData.width=513;
        renametext.setLayoutData(rowData);

        //?????????????????????
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
                System.out.println("????????????"+tempname);
                System.out.println(realname);
                renametext.setText(realname);
            }
        });

        Button button=new Button(downloadshell,SWT.PUSH|SWT.CENTER);
        button.setText("????????????");
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
//??????????????????????????????
        folderdlg.setText("????????????");
//??????????????????
        folderdlg.setFilterPath("D://");
//?????????????????????????????????
        folderdlg.setMessage("???????????????????????????");
//???????????????????????????????????????????????????
        String selecteddir=folderdlg.open();
        if(selecteddir==null){
            return ;
        }
        else{
            System.out.println("?????????????????????????????????"+selecteddir);
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
//                    // ??????BufferedInputStream???????????????
//                    InputStream is = connection.getInputStream();
//                    // ???????????????????????????
//                    File file = new File(path);
//                    System.out.println(path);
//                    FileOutputStream fos = new FileOutputStream(file);
//                    byte[] buffer = new byte[4096];
//                    int b = 0;
//                    // ????????????
//                    while ((b = is.read(buffer)) != -1) {
//                        fos.write(buffer, 0, b);
//                    }
//                    // ?????????
//                    fos.close();
//                    is.close();
////                    JOptionPane.showMessageDialog(null, "????????????", "??????!", JOptionPane.INFORMATION_MESSAGE);
//                    Shell dlshell=new Shell();
//                    MessageBox box=new MessageBox(dlshell,SWT.ICON_INFORMATION|SWT.YES);
//                    box.setText("??????");
//                    box.setMessage("????????????");
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
//        public String urlStr = null;//???????????????
//        public String filename = null;//????????????
//        public String tmpfilename = null;//??????????????????
//        public long fileLength = 0;//??????????????????????????????
//        public long threadLength = 0;//?????????????????????????????????
//        public int threadNum = 3;//????????????
//        public CountDownLatch latch = null;//???????????????????????????????????????????????????????????????????????????
//
//        public long[] startPos;//????????????????????????????????????????????????
//        public long[] endPos;//????????????????????????????????????????????????
//        public boolean bool = false;
//        public URL url = null;
//
//        //?????????????????????????????????????????????
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
//        //?????????????????????????????????
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
//            //???????????????????????????????????????????????????????????????UUID?????????????????????????????????????????????
////            filename = urlStr.substring(urlStr.lastIndexOf('/')+ 1, urlStr.contains("?") ? urlStr.lastIndexOf('?') : urlStr.length());
////            tmpfilename = filename + "_tmp";
//
//            try
//            {
//                //??????url
//                url = new URL(urlStr);
//                trustAllHosts();
//                //???????????????????????????????????????HttpURLConnection???????????????httpcon
//                HttpURLConnection httpcon = (HttpURLConnection) url.openConnection();
//                httpcon.setRequestProperty("Accept-Encoding", "identity");
//
//                //?????????????????????????????????
//                fileLength =httpcon.getContentLengthLong();//getContentSize(httpcon);
//                System.out.println(fileLength);
//
//                //??????????????????????????????????????????????????????????????????????????????????????????
//                threadLength = fileLength%threadNum == 0 ? fileLength/threadNum : fileLength/threadNum+1;
//                //??????????????????
//                System.out.println("fileName: " + filename + " ," + "fileLength= "
//                        + fileLength + " the threadLength= " + threadLength);
//
//                //???????????????exec?????????????????????????????????--????????????
//                if (file.exists() && file.length() == fileLength)
//                {
//                    System.out.println("???????????????!!");
//                    return;
//                }
//                else
//                {
//                    setBreakPoint(startPos, endPos, tmpfile);
//                    ExecutorService exec = Executors.newCachedThreadPool();//???????????????????????????
//                    for (int i = 0; i < threadNum; i++)
//                        exec.execute(new DownLoadThread(startPos[i], endPos[i],this, i, tmpfile, latch));
//
//                    latch.await();//????????????????????????0?????????????????????????????????
//                    exec.shutdown();//?????? ExecutorService?????????????????????????????????
//                }
//            }
//            catch (MalformedURLException e) {e.printStackTrace();}
//            catch (IOException e) {e.printStackTrace();}
//            catch (InterruptedException e) {e.printStackTrace();}
//
//            //??????????????????????????????????????????????????????????????????
//            if (file.length() == fileLength)
//                if (tmpfile.exists())
//                {
//                    System.out.println("??????????????????!!");
//                    tmpfile.delete();
//                }
//        }
//
//
//        private void setBreakPoint(long[] startPos, long[] endPos, File tmpfile)
//        {
//            RandomAccessFile rantmpfile = null;//??????????????????????????????????????????
//            //void seek(long pos)?????????????????????????????????pos??????
//            //rw????????????????????????????????????????????????????????????????????????????????????????????????
//            try
//            {
//                if (tmpfile.exists())
//                {
//                    System.out.println("????????????!!");
//                    rantmpfile = new RandomAccessFile(tmpfile, "rw");
//                    for (int i = 0; i < threadNum; i++)
//                    {
//                        rantmpfile.seek(8 * i + 8);
//                        startPos[i] = rantmpfile.readLong();
//                        //?????????????????????????????????64????????? public final long readLong(){}
//
//                        rantmpfile.seek(8 * (i + 1000) + 16);
//                        endPos[i] = rantmpfile.readLong();
//
//                        System.out.println("the Array content in the exit file: ");
//                        System.out.println("the thread" + (i + 1) + " startPos:"
//                                + startPos[i] + ", endPos: " + endPos[i]);
//                    }
//                }
//                else //????????????tmpfile
//                {
//                    System.out.println("the tmpfile is not available!!");
//                    rantmpfile = new RandomAccessFile(tmpfile, "rw");
//
//                    //???????????????????????????????????????????????????????????????
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
//        //????????????????????????????????????????????????????????????????????????
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
//                System.out.println("??????" + id + " ????????????......");
//
//                while (true)
//                {
//                    try
//                    {
//                        trustAllHosts();
//                        httpcon = (HttpURLConnection) task.url.openConnection();
//                        httpcon.setRequestMethod("GET");
//                        httpcon.setReadTimeout(20000);//???????????????????????????
//                        httpcon.setConnectTimeout(20000);//?????????????????????
//
//                        if (startPos < endPos)
//                        {
//                            //?????????????????????????????????????????????????????????????????????????????????
//                            httpcon.setRequestProperty("Range", "bytes=" + startPos+ "-" + endPos);
//                            System.out.println("?????? " + id+ " ??????:---- "+ (endPos - startPos));
//                            downloadfile.seek(startPos);
//
//                            if (httpcon.getResponseCode() != HttpURLConnection.HTTP_OK
//                                    && httpcon.getResponseCode() != HttpURLConnection.HTTP_PARTIAL)
//                            {
//                                this.task.bool = true;
//                                httpcon.disconnect();
//                                downloadfile.close();
//                                System.out.println("?????? ---" + id + " ????????????!!");
//                                latch.countDown();//???????????????
//                                break;
//                            }
//                            is = httpcon.getInputStream();//?????????????????????????????????
//                            long count = 0l;
//                            byte[] buf = new byte[1024];
//
//                            while (!this.task.bool && (length = is.read(buf)) != -1)
//                            {
//                                count += length;
//                                downloadfile.write(buf, 0, length);
//
//                                //??????????????????????????????????????????????????????????????????????????????????????????????????????
//                                startPos += length;
//                                rantmpfile.seek(8 * id + 8);
//                                rantmpfile.writeLong(startPos);
//                            }
//                            System.out.println("?????? " + id + " ???????????????: " + count);
//
//                            //?????????
//                            is.close();
//                            httpcon.disconnect();
//                            downloadfile.close();
//                            rantmpfile.close();
//                        }
//                        latch.countDown();//???????????????
//                        System.out.println("?????? " + id + " ????????????!!");
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
            //????????????????????????????????????url
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            //?????????????????????????????????
            connection.setRequestMethod("GET");
            long sum = 0;//????????????????????????????????????

            //??????????????????????????????
            if (file.exists()) {
                sum = file.length();
                // ????????????????????????????????????????????????????????????
                connection.setRequestProperty("Range", "bytes=" + file.length() + "-");
            }

            //code???HTTP???????????????????????????
            int code = connection.getResponseCode();
            System.out.println("code = " + code);
            if (code == 200 || code == 206) {
                int contentLength = connection.getContentLength();
                System.out.println("???????????? = " + contentLength);
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

                // ??????????????????????????? name ?????????????????????????????????????????????true???????????????????????????????????????????????? ??????????????????????????????????????????????????????
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

                        //???????????????
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
