package com.minizk;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.nio.charset.StandardCharsets;

/**
 * Client for Mini ZooKeeper operations. Handles string data and converts to byte arrays internally.
 */
@Slf4j
@RequiredArgsConstructor
public final class MiniZooKeeperClient {
    
    @NonNull
    private final MiniZooKeeper zooKeeper;

    public boolean create(@NonNull String path, @NonNull String data) {
        log.debug("Creating ZNode at path: {} with data length: {}", path, data.length());
        ZooKeeperRequest request = ZooKeeperRequest.builder()
            .operation(ZooKeeperRequest.Operation.CREATE)
            .path(path)
            .data(data.getBytes(StandardCharsets.UTF_8))
            .build();
        ZooKeeperResponse response = zooKeeper.processRequest(request);
        if (!response.isSuccess()) {
            System.err.println("Create failed: " + response.getErrorMessage());
        }
        return response.isSuccess();
    }

    public String getData(@NonNull String path) {
        log.debug("Reading data from ZNode at path: {}", path);
        ZooKeeperRequest request = ZooKeeperRequest.builder()
            .operation(ZooKeeperRequest.Operation.READ)
            .path(path)
            .build();
        ZooKeeperResponse response = zooKeeper.processRequest(request);
        if (!response.isSuccess()) {
            System.err.println("Read failed: " + response.getErrorMessage());
            return null;
        }
        byte[] data = response.getData();
        return data != null ? new String(data, StandardCharsets.UTF_8) : "";
    }

    public boolean setData(@NonNull String path, @NonNull String data) {
        log.debug("Updating ZNode at path: {} with data length: {}", path, data.length());
        ZooKeeperRequest request = ZooKeeperRequest.builder()
            .operation(ZooKeeperRequest.Operation.UPDATE)
            .path(path)
            .data(data.getBytes(StandardCharsets.UTF_8))
            .build();
        ZooKeeperResponse response = zooKeeper.processRequest(request);
        if (!response.isSuccess()) {
            System.err.println("Update failed: " + response.getErrorMessage());
        }
        return response.isSuccess();
    }

    public boolean delete(@NonNull String path) {
        log.debug("Deleting ZNode at path: {}", path);
        ZooKeeperRequest request = ZooKeeperRequest.builder()
            .operation(ZooKeeperRequest.Operation.DELETE)
            .path(path)
            .build();
        ZooKeeperResponse response = zooKeeper.processRequest(request);
        if (!response.isSuccess()) {
            System.err.println("Delete failed: " + response.getErrorMessage());
        }
        return response.isSuccess();
    }

    public List<String> getChildren(@NonNull String path) {
        log.debug("Listing children of ZNode at path: {}", path);
        ZooKeeperRequest request = ZooKeeperRequest.builder()
            .operation(ZooKeeperRequest.Operation.LIST)
            .path(path)
            .build();
        ZooKeeperResponse response = zooKeeper.processRequest(request);
        if (!response.isSuccess()) {
            System.err.println("List failed: " + response.getErrorMessage());
            return null;
        }
        return response.getChildren();
    }
}