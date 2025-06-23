package com.minizk;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * ZNode in the hierarchical namespace. Can store data and have children.
 * Thread-safe with version tracking.
 */
@Slf4j
public final class ZNode {
    
    @Getter
    private final String path;
    
    private byte[] data;
    
    @Getter
    private final Map<String, ZNode> children;
    
    @Getter
    private long version;
    
    @Getter
    private final long createdTime;
    
    @Getter
    private long modifiedTime;

    public ZNode(String path) {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("Path cannot be null or empty");
        }
        
        this.path = path;
        this.data = new byte[0];
        this.children = new ConcurrentHashMap<>();
        this.version = 0;
        this.createdTime = System.currentTimeMillis();
        this.modifiedTime = this.createdTime;
        
        log.debug("Created ZNode at path: {}", path);
    }

    public byte[] getData() {
        return data.clone();
    }

    public void setData(byte[] data) {
        if (data == null) {
            throw new IllegalArgumentException("Data cannot be null");
        }
        
        this.data = data.clone();
        this.version++;
        this.modifiedTime = System.currentTimeMillis();
        
        log.debug("Updated data for ZNode at path: {}, new version: {}", path, version);
    }

    public void addChild(String name, ZNode child) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Child name cannot be null or empty");
        }
        if (child == null) {
            throw new IllegalArgumentException("Child ZNode cannot be null");
        }
        
        children.put(name, child);
        log.debug("Added child '{}' to ZNode at path: {}", name, path);
    }

    public void removeChild(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Child name cannot be null or empty");
        }
        
        ZNode removed = children.remove(name);
        if (removed != null) {
            log.debug("Removed child '{}' from ZNode at path: {}", name, path);
        }
    }

    public List<String> getChildrenNames() {
        return new ArrayList<>(children.keySet());
    }
}