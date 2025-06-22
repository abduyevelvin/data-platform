# Drive Client

A simple Java Spring Boot client for interacting with a remote Drive Service API.  
Supports listing files and uploading (moving) files on the drive.

## Features

- List files on the remote drive
- Upload (move) files to the remote drive

## Requirements

- Java 21
- Maven

## Build

To build the executable JAR, run:

```sh
mvn clean package
```

## Run - List Files

To run the application and list files, execute:

```sh
java -jar target/drive-client-0.0.1-SNAPSHOT.jar list
```

## Run - Upload File

To run the application and upload a file, execute:

```sh
java -jar target/drive-client-0.0.1-SNAPSHOT.jar upload --src /local/path/file.txt --dst /remote/path/file.txt
```

## Configuration

The Drive Service API base URL is set in DriveClientApplication.java (default: http://localhost:8080/api/v1).
Update the base URL in the code if your Drive Service runs elsewhere.