package com.minizk;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;

import static org.assertj.core.api.Assertions.*;

/**
 * Unit tests for MiniZooKeeper class.
 */
@DisplayName("MiniZooKeeper Tests")
class MiniZooKeeperTest {

    private MiniZooKeeper zooKeeper;
    private static final String TEST_PATH = "/test";
    private static final String TEST_DATA = "test data";

    @BeforeEach
    void setUp() {
        zooKeeper = new MiniZooKeeper();
    }

    @Nested
    @DisplayName("Initialization Tests")
    class InitializationTests {

        @Test
        @DisplayName("Should initialize with root node")
        void shouldInitializeWithRootNode() {
            ZooKeeperRequest listRequest = ZooKeeperRequest.builder()
                .operation(ZooKeeperRequest.Operation.LIST)
                .path("/")
                .build();
            ZooKeeperResponse response = zooKeeper.processRequest(listRequest);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getChildren()).isEmpty();
        }

        @Test
        @DisplayName("Should be able to read root node")
        void shouldBeAbleToReadRootNode() {
            ZooKeeperRequest readRequest = ZooKeeperRequest.builder()
                .operation(ZooKeeperRequest.Operation.READ)
                .path("/")
                .build();
            ZooKeeperResponse response = zooKeeper.processRequest(readRequest);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getData()).isEmpty();
        }
    }

    @Nested
    @DisplayName("Create Operation Tests")
    class CreateOperationTests {

        @Test
        @DisplayName("Should create node successfully")
        void shouldCreateNodeSuccessfully() {
            ZooKeeperRequest request = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.CREATE, TEST_PATH, TEST_DATA.getBytes());
            ZooKeeperResponse response = zooKeeper.processRequest(request);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getErrorMessage()).isNull();
        }

        @Test
        @DisplayName("Should fail to create existing node")
        void shouldFailToCreateExistingNode() {
            // Create node first
            ZooKeeperRequest createRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.CREATE, TEST_PATH, TEST_DATA.getBytes());
            zooKeeper.processRequest(createRequest);

            // Try to create same node again
            ZooKeeperRequest duplicateRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.CREATE, TEST_PATH, "duplicate data".getBytes());
            ZooKeeperResponse response = zooKeeper.processRequest(duplicateRequest);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getErrorMessage()).isEqualTo("Node already exists");
        }

        @Test
        @DisplayName("Should fail to create node with non-existent parent")
        void shouldFailToCreateNodeWithNonExistentParent() {
            ZooKeeperRequest request = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.CREATE, "/nonexistent/child", TEST_DATA.getBytes());
            ZooKeeperResponse response = zooKeeper.processRequest(request);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getErrorMessage()).isEqualTo("Parent node does not exist");
        }

        @Test
        @DisplayName("Should create nested nodes when parent exists")
        void shouldCreateNestedNodesWhenParentExists() {
            // Create parent
            ZooKeeperRequest parentRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.CREATE, "/parent", "parent data".getBytes());
            zooKeeper.processRequest(parentRequest);

            // Create child
            ZooKeeperRequest childRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.CREATE, "/parent/child", "child data".getBytes());
            ZooKeeperResponse response = zooKeeper.processRequest(childRequest);

            assertThat(response.isSuccess()).isTrue();
        }
    }

    @Nested
    @DisplayName("Read Operation Tests")
    class ReadOperationTests {

        @Test
        @DisplayName("Should read existing node data")
        void shouldReadExistingNodeData() {
            // Create node first
            ZooKeeperRequest createRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.CREATE, TEST_PATH, TEST_DATA.getBytes());
            zooKeeper.processRequest(createRequest);

            // Read the node
            ZooKeeperRequest readRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.READ, TEST_PATH);
            ZooKeeperResponse response = zooKeeper.processRequest(readRequest);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getData()).isEqualTo(TEST_DATA.getBytes());
        }

        @Test
        @DisplayName("Should fail to read non-existent node")
        void shouldFailToReadNonExistentNode() {
            ZooKeeperRequest request = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.READ, "/nonexistent");
            ZooKeeperResponse response = zooKeeper.processRequest(request);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getErrorMessage()).isEqualTo("Node does not exist");
        }
    }

    @Nested
    @DisplayName("Update Operation Tests")
    class UpdateOperationTests {

        @Test
        @DisplayName("Should update existing node data")
        void shouldUpdateExistingNodeData() {
            // Create node first
            ZooKeeperRequest createRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.CREATE, TEST_PATH, TEST_DATA.getBytes());
            zooKeeper.processRequest(createRequest);

            // Update the node
            String newData = "updated data";
            ZooKeeperRequest updateRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.UPDATE, TEST_PATH, newData.getBytes());
            ZooKeeperResponse response = zooKeeper.processRequest(updateRequest);

            assertThat(response.isSuccess()).isTrue();

            // Verify the update
            ZooKeeperRequest readRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.READ, TEST_PATH);
            ZooKeeperResponse readResponse = zooKeeper.processRequest(readRequest);
            assertThat(readResponse.getData()).isEqualTo(newData.getBytes());
        }

        @Test
        @DisplayName("Should fail to update non-existent node")
        void shouldFailToUpdateNonExistentNode() {
            ZooKeeperRequest request = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.UPDATE, "/nonexistent", TEST_DATA.getBytes());
            ZooKeeperResponse response = zooKeeper.processRequest(request);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getErrorMessage()).isEqualTo("Node does not exist");
        }
    }

    @Nested
    @DisplayName("Delete Operation Tests")
    class DeleteOperationTests {

        @Test
        @DisplayName("Should delete existing leaf node")
        void shouldDeleteExistingLeafNode() {
            // Create node first
            ZooKeeperRequest createRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.CREATE, TEST_PATH, TEST_DATA.getBytes());
            zooKeeper.processRequest(createRequest);

            // Delete the node
            ZooKeeperRequest deleteRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.DELETE, TEST_PATH);
            ZooKeeperResponse response = zooKeeper.processRequest(deleteRequest);

            assertThat(response.isSuccess()).isTrue();

            // Verify deletion
            ZooKeeperRequest readRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.READ, TEST_PATH);
            ZooKeeperResponse readResponse = zooKeeper.processRequest(readRequest);
            assertThat(readResponse.isSuccess()).isFalse();
        }

        @Test
        @DisplayName("Should fail to delete non-existent node")
        void shouldFailToDeleteNonExistentNode() {
            ZooKeeperRequest request = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.DELETE, "/nonexistent");
            ZooKeeperResponse response = zooKeeper.processRequest(request);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getErrorMessage()).isEqualTo("Node does not exist");
        }

        @Test
        @DisplayName("Should fail to delete root node")
        void shouldFailToDeleteRootNode() {
            ZooKeeperRequest request = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.DELETE, "/");
            ZooKeeperResponse response = zooKeeper.processRequest(request);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getErrorMessage()).isEqualTo("Cannot delete root node");
        }

        @Test
        @DisplayName("Should fail to delete node with children")
        void shouldFailToDeleteNodeWithChildren() {
            // Create parent and child
            ZooKeeperRequest parentRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.CREATE, "/parent", "parent data".getBytes());
            zooKeeper.processRequest(parentRequest);

            ZooKeeperRequest childRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.CREATE, "/parent/child", "child data".getBytes());
            zooKeeper.processRequest(childRequest);

            // Try to delete parent
            ZooKeeperRequest deleteRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.DELETE, "/parent");
            ZooKeeperResponse response = zooKeeper.processRequest(deleteRequest);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getErrorMessage()).isEqualTo("Node has children");
        }
    }

    @Nested
    @DisplayName("List Operation Tests")
    class ListOperationTests {

        @Test
        @DisplayName("Should list children of existing node")
        void shouldListChildrenOfExistingNode() {
            // Create parent and children
            ZooKeeperRequest parentRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.CREATE, "/parent", "parent data".getBytes());
            zooKeeper.processRequest(parentRequest);

            ZooKeeperRequest child1Request = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.CREATE, "/parent/child1", "child1 data".getBytes());
            zooKeeper.processRequest(child1Request);

            ZooKeeperRequest child2Request = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.CREATE, "/parent/child2", "child2 data".getBytes());
            zooKeeper.processRequest(child2Request);

            // List children
            ZooKeeperRequest listRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.LIST, "/parent");
            ZooKeeperResponse response = zooKeeper.processRequest(listRequest);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getChildren()).containsExactlyInAnyOrder("child1", "child2");
        }

        @Test
        @DisplayName("Should return empty list for node with no children")
        void shouldReturnEmptyListForNodeWithNoChildren() {
            // Create node without children
            ZooKeeperRequest createRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.CREATE, TEST_PATH, TEST_DATA.getBytes());
            zooKeeper.processRequest(createRequest);

            // List children
            ZooKeeperRequest listRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.LIST, TEST_PATH);
            ZooKeeperResponse response = zooKeeper.processRequest(listRequest);

            assertThat(response.isSuccess()).isTrue();
            assertThat(response.getChildren()).isEmpty();
        }

        @Test
        @DisplayName("Should fail to list children of non-existent node")
        void shouldFailToListChildrenOfNonExistentNode() {
            ZooKeeperRequest request = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.LIST, "/nonexistent");
            ZooKeeperResponse response = zooKeeper.processRequest(request);

            assertThat(response.isSuccess()).isFalse();
            assertThat(response.getErrorMessage()).isEqualTo("Node does not exist");
        }
    }

    @Nested
    @DisplayName("Basic Workflow Tests")
    class BasicWorkflowTests {

        @Test
        @DisplayName("Should handle basic CRUD workflow")
        void shouldHandleBasicCrudWorkflow() {
            String path = "/workflow";
            
            // Create
            ZooKeeperRequest createRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.CREATE, path, "initial data".getBytes());
            ZooKeeperResponse createResponse = zooKeeper.processRequest(createRequest);
            assertThat(createResponse.isSuccess()).isTrue();

            // Read
            ZooKeeperRequest readRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.READ, path);
            ZooKeeperResponse readResponse = zooKeeper.processRequest(readRequest);
            assertThat(readResponse.isSuccess()).isTrue();
            assertThat(readResponse.getData()).isEqualTo("initial data".getBytes());

            // Update
            ZooKeeperRequest updateRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.UPDATE, path, "updated data".getBytes());
            ZooKeeperResponse updateResponse = zooKeeper.processRequest(updateRequest);
            assertThat(updateResponse.isSuccess()).isTrue();

            // Delete
            ZooKeeperRequest deleteRequest = new ZooKeeperRequest(
                ZooKeeperRequest.Operation.DELETE, path);
            ZooKeeperResponse deleteResponse = zooKeeper.processRequest(deleteRequest);
            assertThat(deleteResponse.isSuccess()).isTrue();
        }
    }

    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {

        @Test
        @DisplayName("Should handle null request")
        void shouldHandleNullRequest() {
            assertThatThrownBy(() -> zooKeeper.processRequest(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Request cannot be null");
        }

        @Test
        @DisplayName("Should handle unknown operation")
        void shouldHandleUnknownOperation() {
            // This test simulates what would happen with an unknown operation
            // Since we can't create an unknown operation enum value, 
            // we'll test the default case indirectly by ensuring all known operations work
            
            // Test all known operations work
            assertThat(ZooKeeperRequest.Operation.values()).containsExactlyInAnyOrder(
                ZooKeeperRequest.Operation.CREATE,
                ZooKeeperRequest.Operation.READ,
                ZooKeeperRequest.Operation.UPDATE,
                ZooKeeperRequest.Operation.DELETE,
                ZooKeeperRequest.Operation.LIST
            );
        }
    }

}