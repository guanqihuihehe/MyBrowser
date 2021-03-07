package com.company;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.*;
import org.eclipse.swt.widgets.*;
import javax.swing.*;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.regex.*;

public class BookMark {
    //用链表把记录的书签存起来
    public ArrayList<Mark> list;
    public Log log;
    //查看书签的内容
    void read() {
        InputStreamReader isr = null;
        BufferedReader br = null;
        File file = new File("bookmark.txt");
        if(!file.exists()) {
            try {
                file.createNewFile();
            }catch(Exception e) {
                String s = e.getLocalizedMessage();
                e.printStackTrace();
                log.logger.warning(s);
            }
        }
        try {
            isr = new InputStreamReader(new FileInputStream(file),"utf-8");
            br = new BufferedReader(isr);
            String url = null, record = null;
            while(((record = br.readLine()) != null) && ((url = br.readLine()) != null)) {
                Mark mark = new Mark(url, record);
                list.add(mark);
            }
            log.logger.info("查看书签");
        }catch(Exception e) {
            String s = e.getLocalizedMessage();
            e.printStackTrace();
            log.logger.warning(s);
        }finally {
            try {
                //关闭读出流
                br.close();
            }catch(Exception e) {
                String s = e.getLocalizedMessage();
                e.printStackTrace();
//                log.logger.warning(s);
            }
        }
    }

    //保存书签
    void savebm() {
        File file = new File("bookmark.txt");
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
            for(Mark mark : list)
                bw.write(mark.record + "\r\n" + mark.url + "\r\n");
            bw.flush();
            bw.close();
            log.logger.info("成功保存书签");
        }catch(Exception e) {
            String s = e.getLocalizedMessage();
            e.printStackTrace();
            log.logger.warning(s);
        }finally {
            try {
                bw.close();
            }catch(Exception e) {
                String s = e.getLocalizedMessage();
                e.printStackTrace();
                log.logger.warning(s);
            }finally {
                try {
                    bw.close();
                }catch(Exception e) {
                    String s = e.getLocalizedMessage();
                    e.printStackTrace();
//                    log.logger.warning(s);
                }
            }
        }
    }

    void setNewMark(String url) {
        Shell shell=new Shell();
        shell.setText("为该书签命名");
        shell.setSize(450,85);
        shell.setLayout(new GridLayout(2,false));

        Text text=new Text(shell, SWT.CENTER|SWT.BORDER);
        text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Button ok=new Button(shell,SWT.PUSH);
        ok.setText("OK");

        text.addKeyListener(new KeyAdapter() {  //回车触发
            @Override
            public void keyReleased(KeyEvent e) {
                if (e.keyCode==SWT.CR||e.keyCode==SWT.KEYPAD_CR){
                    String name=text.getText();
                    while(name.equals("")){
//                        JOptionPane.showMessageDialog(null,"违法输入!",
//                                "错误",JOptionPane.INFORMATION_MESSAGE);
                        MessageBox box=new MessageBox(shell,SWT.ICON_ERROR|SWT.YES);
                        box.setText("错误");
                        box.setMessage("违法输入！");
                        int choice=box.open();
                        if(choice==SWT.YES)
                        {

                        }
                        name=text.getText();
                    }
                    for(int i=0;i<list.size();i++){
                        if(list.get(i).url.equals(url)){
//                            JOptionPane.showMessageDialog(null,"该书签已经存在!",
//                                    "信息",JOptionPane.INFORMATION_MESSAGE);
                            MessageBox box=new MessageBox(shell,SWT.ICON_WARNING|SWT.YES);
                            box.setText("");
                            box.setMessage("该书签已存在");
                            int choice=box.open();
                            if(choice==SWT.YES)
                            {

                            }
                            shell.dispose();
                            return;
                        }
                    }
                    Mark marks=new Mark();
                    marks.url=url;
                    marks.record=name;
                    list.add(marks);
                    shell.dispose();
                    savebm();
//                    JOptionPane.showMessageDialog(null,"成功!",
//                            "信息",JOptionPane.INFORMATION_MESSAGE);
                    MessageBox box=new MessageBox(shell,SWT.ICON_INFORMATION|SWT.YES);
                    box.setText(" ");
                    box.setMessage("成功！");
                    int choice=box.open();
                    if(choice==SWT.YES)
                    {

                    }

                }
            }
        });

        ok.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {  //点击触发
                String name=text.getText();
                if(name.equals("")){
//                    JOptionPane.showMessageDialog(null,"该域名即为书签名",
//                            "信息",JOptionPane.INFORMATION_MESSAGE);
                    MessageBox box=new MessageBox(shell,SWT.ICON_INFORMATION|SWT.YES);
                    box.setText(" ");
                    box.setMessage("该域名即为书签名");
                    int choice=box.open();
                    if(choice==SWT.YES)
                    {

                    }
                    name=url;
                }
                for(int i=0;i<list.size();i++){
                    if(list.get(i).url.equals(url)&&list.get(i).record.equals(name)){
//                        JOptionPane.showMessageDialog(null,"已经存在!",
//                                "信息",JOptionPane.INFORMATION_MESSAGE);
                        MessageBox box=new MessageBox(shell,SWT.ICON_WARNING|SWT.YES);
                        box.setText(" ");
                        box.setMessage("已经存在！");
                        int choice=box.open();
                        if(choice==SWT.YES)
                        {

                        }
                        shell.dispose();
                        return;
                    }
                }
                Mark marks=new Mark();
                marks.url=url;
                marks.record=name;
                list.add(marks);
                savebm();
//                JOptionPane.showMessageDialog(null,"成功!",
//                        "信息",JOptionPane.INFORMATION_MESSAGE);
                MessageBox box=new MessageBox(shell,SWT.ICON_INFORMATION|SWT.YES);
                box.setText(" ");
                box.setMessage("成功！");
                int choice=box.open();
                if(choice==SWT.YES)
                {

                }
                shell.dispose();
            }
        });

        shell.open();
    }

    void delete(int index){
        list.remove(index);
        savebm();
    }

    void deleteAll(){
        list.clear();
        savebm();
    }

    String getTitle(String url,Log log){
        if(!url.startsWith("http://")||!url.startsWith("https://")){
            url="http://"+url;
        }
        try {
            URLConnection connection=new URL(url).openConnection();
            InputStream is=connection.getInputStream();
            ByteArrayOutputStream bao=new ByteArrayOutputStream();
            int b=-1;
            while((b=is.read())!=-1){
                bao.write(b);
            }
            bao.flush();
            String code=new String(bao.toByteArray());

            String regex="<title>.*?</title>";
            Pattern pattern=Pattern.compile(regex);
            Matcher matcher=pattern.matcher(code);
            if(matcher.find()){
                String title=matcher.group();
                return title;
            }
            is.close();
            bao.close();
        }catch (Exception e){
            String str=e.getLocalizedMessage()+" "+e.getMessage();
            e.printStackTrace();
            log.logger.warning(str);
        }
        return url;
    }

    public BookMark(Log log) {
        // TODO Auto-generated constructor stub
        list = new ArrayList();
        this.log=log;
        read();
    }
    public int getNum() {
        return list.size();
    }
}

class Mark
{
    public String url,record;//书签需要记录的是网站和标记名
    //构造函数
    Mark() {
        url = null;
        record = null;
    }
    Mark(String u, String r) {
        this.url = u;
        this.record = r;
    }
}