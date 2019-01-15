package com.zq.sword.array.io.file;

import com.zq.sword.array.io.*;
import com.zq.sword.array.io.ex.InputStreamOpenException;
import com.zq.sword.array.io.ex.OutputStreamOpenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

/**
 * @program: sword-array
 * @description: 本地文件系统存储
 * @author: zhouqi1
 * @create: 2019-01-14 11:12
 **/
public class FileSystemResourceStore implements ResourceStore {

    private Logger logger = LoggerFactory.getLogger(FileSystemResourceStore.class);

    private File file;

    public FileSystemResourceStore(File file) {
        this.file = file;
    }

    public FileSystemResourceStore(String filePath) {
        this(filePath == null ? null : new File(filePath));
    }

    @Override
    public ResourceInputStream openInputStream() throws InputStreamOpenException {
        try {
            return new FileResourceInputStream(file);
        } catch (IOException e) {
            throw new InputStreamOpenException(e.getMessage());
        }
    }

    @Override
    public ResourceOutputStream openOutputStream() throws OutputStreamOpenException {
        try {
            return new FileResourceOutputStream(file);
        } catch (IOException e) {
            throw new OutputStreamOpenException(e.getMessage());
        }
    }
}
