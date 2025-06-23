# Mini ZooKeeper

A minimal Apache ZooKeeper-like coordination service implementation in Java.

## Features

- **Hierarchical Namespace**: Tree-like structure with znodes
- **CRUD Operations**: Create, Read, Update, Delete, List operations
- **Thread-Safe**: Synchronized operations for concurrent access
- **Versioning**: Each znode tracks version and timestamps
- **Interactive CLI**: Command-line interface for testing

## Quick Start

```bash
# Compile and run
mvn compile
mvn exec:java

# Run tests
mvn test
```

## Usage

### Commands
- `create <path> <data>` - Create a new znode
- `get <path>` - Get znode data
- `set <path> <data>` - Update znode data
- `delete <path>` - Delete znode (no children)
- `ls <path>` - List children
- `help` - Show commands
- `quit` - Exit

### Example Session
```
minizk> create /app "config data"
Created successfully

minizk> create /app/web "web config"
Created successfully

minizk> ls /app
Children: [web]

minizk> get /app/web
Data: web config
```

### Programmatic API
```java
MiniZooKeeper zk = new MiniZooKeeper();
MiniZooKeeperClient client = new MiniZooKeeperClient(zk);

client.create("/config", "data");
String data = client.getData("/config");
List<String> children = client.getChildren("/");
```

## Architecture

- **ZNode**: Hierarchical data nodes
- **MiniZooKeeper**: Core service implementation
- **MiniZooKeeperClient**: Client API
- **MiniZooKeeperServer**: Interactive CLI

## Limitations

- In-memory only (no persistence)
- Single-process (no network)
- No authentication or ACLs
- No watches or ephemeral nodes

## Testing

57 tests covering core functionality, error handling, and integration workflows.

## Requirements

- Java 11+
- Maven 3.6+
- 
## License

MIT License - see [LICENSE](LICENSE) file for details.
