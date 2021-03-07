package com.company;

import java.util.logging.*;
import java.util.Date;
import java.io.*;

public class Log {
	Logger logger;
	FileHandler file;
	Log() {
		logger = Logger.getLogger("Demo");
		try {
			FileHandler fileHandler = new FileHandler("log.txt");
			logger.addHandler(fileHandler);
			fileHandler.setFormatter(new SimpleFormatter());
			logger.info("新建");
		}
		catch(Exception e) {
			String str = e.getLocalizedMessage();
			e.printStackTrace();
			logger.warning(str);
		}
	}
}
