package com.minizk;

import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;

/**
 * Request to the Mini ZooKeeper service with operation, path, and optional data.
 */
@Getter
public final class ZooKeeperRequest {
    
    public enum Operation {
        /** Create a new ZNode */
        CREATE,
        /** Read data from an existing ZNode */
        READ,
        /** Update data in an existing ZNode */
        UPDATE,
        /** Delete an existing ZNode */
        DELETE,
        /** List children of an existing ZNode */
        LIST
    }

    @NonNull
    private final Operation operation;
    
    @NonNull
    private final String path;
    
    private final byte[] data;

    public ZooKeeperRequest(@NonNull Operation operation, @NonNull String path) {
        this.operation = operation;
        this.path = path;
        this.data = new byte[0];
    }

    public ZooKeeperRequest(@NonNull Operation operation, @NonNull String path, @NonNull byte[] data) {
        this.operation = operation;
        this.path = path;
        this.data = data != null ? data.clone() : new byte[0];
    }

    public static ZooKeeperRequestBuilder builder() {
        return new ZooKeeperRequestBuilder();
    }

    public static final class ZooKeeperRequestBuilder {
        private Operation operation;
        private String path;
        private byte[] data = new byte[0];

        public ZooKeeperRequestBuilder operation(Operation operation) {
            this.operation = operation;
            return this;
        }

        public ZooKeeperRequestBuilder path(String path) {
            this.path = path;
            return this;
        }

        public ZooKeeperRequestBuilder data(byte[] data) {
            this.data = data;
            return this;
        }

        public ZooKeeperRequest build() {
            return new ZooKeeperRequest(operation, path, data);
        }
    }


    public byte[] getData() {
        return data.clone();
    }
}