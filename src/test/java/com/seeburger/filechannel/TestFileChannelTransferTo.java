package com.seeburger.filechannel;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

public class TestFileChannelTransferTo {
    public static void main(String[] args) {
        try (FileChannel from = new FileInputStream("data.txt").getChannel();
             FileChannel to = new FileOutputStream("to.txt").getChannel();
        ) {
            // 比使用输入输出流效率更高，底层会使用操作系统的零拷贝进行优化
            long size = from.size();
            for (long left = size; left > 0;) {
                System.out.println("position:" + (size - left) + " left:" + left);
                left -= from.transferTo(size - left, size, to);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
