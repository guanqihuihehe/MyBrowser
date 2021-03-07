package com.company;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.net.Socket;
import java.net.URLConnection;

// 接收请求类
public class Request implements Runnable{
    private static final Logger logger = LogManager.getLogger(Request.class.getCanonicalName());
    private File rootDirectory;
    private String indexFileName = "index.html";
    private Socket connection;
    private ResponseProcessor response;
    private Cache cache;

    public Request(File rootDirectory, String indexFileName, Socket connection, Cache cache){
        if(rootDirectory.isFile()){
            throw new IllegalArgumentException(
                    "rootDirectory must be a directory, not a file");
        }
        //            rootDirectory = rootDirectory.getCanonicalFile();
        this.rootDirectory = rootDirectory;
        if(indexFileName != null)
            this.indexFileName = indexFileName;
        this.connection = connection;
        this.cache = cache;
    }

    @Override
    public void run() {
        String root = rootDirectory.getPath();
//        System.out.println("root"+root);
        try{
            OutputStream out = new BufferedOutputStream(connection.getOutputStream());
            Writer writer = new OutputStreamWriter(out);
            InputStream in = connection.getInputStream();
            Reader reader = new InputStreamReader(new BufferedInputStream(connection.getInputStream()),"US-ASCII");
            response = new ResponseProcessor(rootDirectory, in, out, writer, reader, cache);

            int length = in.available();
            byte[] buffer = new byte[length];
            //获得发送内容的字节
            in.read(buffer);
            String request = new String(buffer);
            System.out.println("第1000个请求：");
            System.out.println(request);
            //取得连接请求的第一行
            String firstLineOfRequest=request.substring(0,request.indexOf("\r\n"));
            System.out.println("第一行："+firstLineOfRequest);
            String[] parts = firstLineOfRequest.split(" ");
            String method = parts[0];
            System.out.println("method"+method);

            String version = "";
            String fileName = parts[1];
            if(fileName.endsWith("/"))
                fileName += indexFileName;
//          获取内容种类
            String contentType = URLConnection.getFileNameMap().getContentTypeFor(fileName);
            File theFile = new File(rootDirectory, fileName.substring(1,fileName.length()));
//          分别处理请求方法
            if(method.equals("GET") ){
                if(parts.length > 2){
                    version = parts[2];
                }
                response.doGET(theFile, contentType, root, version);
            }else if( method.equals("HEAD")){   // HEAD 请求
                response.doHEAD("HTTP/1.0 200 OK", "txt/html", 0);
            }else if( method.equals("POST")) {  // POST 请求
                response.doPOST(contentType,request);
            }else if( method.equals("PUT")){    // PUT 请求
                response.doPUT();
            }else if( method.equals("DELETE")){ // DELETE 请求
                response.doDELETE(fileName);
            }else if( method.equals("OPTIONS")){   // OPTIONS 请求
                response.doOPTIONS();
            }else if( method.equals("TRACE")){      // TRACE 请求
                response.doTRACE(firstLineOfRequest);
            }else {

            }
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.log(Level.WARN, "Error talking to " + connection.getRemoteSocketAddress(), ex);
        } finally {
            try{
                connection.close();
            } catch (IOException ex) {
                ex.printStackTrace();
                logger.log(Level.WARN, "Error close Socket:" + connection.getRemoteSocketAddress(), ex);
            }
        }
    }
}
