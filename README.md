# FTPtrack

Client / Server for Live Track video file upload.

    * Client sends to the server the first listed file in a directory
    * Server receives video files from clients and updates the database

## Client Configuration

    *  **Required**  server IP
    *  **Required**  videoDir : path from the cwd to video files' directory

## Compilation & Execution

```shell
javac FTPServer.java
java FTPServer
javac FTPClient.java
java FTPClient
```
