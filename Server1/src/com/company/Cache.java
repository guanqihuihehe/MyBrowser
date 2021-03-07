package com.company;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;

public class Cache {
    private static final Logger logger = LogManager.getLogger(Cache.class.getCanonicalName());
    private File rootDirectory;
    private HashMap<String, byte[]> hashMap = new HashMap<>();
    private boolean canread;

    public Cache(File rootDirectory) {
        this.rootDirectory = rootDirectory;
        canread = false;
        System.out.println("cached:");
        setBytes();
    }

    private void setBytes() {
        String filename[] = {"index.html","post.html","bg4.jpg" ,"img\\background.jpg", "img\\view.jpg"};
        try{
            for(String s: filename) {
                File theFile = new File(rootDirectory, s);
                if(theFile.canRead()){
                    // 用hashmap来让路径和文件信息一一对应
                    byte[] bytes = Files.readAllBytes(theFile.toPath());
                    hashMap.put(theFile.getPath(), bytes);
                    canread = true;
                }
            }

        }catch (IOException e){
            e.printStackTrace();
            logger.log(Level.ERROR, "Error opening file " + rootDirectory + "\\" + filename);
        }
    }

    public HashMap<String, byte[]> getHashMap() {
        return hashMap;
    }

    public boolean isCanread() {
        return canread;
    }
}
