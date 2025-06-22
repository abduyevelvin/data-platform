package data.platform.client;

import data.platform.client.sdk.DriveSdk;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class DriveClientApplication {

    public static void main(String[] args) {
        var sdk = new DriveSdk("http://localhost:8080/api/v1");

        if (args.length < 1) {
            System.out.println("Usage: list | upload --src <path> --dst <path>");
            return;
        }

        switch (args[0]) {
            case "list" -> sdk.listFiles()
                              .forEach(System.out::println);
            case "upload" -> {
                String src = null, dst = null;
                for (int i = 1; i < args.length; i++) {
                    if ("--src".equals(args[i]) && i + 1 < args.length) {
                        src = args[++i];
                    } else if ("--dst".equals(args[i]) && i + 1 < args.length) {
                        dst = args[++i];
                    }
                }
                if (src == null || dst == null) {
                    System.out.println("Missing --src or --dst arguments.");
                    return;
                }
                System.out.println(sdk.uploadFile(src, dst));
            }
            default -> System.out.println("Unknown command: " + args[0]);
        }
    }
}
