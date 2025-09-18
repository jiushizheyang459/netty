package com.seeburger.bytebuffer;

import lombok.extern.slf4j.Slf4j;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

@Slf4j
public class TestByteBuffer {
    public static void main(String[] args) {
        // FileChannel
        try (FileChannel channel = new FileInputStream("data.txt").getChannel()) {
            // 准备缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(10);
            while (true) {
                // 从 channel 读取数据，向 buffer 写入
                int len = channel.read(buffer);
                log.debug("读取到的字节数 {}", len);
                if (len == -1) {
                    break;
                }

                // 打印 buffer 的内容
                buffer.flip(); // 切换到读模式
                while (buffer.hasRemaining()) { // 是否还有剩余未读数据
                    byte b = buffer.get();
                    System.out.println((char) b);
                    log.debug("实际字节 {}", (char) b);
                }
                buffer.clear(); // 重置缓冲区，准备接收新的数据
            }
        } catch (IOException e) {
        }
    }
}
