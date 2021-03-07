package com.company;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.ShellAdapter;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowData;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.*;

import java.io.*;
import java.util.ArrayList;

public class fileUI {


    public void createUI(ArrayList<savefile> filelist,Display display)
    {
//        Display display = new Display();
        Shell shell = new Shell();
        shell.setSize(420,800);
        Image image=new Image(display,"bg4.jpg");
        shell.setBackgroundImage(image);
        shell.setBackgroundMode(SWT.INHERIT_FORCE);
        RowLayout rowLayout =new RowLayout();
        rowLayout.marginLeft = 25;
        rowLayout.marginRight = 25;
        rowLayout.marginTop = 25;
        rowLayout.spacing = 10;
        shell.setLayout(rowLayout);

        for(int i=0;i<filelist.size();i++)
        {
            savefile tempfile=filelist.get(i);
            String tempname=tempfile.filename;
            double tempratio=tempfile.bpr.ratio;
            Label fn=new Label(shell,SWT.NONE);
            fn.setText(tempname);
            RowData rowData=new RowData();
            rowData.width=350;
//            gridData.horizontalAlignment = GridData.BEGINNING;
            fn.setLayoutData(rowData);

            // 添加平滑的进度条
            ProgressBar pb1 = new ProgressBar(shell, SWT.HORIZONTAL | SWT.SMOOTH);
            RowData rowData1=new RowData();
            rowData1.width=330;
            pb1.setLayoutData(rowData1);
            // 设置进度条的最小值
            pb1.setMinimum(0);
            // 设置进度条的最大值
            pb1.setMaximum(100);

            Button buttonreload=new Button(shell,SWT.PUSH);
            RowData rowData2=new RowData();
            rowData2.width=100;
            buttonreload.setText("继续下载");
            buttonreload.setLayoutData(rowData2);
            buttonreload.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    tempfile.bpr.run();
                }
            });

            Button buttonpause=new Button(shell,SWT.PUSH);
            RowData rowData3=new RowData();
            rowData3.width=100;
            buttonpause.setText("暂停下载");
            buttonpause.setLayoutData(rowData3);
            buttonpause.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    tempfile.bpr.stop();
                }
            });

            Button buttonstop=new Button(shell,SWT.PUSH);
            RowData rowData4=new RowData();
            rowData4.width=100;
            buttonstop.setText("取消下载");
            buttonstop.setLayoutData(rowData4);
            int finalI = i;
            buttonstop.addSelectionListener(new SelectionAdapter() {
                @Override
                public void widgetSelected(SelectionEvent e) {
                    File file = new File("download.txt");
                    BufferedWriter bw = null;
                    filelist.remove(finalI);
                    System.out.println("删除");
                    for(int j=0;j<filelist.size();j++)
                    {
                        System.out.println(filelist.get(j).filename+" "+filelist.get(j).filepath);
                    }
                    try {
                        bw = new BufferedWriter(new FileWriter(file));
                        for(savefile sff: filelist) {
                            bw.write(sff.bpr.url + " " + sff.filename + " "+sff.filepath+" "+sff.bpr.totalsize);
                            bw.newLine();
                        }
                        bw.flush();
                        bw.close();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }

                }
            });

            Label blank=new Label(shell,SWT.NONE);
            RowData rowData5=new RowData();
            rowData5.width=380;
            blank.setLayoutData(rowData5);

            // 添加自动递增的进度条
//        ProgressBar pb2 = new ProgressBar(shell, SWT.HORIZONTAL
//                | SWT.INDETERMINATE);
//        pb2.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
            // 添加线程，在线程中处理长时间的任务，并最终反映在平滑进度条上
            new Thread(new LongRunningOperation(display, pb1,tempfile)).start();

        }

        shell.open();


        while (!shell.isDisposed()) {
            if (!display.readAndDispatch()) {
                display.sleep();
            }
        }
    }

    class LongRunningOperation implements Runnable {
        private Display display = null;
        private ProgressBar progressBar = null;
        public savefile currfile=null;
        public LongRunningOperation(Display display, ProgressBar progressBar,savefile currfile) {
            this.display = display;
            this.progressBar = progressBar;
            this.currfile=currfile;
        }

        public void run() {
            // 模仿长时间的任务
            for (int i = 0; i < 100; i++) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {

                }
                int slec=(int)(currfile.bpr.ratio);
                currfile.saveratio();
                display.asyncExec(new Runnable() {
                    public void run() {
                        if (progressBar.isDisposed())
                            return;
                        // 进度条递增a
                        progressBar.setSelection(slec);
                    }
                });
            }
        }
    }
}
