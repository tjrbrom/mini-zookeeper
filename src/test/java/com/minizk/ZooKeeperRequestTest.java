package com.minizk;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for ZooKeeperRequest class.
 */
@DisplayName("ZooKeeperRequest Tests")
class ZooKeeperRequestTest {

    private static final String TEST_PATH = "/test/path";
    private static final String TEST_DATA = "test data";

    @Test
    @DisplayName("Should create basic request")
    void shouldCreateBasicRequest() {
        ZooKeeperRequest request = new ZooKeeperRequest(
            ZooKeeperRequest.Operation.CREATE, TEST_PATH, TEST_DATA.getBytes());

        assertThat(request.getOperation()).isEqualTo(ZooKeeperRequest.Operation.CREATE);
        assertThat(request.getPath()).isEqualTo(TEST_PATH);
        assertThat(request.getData()).isEqualTo(TEST_DATA.getBytes());
    }
}