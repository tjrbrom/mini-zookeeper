package com.minizk;

import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Scanner;

/**
 * Interactive command-line server for Mini ZooKeeper.
 * Commands: create, get, set, delete, ls, quit
 */
@Slf4j
public final class MiniZooKeeperServer {
    
    public static void main(String[] args) {
        log.info("Starting Mini ZooKeeper Server...");
        System.out.println("Starting Mini ZooKeeper Server...");
        
        try {
            MiniZooKeeper zooKeeper = new MiniZooKeeper();
            MiniZooKeeperClient client = new MiniZooKeeperClient(zooKeeper);
            
            runInteractiveSession(client);
        } catch (Exception e) {
            log.error("Failed to start Mini ZooKeeper Server", e);
            System.err.println("Failed to start server: " + e.getMessage());
            System.exit(1);
        }
    }
    
    private static void runInteractiveSession(MiniZooKeeperClient client) {
        System.out.println("Mini ZooKeeper is ready!");
        printUsage();
        
        try (Scanner scanner = new Scanner(System.in)) {
            processCommands(scanner, client);
        }
        
        System.out.println("Mini ZooKeeper Server stopped.");
        log.info("Mini ZooKeeper Server stopped");
    }
    
    private static void printUsage() {
        System.out.println("Available commands:");
        System.out.println("  create <path> <data> - Create a new znode");
        System.out.println("  get <path> - Get data from znode");
        System.out.println("  set <path> <data> - Set data in znode");
        System.out.println("  delete <path> - Delete znode");
        System.out.println("  ls <path> - List children of znode");
        System.out.println("  help - Show this help message");
        System.out.println("  quit - Exit the server");
    }
    
    private static void processCommands(Scanner scanner, MiniZooKeeperClient client) {
        while (true) {
            System.out.print("minizk> ");
            String input = scanner.nextLine().trim();
            
            if (input.isEmpty()) {
                continue;
            }
            
            if (input.equals("quit")) {
                break;
            }
            
            String[] parts = input.split("\\s+", 3);
            String command = parts[0].toLowerCase();
            
            try {
                executeCommand(command, parts, client);
            } catch (Exception e) {
                System.out.println("Error executing command: " + e.getMessage());
                log.error("Error executing command: {}", input, e);
            }
        }
    }
    
    private static void executeCommand(String command, String[] parts, MiniZooKeeperClient client) {
        switch (command) {
            case "help":
                printUsage();
                break;
            case "create":
                executeCreateCommand(parts, client);
                break;
            case "get":
                executeGetCommand(parts, client);
                break;
            case "set":
                executeSetCommand(parts, client);
                break;
            case "delete":
                executeDeleteCommand(parts, client);
                break;
            case "ls":
                executeListCommand(parts, client);
                break;
            default:
                System.out.println("Unknown command: " + command);
                System.out.println("Type 'help' for available commands.");
                break;
        }
    }

    private static void executeCreateCommand(String[] parts, MiniZooKeeperClient client) {
        if (!validateArguments(parts, 3, "create <path> <data>")) {
            return;
        }
        boolean created = client.create(parts[1], parts[2]);
        System.out.println(created ? "Created successfully" : "Create failed");
    }

    private static void executeGetCommand(String[] parts, MiniZooKeeperClient client) {
        if (!validateArguments(parts, 2, "get <path>")) {
            return;
        }
        String data = client.getData(parts[1]);
        if (data != null) {
            System.out.println("Data: " + data);
        }
    }

    private static void executeSetCommand(String[] parts, MiniZooKeeperClient client) {
        if (!validateArguments(parts, 3, "set <path> <data>")) {
            return;
        }
        boolean updated = client.setData(parts[1], parts[2]);
        System.out.println(updated ? "Updated successfully" : "Update failed");
    }

    private static void executeDeleteCommand(String[] parts, MiniZooKeeperClient client) {
        if (!validateArguments(parts, 2, "delete <path>")) {
            return;
        }
        boolean deleted = client.delete(parts[1]);
        System.out.println(deleted ? "Deleted successfully" : "Delete failed");
    }

    private static void executeListCommand(String[] parts, MiniZooKeeperClient client) {
        if (!validateArguments(parts, 2, "ls <path>")) {
            return;
        }
        List<String> children = client.getChildren(parts[1]);
        if (children != null) {
            if (children.isEmpty()) {
                System.out.println("No children");
            } else {
                System.out.println("Children: " + children);
            }
        }
    }

    private static boolean validateArguments(String[] parts, int requiredLength, String usage) {
        if (parts.length < requiredLength) {
            System.out.println("Usage: " + usage);
            return false;
        }
        return true;
    }
}