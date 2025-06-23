package com.minizk;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

/**
 * Response from Mini ZooKeeper service with success status, error messages, and optional data.
 */
@Getter
public final class ZooKeeperResponse {
    
    private final boolean success;
    
    private final String errorMessage;
    
    private final byte[] data;
    
    private final List<String> children;

    public ZooKeeperResponse(boolean success, String errorMessage, byte[] data, List<String> children) {
        this.success = success;
        this.errorMessage = errorMessage;
        this.data = data != null ? data.clone() : null;
        this.children = children;
    }

    // Convenience constructors for backwards compatibility
    public ZooKeeperResponse(boolean success) {
        this(success, null, null, null);
    }

    public ZooKeeperResponse(boolean success, String errorMessage) {
        this(success, errorMessage, null, null);
    }

    public ZooKeeperResponse(boolean success, byte[] data) {
        this(success, null, data, null);
    }

    public ZooKeeperResponse(boolean success, List<String> children) {
        this(success, null, null, children);
    }


    public static ZooKeeperResponseBuilder builder() {
        return new ZooKeeperResponseBuilder();
    }

    public static final class ZooKeeperResponseBuilder {
        private boolean success;
        private String errorMessage;
        private byte[] data;
        private List<String> children;

        public ZooKeeperResponseBuilder success(boolean success) {
            this.success = success;
            return this;
        }

        public ZooKeeperResponseBuilder errorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
            return this;
        }

        public ZooKeeperResponseBuilder data(byte[] data) {
            this.data = data;
            return this;
        }

        public ZooKeeperResponseBuilder children(List<String> children) {
            this.children = children;
            return this;
        }

        public ZooKeeperResponse build() {
            return new ZooKeeperResponse(success, errorMessage, data, children);
        }
    }

    // Convenience static factory methods for common response types
    
    public static ZooKeeperResponse success() {
        return ZooKeeperResponse.builder().success(true).build();
    }

    public static ZooKeeperResponse failure() {
        return ZooKeeperResponse.builder().success(false).build();
    }

    public static ZooKeeperResponse error(String errorMessage) {
        return ZooKeeperResponse.builder()
            .success(false)
            .errorMessage(errorMessage)
            .build();
    }

    public static ZooKeeperResponse withData(byte[] data) {
        return ZooKeeperResponse.builder()
            .success(true)
            .data(data)
            .build();
    }

    public static ZooKeeperResponse withChildren(List<String> children) {
        return ZooKeeperResponse.builder()
            .success(true)
            .children(children)
            .build();
    }

    public byte[] getData() {
        return data != null ? data.clone() : null;
    }
}