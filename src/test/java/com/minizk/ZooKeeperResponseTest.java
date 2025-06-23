package com.minizk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for ZooKeeperResponse class.
 */
@DisplayName("ZooKeeperResponse Tests")
class ZooKeeperResponseTest {

    private static final String TEST_ERROR = "Test error message";
    private static final String TEST_DATA = "test data";

    @Test
    @DisplayName("Should create success response")
    void shouldCreateSuccessResponse() {
        ZooKeeperResponse response = new ZooKeeperResponse(true);
        assertThat(response.isSuccess()).isTrue();
    }

    @Test
    @DisplayName("Should create error response")
    void shouldCreateErrorResponse() {
        ZooKeeperResponse response = new ZooKeeperResponse(false, TEST_ERROR);
        assertThat(response.isSuccess()).isFalse();
        assertThat(response.getErrorMessage()).isEqualTo(TEST_ERROR);
    }

    @Test
    @DisplayName("Should create data response")
    void shouldCreateDataResponse() {
        byte[] data = TEST_DATA.getBytes();
        ZooKeeperResponse response = new ZooKeeperResponse(true, data);
        assertThat(response.isSuccess()).isTrue();
        assertThat(response.getData()).isEqualTo(data);
    }

}