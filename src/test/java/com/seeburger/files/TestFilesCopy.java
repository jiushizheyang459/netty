package com.seeburger.files;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TestFilesCopy {
    public static void main(String[] args) throws IOException {
        String source = "/Users/helewei/Music/陶喆/爱，很简单 - 陶喆.mp3";
        String target = "/Users/helewei/Downloads/music/love is simple.mp3";

        Files.walk(Paths.get(source)).forEach(path -> {
            try {
                String targerName = path.toString().replace(source, target);
                // 是目录
                if (Files.isDirectory(path)) {
                    Files.createDirectory(Paths.get(targerName));
                }
                // 是普通文件
                else if (Files.isRegularFile(path)) {
                    Files.copy(path, Paths.get(targerName));
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
