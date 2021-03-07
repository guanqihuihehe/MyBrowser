package com.company;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.*;
import java.util.Date;
import java.util.HashMap;

public class ResponseProcessor {
    private static final Logger logger = LogManager.getLogger(Cache.class.getCanonicalName());
    private InputStream in;
    private OutputStream out;
    private Writer writer;
    private Reader reader;
    private File rootDirectory;
    private Cache cache;

    // 404的html网页信息
    private String errorbody = new StringBuilder("<HTML>\r\n")
            .append("<HEAD><TITLE>File Not Found<TITLE>\r\n")
            .append("</HEAD>\r\n")
            .append("<BODY>")
            .append("<H1>HTTP Error 404: File Not Found</H1>\r\n")
            .append("</BODY></HTML>\r\n").toString();

    // 构造函数
    // 接收所需参数输入
    public ResponseProcessor(File rootDirectory, InputStream in, OutputStream out,
                             Writer writer, Reader reader, Cache cache){
        this.rootDirectory = rootDirectory;
        this.in = in;
        this.out = out;
        this.writer = writer;
        this.reader = reader;
        this.cache = cache;
    }

    // 处理HEAD请求方法
    public void doHEAD (String responseCode, String contentType, int length) {
        try{
            System.out.println("head");
            writer.write(responseCode + "\r\n");
            Date now = new Date();
            writer.write("Date: " + now + "\r\n");
            writer.write("Server: MyHttpServer\r\n");
            writer.write("Content-length: " + length + "\r\n");
            writer.write("Content-type: " + contentType + "\r\n\r\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARN, "Error to write.", e);
        }
    }

    // 处理GET请求方法
    public void doGET(File theFile, String contentType, String root, String version){
        HashMap<String, byte[]> hashMap = cache.getHashMap();
        try{
//            System.out.println("get");
//            System.out.println("inget:"+theFile);
//          判断文件是否可读（存在）
            if(cache.isCanread()){
//              通过文件名找到对应文件的数据
                byte[] theData = hashMap.get(theFile.getPath());
//                System.out.println("final:"+theFile.getPath());
                if( version.startsWith("HTTP/")){
                    doHEAD("HTTP/1.0 200 OK", contentType, theData.length);
                }
                out.write(theData);
                out.flush();
            }else{
                if(version.startsWith("HTTP/")){
                    doHEAD("HTTP/1.0 501 Not Implemented", "text/html; charset=utf-8", errorbody.length());
                }
                writer.write(errorbody);
                writer.flush();
            }
        }catch (IOException ex){
            ex.printStackTrace();
            logger.log(Level.WARN, "Error to write.", ex);
        }
    }

    // 处理POST请求方法
    public void doPOST(String contentType,String request) {

        String s[]=request.split("\r\n");
        String form=s[s.length-1];

        //获取提交的用户信息
        String[] forms=form.split("&");

        String body = new StringBuilder().append(form).toString();
        byte[] theData = body.getBytes();
        try{
            doHEAD("HTTP/1.0 200 OK", contentType, theData.length);
            out.write(theData);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARN, "Error to write.", e);
        }
    }

    // 处理PUT请求方法
    public void doPUT(){
        try{
            StringBuilder s = new StringBuilder();

            // 读取请求头
            while (true) {
                int c = in.read();
                s.append((char) c);
                // 以两个\r\n为结束符
                if(s.toString().endsWith("\r\n\r\n")){
                    break;
                }
            }

            // 根据请求头截取内容长度
            String cl = "Content-Length: ";
            if( !s.toString().contains("Content-Length: ")){
                cl = "content-length: ";
            }
            String temp = s.substring(s.indexOf(cl) + 16);
            String len = temp.substring(0,temp.indexOf("\r\n"));
            int length = 0;

            // 根据请求头获取内容种类
            // 默认使用.dat格式保存
            String type = ".dat";
            String ct = "Content-Type: ";
            if( !s.toString().contains("Content-Type: ")){
                ct = "content-type: ";
            }
            temp = s.substring(s.indexOf(ct) + 14);
            temp = temp.substring(0,temp.indexOf("\r\n"));
            String[] tokens = temp.split("/");
            if(tokens.length > 1){
                type = "." + tokens[1];
            }else {
                if(tokens != null)
                    type = "." + tokens[0];
            }

//          内容长度格式转换String -> int
            try{
                length = Integer.parseInt(len);
            }catch (NumberFormatException e){
                logger.log(Level.WARN, "Error to format number.", e);
            }
            byte[] bytes = new byte[length];
            int i = 0;
//          根据内容长度读取文件
            while( i < length ){
                int c = in.read(bytes, i, length - i);
                i += c;
            }
            // 写入文件
            File file = new File("test" + type);
            FileOutputStream fout = new FileOutputStream(file);
            fout.write(bytes);
            fout.flush();
//          返回一个200 OK的请求头
            doHEAD("HTTP/1.0 200 OK", "text/html", 0);
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARN, "Error to read.", e);
        }

    }

    // 处理DELETE请求方法
    public void doDELETE(String filename){
        try{
            System.out.println("delete");
            String root = rootDirectory.getPath();
            File file = new File(root, filename);
            if(file.exists()){
                file.delete();
                doHEAD("HTTP/1.0 200 OK","text/html",0);
            }else{
                doHEAD("HTTP/1.0 404 File Not Found","text/html", errorbody.length());
                writer.write(errorbody);
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARN, "Error to write.", e);
        }

    }

    // 处理OPTIONS请求方法
    public void doOPTIONS() {
        try{
            System.out.println("options");
            // 返回能处理GET、HEAD、POST、PUT、DELETE、OPTIONS命令的首部信息
            writer.write("HTTP/1.1 200 OK\r\n");
            Date now = new Date();
            writer.write("Date: " + now + "\r\n");
            writer.write("Server: MyHttpServer\r\n");
            writer.write("Allow: GET,HEAD,POST,PUT,DELETE,OPTIONS,TRACE\r\n");
            writer.write("Content-Sytle-Type: text/html\r\n");
            writer.write("Content-Length: 0\r\n");
            writer.write("Connection: close\r\n");
            writer.write("Content-Type: text/html; charset=utf-8\r\n\r\n");
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARN, "Error to write.", e);
        }

    }

    // 处理TRACE请求方法
    public void doTRACE(String requestLine){
        try{
            System.out.println("trace");
            StringBuilder s = new StringBuilder();
            // 读取剩下部分请求头
            while (true) {
                int c = in.read();
                s.append((char) c);
                // 以两个\r\n为结束符
                if(s.toString().endsWith("\r\n\r\n")){
                    break;
                }
            }
            doHEAD("HTTP/1.1 200 OK", "message/http",0);
            writer.write(requestLine + "\r\n");
            writer.write(s.toString());
        } catch (IOException e) {
            e.printStackTrace();
            logger.log(Level.WARN, "Error to write.", e);
        }
    }
}
