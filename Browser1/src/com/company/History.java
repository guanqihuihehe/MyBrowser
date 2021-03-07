package com.company;

import java.io.*;
import java.util.*;

public class History {
	public ArrayList<Record> list;
	Log log;
	History(Log log) {
		list = new ArrayList<>();
		read();
		this.log=log;
	}

	//保存历史记录
	void save() {
		BufferedWriter bw = null;
		try {
			bw = new BufferedWriter(new FileWriter("historyRecord.txt"));
			for(int i = 0; i < list.size(); i++) {
				String url = list.get(i).url;
				String name = list.get(i).name;
//				System.out.println(url+ " "+name);
				bw.write(name + "\r\n" + url + "\r\n");
			}
			bw.close();
			log.logger.info("成功保存历史记录");
		} catch(Exception e) {
			log.logger.info(e.toString());
			e.printStackTrace();
		}finally {
			try {
				bw.close();
			} catch(Exception e) {
				log.logger.info(e.toString());
				e.printStackTrace();
			}
		}
	}
	//读取历史记录
	void read() {
		InputStreamReader isr = null;
		BufferedReader br = null;
		File file = new File("historyRecord.txt");//写入文件中
		if(!file.exists()) {
			try {
				file.createNewFile();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		try {
			isr = new InputStreamReader(new FileInputStream(file), "utf-8");
			br = new BufferedReader(isr);
			String url = null, name = null;
			while(((name = br.readLine()) != null) && ((url = br.readLine()) != null))
			{
				Record record = new Record(url, name);
				list.add(record);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				br.close();
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	void setNewRecord(String url, String name) {
		for(int i = 0; i < list.size(); i++) {
			//如果该url之前已经浏览过，则更新它的时间
			if(list.get(i).url.equals(url))
				list.remove(i);
		}
		Record record = new Record();
		record.name = name;
		record.url = url;
		list.add(record);
		save();
	}
	void deleteAll() {
		list.clear();
		save();
	}
	int getNum() {
		return list.size();
	}
}
class Record
{
	public String url,name;
	Record() {
		url = null;
		name = null;
	}
	Record(String url, String name) {
		this.url = url;
		this.name = name;
	}
}