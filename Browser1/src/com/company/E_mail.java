package com.company;

import com.sun.mail.smtp.SMTPMessage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import javax.mail.*;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMessage.RecipientType;
import javax.mail.internet.MimeMultipart;
import javax.swing.*;
import java.util.Date;
import java.util.Properties;

public class E_mail {
    Properties properties;
    Session session;
    MimeMessage mimeMessage;
    Log log;
    public E_mail(Log log) {
        properties=new Properties();
        session=Session.getInstance(properties);
        mimeMessage=new MimeMessage(session);
        this.log=log;
    }
    void createUI(Display display){
        Shell mashell=new Shell(SWT.RESIZE|SWT.MAX);
        Image image=new Image(display,"bg4.jpg");
        mashell.setBackgroundImage(image);
        mashell.setBackgroundMode(SWT.INHERIT_FORCE);
        mashell.setText("E-mail");
        mashell.setSize(800,700);
        log.logger.info("新建Email");
        RowLayout rowLayout = new RowLayout();
        rowLayout.marginLeft = 35;
        rowLayout.marginRight = 35;
        rowLayout.marginTop = 40;
        rowLayout.spacing = 30;
        mashell.setLayout(rowLayout);
        RowData rowData=new RowData();

        Label sender=new Label(mashell,SWT.NONE);
        sender.setText("发件邮箱：");
        Text setext=new Text(mashell,SWT.BORDER);
        rowData=new RowData();
        rowData.width=600;
        setext.setLayoutData(rowData);

        Label password=new Label(mashell,SWT.NONE);
        password.setText("密        码：");
        Text passwordtext=new Text(mashell,SWT.BORDER);
        rowData=new RowData();
        rowData.width=600;
        passwordtext.setLayoutData(rowData);

        Label receiver=new Label(mashell,SWT.NONE);
        receiver.setText("收件邮箱：");
        Text retext=new Text(mashell,SWT.BORDER);
        rowData=new RowData();
        rowData.width=600;
        retext.setLayoutData(rowData);

        Label theme=new Label(mashell,SWT.NONE);
        theme.setText("主        题：");
        Text thtext=new Text(mashell,SWT.BORDER);
        rowData=new RowData();
        rowData.width=600;
        thtext.setLayoutData(rowData);

        Label content=new Label(mashell,SWT.NONE);
        content.setText("内        容：");
        Text cotext=new Text(mashell,SWT.BORDER|SWT.V_SCROLL|SWT.WRAP);
        rowData=new RowData();
        rowData.width=575;
        rowData.height=300;
        cotext.setLayoutData(rowData);
        Button button=new Button(mashell,SWT.PUSH);
        button.setText("发送邮件");
        button.addSelectionListener(new SelectionAdapter() {
            @Override
            public void widgetSelected(SelectionEvent e) {
                Properties props = new Properties();
                props.setProperty("mail.transport.protocol", "SMTP");
                props.setProperty("mail.smtp.host", "smtp.163.com");
                props.setProperty("mail.smtp.port", "25");
                // 指定验证为true
                props.setProperty("mail.smtp.auth", "true");
                props.setProperty("mail.smtp.timeout", "4000");  // 设置是否使用ssl安全连接 ---一般都使用
//     	        properties.put("mail.debug", "true"); // 设置是否显示debug信息 true 会在控制台显示相关信息

                String username=setext.getText();
                String pw=passwordtext.getText();

                Authenticator auth = new Authenticator() {

                    public PasswordAuthentication getPasswordAuthentication() {

//                        return new PasswordAuthentication("18718259883@163.com", "RLSDTUEVSAYZVDWY");
                        return new PasswordAuthentication(username, pw);
                    }
                };

                // 得到回话对象
                Session session = Session.getInstance(props, auth);
                // 获取邮件对象
                Message message = new MimeMessage(session);
                try {

                    // 设置发送者
                    message.setFrom(new InternetAddress(username));
                    //设置收件者
                    String re=retext.getText();
                    message.setRecipient(MimeMessage.RecipientType.TO,
                            new InternetAddress(re));
                    // 设置主题
                    message.setSubject(thtext.getText());
                    // 设置内容
                    message.setContent(cotext.getText(), "text/html;charset=utf-8");
                    // 设置发信时间
                    message.setSentDate(new Date());
                    // 创建 Transport用于将邮件发送
                    Transport.send(message);
                    MessageBox box=new MessageBox(mashell,SWT.ICON_INFORMATION|SWT.YES);
                    box.setText("发送邮件");
                    box.setMessage("发送成功");
                    int choice=box.open();
                    if(choice==SWT.YES)
                    {
                        log.logger.info("发送邮件成功");
                    }
                } catch (AddressException e1) {
                    MessageBox box=new MessageBox(mashell,SWT.ICON_ERROR|SWT.YES);
                    box.setText("发送邮件");
                    box.setMessage("发送失败:\n"+e1);
                    int choice=box.open();
                    if(choice==SWT.YES)
                    {

                    }
                    e1.printStackTrace();
                } catch (MessagingException e1) {
                    e1.printStackTrace();
                }
            }
        });
        mashell.open();
    }
}