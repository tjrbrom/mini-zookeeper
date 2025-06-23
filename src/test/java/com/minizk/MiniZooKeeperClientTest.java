package com.minizk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

/**
 * Integration tests for MiniZooKeeperClient class.
 */
@DisplayName("MiniZooKeeperClient Tests")
class MiniZooKeeperClientTest {

    private MiniZooKeeper zooKeeper;
    private MiniZooKeeperClient client;
    private static final String TEST_PATH = "/test";
    private static final String TEST_DATA = "test data";

    @BeforeEach
    void setUp() {
        zooKeeper = new MiniZooKeeper();
        client = new MiniZooKeeperClient(zooKeeper);
    }

    @Nested
    @DisplayName("Constructor Tests")
    class ConstructorTests {

        @Test
        @DisplayName("Should create client with valid ZooKeeper instance")
        void shouldCreateClientWithValidZooKeeperInstance() {
            MiniZooKeeper zk = new MiniZooKeeper();
            
            assertThatCode(() -> new MiniZooKeeperClient(zk))
                .doesNotThrowAnyException();
        }

        @Test
        @DisplayName("Should throw exception for null ZooKeeper instance")
        void shouldThrowExceptionForNullZooKeeperInstance() {
            assertThatThrownBy(() -> new MiniZooKeeperClient(null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Create Operation Tests")
    class CreateOperationTests {

        @Test
        @DisplayName("Should create node successfully")
        void shouldCreateNodeSuccessfully() {
            boolean result = client.create(TEST_PATH, TEST_DATA);
            
            assertThat(result).isTrue();
            
            // Verify node was created
            String retrievedData = client.getData(TEST_PATH);
            assertThat(retrievedData).isEqualTo(TEST_DATA);
        }

        @Test
        @DisplayName("Should return false for duplicate node creation")
        void shouldReturnFalseForDuplicateNodeCreation() {
            // Create node first
            boolean firstCreate = client.create(TEST_PATH, TEST_DATA);
            assertThat(firstCreate).isTrue();
            
            // Try to create same node again
            boolean secondCreate = client.create(TEST_PATH, "different data");
            assertThat(secondCreate).isFalse();
        }

        @Test
        @DisplayName("Should return false for invalid parent path")
        void shouldReturnFalseForInvalidParentPath() {
            boolean result = client.create("/nonexistent/child", TEST_DATA);
            
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should handle empty data")
        void shouldHandleEmptyData() {
            boolean result = client.create(TEST_PATH, "");
            
            assertThat(result).isTrue();
            assertThat(client.getData(TEST_PATH)).isEmpty();
        }

        @Test
        @DisplayName("Should throw exception for null path")
        void shouldThrowExceptionForNullPath() {
            assertThatThrownBy(() -> client.create(null, TEST_DATA))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should throw exception for null data")
        void shouldThrowExceptionForNullData() {
            assertThatThrownBy(() -> client.create(TEST_PATH, null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Get Data Operation Tests")
    class GetDataOperationTests {

        @Test
        @DisplayName("Should retrieve data from existing node")
        void shouldRetrieveDataFromExistingNode() {
            client.create(TEST_PATH, TEST_DATA);
            
            String retrievedData = client.getData(TEST_PATH);
            
            assertThat(retrievedData).isEqualTo(TEST_DATA);
        }

        @Test
        @DisplayName("Should return null for non-existent node")
        void shouldReturnNullForNonExistentNode() {
            String result = client.getData("/nonexistent");
            
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("Should handle empty data")
        void shouldHandleEmptyData() {
            client.create(TEST_PATH, "");
            
            String retrievedData = client.getData(TEST_PATH);
            
            assertThat(retrievedData).isEmpty();
        }

        @Test
        @DisplayName("Should handle special characters in data")
        void shouldHandleSpecialCharactersInData() {
            String specialData = "Special chars: äöü @#$%^&*()";
            client.create(TEST_PATH, specialData);
            
            String retrievedData = client.getData(TEST_PATH);
            
            assertThat(retrievedData).isEqualTo(specialData);
        }

        @Test
        @DisplayName("Should throw exception for null path")
        void shouldThrowExceptionForNullPath() {
            assertThatThrownBy(() -> client.getData(null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Set Data Operation Tests")
    class SetDataOperationTests {

        @Test
        @DisplayName("Should update data in existing node")
        void shouldUpdateDataInExistingNode() {
            client.create(TEST_PATH, TEST_DATA);
            
            String newData = "updated data";
            boolean result = client.setData(TEST_PATH, newData);
            
            assertThat(result).isTrue();
            assertThat(client.getData(TEST_PATH)).isEqualTo(newData);
        }

        @Test
        @DisplayName("Should return false for non-existent node")
        void shouldReturnFalseForNonExistentNode() {
            boolean result = client.setData("/nonexistent", TEST_DATA);
            
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should handle multiple updates")
        void shouldHandleMultipleUpdates() {
            client.create(TEST_PATH, "initial");
            
            assertThat(client.setData(TEST_PATH, "update1")).isTrue();
            assertThat(client.getData(TEST_PATH)).isEqualTo("update1");
            
            assertThat(client.setData(TEST_PATH, "update2")).isTrue();
            assertThat(client.getData(TEST_PATH)).isEqualTo("update2");
            
            assertThat(client.setData(TEST_PATH, "final")).isTrue();
            assertThat(client.getData(TEST_PATH)).isEqualTo("final");
        }

        @Test
        @DisplayName("Should throw exception for null path")
        void shouldThrowExceptionForNullPath() {
            assertThatThrownBy(() -> client.setData(null, TEST_DATA))
                .isInstanceOf(NullPointerException.class);
        }

        @Test
        @DisplayName("Should throw exception for null data")
        void shouldThrowExceptionForNullData() {
            assertThatThrownBy(() -> client.setData(TEST_PATH, null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Delete Operation Tests")
    class DeleteOperationTests {

        @Test
        @DisplayName("Should delete existing leaf node")
        void shouldDeleteExistingLeafNode() {
            client.create(TEST_PATH, TEST_DATA);
            
            boolean result = client.delete(TEST_PATH);
            
            assertThat(result).isTrue();
            assertThat(client.getData(TEST_PATH)).isNull();
        }

        @Test
        @DisplayName("Should return false for non-existent node")
        void shouldReturnFalseForNonExistentNode() {
            boolean result = client.delete("/nonexistent");
            
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false when trying to delete root")
        void shouldReturnFalseWhenTryingToDeleteRoot() {
            boolean result = client.delete("/");
            
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should return false for node with children")
        void shouldReturnFalseForNodeWithChildren() {
            client.create("/parent", "parent data");
            client.create("/parent/child", "child data");
            
            boolean result = client.delete("/parent");
            
            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("Should allow deletion after removing children")
        void shouldAllowDeletionAfterRemovingChildren() {
            client.create("/parent", "parent data");
            client.create("/parent/child", "child data");
            
            // Delete child first
            assertThat(client.delete("/parent/child")).isTrue();
            
            // Now delete parent should work
            assertThat(client.delete("/parent")).isTrue();
        }

        @Test
        @DisplayName("Should throw exception for null path")
        void shouldThrowExceptionForNullPath() {
            assertThatThrownBy(() -> client.delete(null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Get Children Operation Tests")
    class GetChildrenOperationTests {

        @Test
        @DisplayName("Should list children of existing node")
        void shouldListChildrenOfExistingNode() {
            client.create("/parent", "parent data");
            client.create("/parent/child1", "child1 data");
            client.create("/parent/child2", "child2 data");
            client.create("/parent/child3", "child3 data");
            
            List<String> children = client.getChildren("/parent");
            
            assertThat(children).isNotNull();
            assertThat(children).containsExactlyInAnyOrder("child1", "child2", "child3");
        }

        @Test
        @DisplayName("Should return empty list for node with no children")
        void shouldReturnEmptyListForNodeWithNoChildren() {
            client.create(TEST_PATH, TEST_DATA);
            
            List<String> children = client.getChildren(TEST_PATH);
            
            assertThat(children).isNotNull();
            assertThat(children).isEmpty();
        }

        @Test
        @DisplayName("Should return null for non-existent node")
        void shouldReturnNullForNonExistentNode() {
            List<String> children = client.getChildren("/nonexistent");
            
            assertThat(children).isNull();
        }

        @Test
        @DisplayName("Should list root children")
        void shouldListRootChildren() {
            client.create("/app", "app data");
            client.create("/config", "config data");
            
            List<String> children = client.getChildren("/");
            
            assertThat(children).containsExactlyInAnyOrder("app", "config");
        }

        @Test
        @DisplayName("Should throw exception for null path")
        void shouldThrowExceptionForNullPath() {
            assertThatThrownBy(() -> client.getChildren(null))
                .isInstanceOf(NullPointerException.class);
        }
    }

    @Nested
    @DisplayName("Integration Workflow Tests")
    class IntegrationWorkflowTests {

        @Test
        @DisplayName("Should handle basic application workflow")
        void shouldHandleBasicApplicationWorkflow() {
            // Setup simple structure
            assertThat(client.create("/app", "Application root")).isTrue();
            assertThat(client.create("/app/config", "Configuration data")).isTrue();

            // Verify structure
            List<String> appChildren = client.getChildren("/app");
            assertThat(appChildren).containsExactly("config");

            // Update configuration
            assertThat(client.setData("/app/config", "Updated config")).isTrue();
            assertThat(client.getData("/app/config")).isEqualTo("Updated config");

            // Cleanup
            assertThat(client.delete("/app/config")).isTrue();
            assertThat(client.delete("/app")).isTrue();
        }
    }

}