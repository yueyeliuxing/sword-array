package com.zq.sword.array.common.utils;

import com.google.common.base.Charsets;
import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * @program: sword-array
 * @description: 文件工具类
 * @author: zhouqi1
 * @create: 2018-07-26 10:38
 **/
public class FileUtil{

    private static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    /**
     * 获取指定文件夹下的子文件
     * @param parentPath 文件夹路径
     * @return 子文件
     */
    public static File[] listChildFile(String parentPath){
        return listChildFile(parentPath, null);
    }

    /**
     * 获取指定文件夹下的子文件
     * @param parentPath 文件夹路径
     * @param fileEndWith 文件后缀
     * @return 子文件
     */
    public static File[] listChildFile(String parentPath, String fileEndWith){
        File parentFile =  new File(parentPath);
        return parentFile.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return fileEndWith != null ? name.endsWith(fileEndWith) : true;
            }
        });
    }

    /**
     * 读文件内容
     * @param filePath
     * @param pos
     * @return
     */
    public static List<String> readLines(String filePath, long pos, Integer num){
        List<String> dataLines = new ArrayList<>();
        try(RandomAccessFile file = new RandomAccessFile(filePath, "rw")){
            file.seek(pos);
            StringBuilder sb = new StringBuilder();
            byte[] temp = new byte[1024];
            int line = 0;
            while ((line = file.read(temp)) != -1) {
                sb.append(new String(temp,0,line));
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(sb.toString().getBytes())));
            String dataLine = null;
            int i = 0;
            while((dataLine = bufferedReader.readLine())!=null){
                if(num != null && i>=num){
                    break;
                }
                dataLines.add(dataLine);
                i++;
            }
            return dataLines;
        }catch (Exception e){
            logger.error("RandomAccessFile", e);
            return new ArrayList<>();
        }
    }
    /**
     * 读文件内容
     * @param filePath
     * @param pos
     * @return
     */
    public static List<String> readLines(String filePath, long pos){
        return readLines(filePath, pos, null);
    }

    /**
     * 读文件内容
     * @param file
     * @return
     */
    public static List<String> readLines(File file){
        try{
            return Files.readLines(file, Charsets.UTF_8);
        }catch (Exception e){
            logger.error(" Files.readLines", e);
            return null;
        }
    }

    /**
     * 追加文件内容
     * @param file
     * @param dataLine
     */
    public static void appendLine(File file, String dataLine){
        try{
            Files.append(dataLine+System.lineSeparator(), file, Charsets.UTF_8);
        }catch (Exception e){
            logger.error(" Files.append", e);
        }
    }

    public static String getCRC32(String fileUri) {
        CRC32 crc32 = new CRC32();
        FileInputStream fileinputstream = null;
        CheckedInputStream checkedinputstream = null;
        String crc = null;
        try {
            fileinputstream = new FileInputStream(new File(fileUri));
            checkedinputstream = new CheckedInputStream(fileinputstream, crc32);
            while (checkedinputstream.read() != -1) {
            }
            crc = Long.toHexString(crc32.getValue()).toUpperCase();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileinputstream != null) {
                try {
                    fileinputstream.close();
                } catch (IOException e2) {
                    e2.printStackTrace();
                }
            }
            if (checkedinputstream != null) {
                try {
                    checkedinputstream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return crc;
    }
}
