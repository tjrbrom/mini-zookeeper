package com.minizk;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Simplified ZooKeeper-like service with hierarchical namespace and CRUD operations.
 * Thread-safe.
 */
@Slf4j
public final class MiniZooKeeper {
    
    private static final String ROOT_PATH = "/";
    private static final String INVALID_PATH_FORMAT = "Invalid path format";
    private static final String NODE_ALREADY_EXISTS = "Node already exists";
    private static final String PARENT_NODE_DOES_NOT_EXIST = "Parent node does not exist";
    private static final String NODE_DOES_NOT_EXIST = "Node does not exist";
    private static final String CANNOT_DELETE_ROOT_NODE = "Cannot delete root node";
    private static final String NODE_HAS_CHILDREN = "Node has children";
    private final ZNode root;
    private final Map<String, ZNode> nodeCache;

    public MiniZooKeeper() {
        this.root = new ZNode(ROOT_PATH);
        this.nodeCache = new ConcurrentHashMap<>();
        this.nodeCache.put(ROOT_PATH, root);
        
        log.info("Mini ZooKeeper initialized with root node");
    }

    /**
     * Processes a ZooKeeper request. All operations are atomic and thread-safe.
     */
    public synchronized ZooKeeperResponse processRequest(ZooKeeperRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        
        log.debug("Processing {} request for path: {}", request.getOperation(), request.getPath());
        switch (request.getOperation()) {
            case CREATE:
                return create(request.getPath(), request.getData());
            case READ:
                return read(request.getPath());
            case UPDATE:
                return update(request.getPath(), request.getData());
            case DELETE:
                return delete(request.getPath());
            case LIST:
                return listChildren(request.getPath());
            default:
                return ZooKeeperResponse.error("Unknown operation");
        }
    }

    private ZooKeeperResponse create(String path, byte[] data) {
        if (!isValidPath(path)) {
            return ZooKeeperResponse.error(INVALID_PATH_FORMAT);
        }
        
        if (nodeCache.containsKey(path)) {
            return ZooKeeperResponse.error(NODE_ALREADY_EXISTS);
        }

        String parentPath = getParentPath(path);
        ZNode parent = nodeCache.get(parentPath);
        if (parent == null) {
            return ZooKeeperResponse.error(PARENT_NODE_DOES_NOT_EXIST);
        }

        ZNode newNode = new ZNode(path);
        newNode.setData(data);
        
        String nodeName = getNodeName(path);
        parent.addChild(nodeName, newNode);
        nodeCache.put(path, newNode);

        return ZooKeeperResponse.success();
    }

    private ZooKeeperResponse read(String path) {
        if (!isValidPath(path)) {
            return ZooKeeperResponse.error(INVALID_PATH_FORMAT);
        }
        
        ZNode node = nodeCache.get(path);
        if (node == null) {
            return ZooKeeperResponse.error(NODE_DOES_NOT_EXIST);
        }
        return ZooKeeperResponse.withData(node.getData());
    }

    private ZooKeeperResponse update(String path, byte[] data) {
        if (!isValidPath(path)) {
            return ZooKeeperResponse.error(INVALID_PATH_FORMAT);
        }
        
        ZNode node = nodeCache.get(path);
        if (node == null) {
            return ZooKeeperResponse.error(NODE_DOES_NOT_EXIST);
        }
        node.setData(data);
        return ZooKeeperResponse.success();
    }

    private ZooKeeperResponse delete(String path) {
        if (!isValidPath(path)) {
            return ZooKeeperResponse.error(INVALID_PATH_FORMAT);
        }
        
        if (path.equals(ROOT_PATH)) {
            return ZooKeeperResponse.error(CANNOT_DELETE_ROOT_NODE);
        }

        ZNode node = nodeCache.get(path);
        if (node == null) {
            return ZooKeeperResponse.error(NODE_DOES_NOT_EXIST);
        }

        if (!node.getChildren().isEmpty()) {
            return ZooKeeperResponse.error(NODE_HAS_CHILDREN);
        }

        String parentPath = getParentPath(path);
        ZNode parent = nodeCache.get(parentPath);
        if (parent != null) {
            String nodeName = getNodeName(path);
            parent.removeChild(nodeName);
        }

        nodeCache.remove(path);
        return ZooKeeperResponse.success();
    }

    private ZooKeeperResponse listChildren(String path) {
        if (!isValidPath(path)) {
            return ZooKeeperResponse.error(INVALID_PATH_FORMAT);
        }
        
        ZNode node = nodeCache.get(path);
        if (node == null) {
            return ZooKeeperResponse.error(NODE_DOES_NOT_EXIST);
        }
        return ZooKeeperResponse.withChildren(node.getChildrenNames());
    }

    private boolean isValidPath(String path) {
        if (path == null || path.isEmpty()) {
            return false;
        }
        if (!path.startsWith(ROOT_PATH)) {
            return false;
        }
        if (path.length() > 1 && path.endsWith("/")) {
            return false;
        }
        if (path.contains("//")) {
            return false;
        }
        return true;
    }

    private String getParentPath(String path) {
        if (path.equals(ROOT_PATH)) {
            return null;
        }
        int lastSlash = path.lastIndexOf('/');
        if (lastSlash == 0) {
            return ROOT_PATH;
        }
        return path.substring(0, lastSlash);
    }

    private String getNodeName(String path) {
        if (path.equals(ROOT_PATH)) {
            return "";
        }
        int lastSlash = path.lastIndexOf('/');
        return path.substring(lastSlash + 1);
    }
}