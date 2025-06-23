package com.minizk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for ZNode class.
 */
@DisplayName("ZNode Tests")
class ZNodeTest {

    private ZNode znode;
    private static final String TEST_PATH = "/test/path";
    private static final String TEST_DATA = "test data";

    @BeforeEach
    void setUp() {
        znode = new ZNode(TEST_PATH);
    }

    @Test
    @DisplayName("Should create ZNode with valid path")
    void shouldCreateZNodeWithValidPath() {
        ZNode node = new ZNode("/valid/path");
        
        assertThat(node.getPath()).isEqualTo("/valid/path");
        assertThat(node.getData()).isEmpty();
        assertThat(node.getChildren()).isEmpty();
        assertThat(node.getVersion()).isZero();
    }

    @Test
    @DisplayName("Should set and get data")
    void shouldSetAndGetData() {
        byte[] data = TEST_DATA.getBytes();
        znode.setData(data);
        
        assertThat(znode.getData()).isEqualTo(data);
        assertThat(znode.getVersion()).isEqualTo(1);
    }

    @Test
    @DisplayName("Should manage children")
    void shouldManageChildren() {
        ZNode child1 = new ZNode("/test/path/child1");
        ZNode child2 = new ZNode("/test/path/child2");
        
        znode.addChild("child1", child1);
        znode.addChild("child2", child2);
        
        assertThat(znode.getChildren()).hasSize(2);
        assertThat(znode.getChildrenNames()).containsExactlyInAnyOrder("child1", "child2");
        
        znode.removeChild("child1");
        assertThat(znode.getChildren()).hasSize(1);
        assertThat(znode.getChildrenNames()).containsExactly("child2");
    }


}