package com.library.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Component
public class JsonFileUtil {
    
    @Value("${library.data.path:./data}")
    private String dataPath;
    
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(dataPath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("初始化数据目录失败", e);
        }
    }
    
    public <T> List<T> readList(String fileName, Class<T> clazz) {
        lock.readLock().lock();
        try {
            File file = new File(dataPath, fileName);
            if (!file.exists()) {
                return new ArrayList<>();
            }
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            if (content.trim().isEmpty()) {
                return new ArrayList<>();
            }
            return JSON.parseArray(content, clazz);
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败: " + fileName, e);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public <T> void writeList(String fileName, List<T> list) {
        lock.writeLock().lock();
        try {
            File file = new File(dataPath, fileName);
            String content = JSON.toJSONString(list, true);
            Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("写入文件失败: " + fileName, e);
        } finally {
            lock.writeLock().unlock();
        }
    }
    
    public <T> T readObject(String fileName, TypeReference<T> typeReference) {
        lock.readLock().lock();
        try {
            File file = new File(dataPath, fileName);
            if (!file.exists()) {
                return null;
            }
            String content = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            if (content.trim().isEmpty()) {
                return null;
            }
            return JSON.parseObject(content, typeReference);
        } catch (IOException e) {
            throw new RuntimeException("读取文件失败: " + fileName, e);
        } finally {
            lock.readLock().unlock();
        }
    }
    
    public <T> void writeObject(String fileName, T object) {
        lock.writeLock().lock();
        try {
            File file = new File(dataPath, fileName);
            String content = JSON.toJSONString(object, true);
            Files.write(file.toPath(), content.getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            throw new RuntimeException("写入文件失败: " + fileName, e);
        } finally {
            lock.writeLock().unlock();
        }
    }
}
