package com.company;

import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class savefile {
    public breakpointResume bpr;
    public int filesize=0;
    public double fileratio;
    public String filename;
    public String filepath;

    public savefile(String filename,String filepath,breakpointResume bpr)
    {
        this.filename=filename;
        this.bpr=bpr;
        fileratio=bpr.ratio;
        this.filepath=filepath;
    }

    public void saveratio()
    {
        fileratio=bpr.ratio;
        filesize=bpr.totalsize;
    }






//    public savefile(String )
}

 class savePage {

    savePage(Browser browser, Text text, Log log){
        BufferedWriter bw;
        String code=browser.getText();
        String name;

        String str=text.getText();
        String regex="www.*?.com";
        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(code);
        if(matcher.find()){
            name=matcher.group();
            name=name.substring(4,name.length()-4);
        }else {
            name=text.getText();
        }

        File file=new File(name+".html");
        try {
            if (!file.exists()){
                file.createNewFile();
            }
            bw=new BufferedWriter(new FileWriter(file));
            bw.write(code);
            bw.flush();
            bw.close();

//            JOptionPane.showMessageDialog(null,"保存成功！",
//                    "Message",JOptionPane.INFORMATION_MESSAGE);
            MessageBox box=new MessageBox(browser.getShell(), SWT.ICON_INFORMATION|SWT.YES);
            box.setText("");
            box.setMessage("保存成功！");
            int choice=box.open();
            if(choice==SWT.YES)
            {
                System.out.println("Yes");
            }

        }catch (Exception e){
            e.printStackTrace();
            String message=e.getLocalizedMessage()+" "+e.getMessage();
            log.logger.warning(message);

//            JOptionPane.showMessageDialog(null,"保存失败!",
//                    "Error",JOptionPane.INFORMATION_MESSAGE);
            MessageBox box=new MessageBox(browser.getShell(), SWT.ICON_INFORMATION|SWT.YES);
            box.setText("");
            box.setMessage("保存失败!");
            int choice=box.open();
            if(choice==SWT.YES)
            {
                System.out.println("Yes");
            }
        }
    }

}