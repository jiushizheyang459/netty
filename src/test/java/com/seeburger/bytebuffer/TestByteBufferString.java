package com.seeburger.bytebuffer;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import static com.seeburger.bytebuffer.ByteBufferUtil.debugAll;

/**
 * 1在转换为字符串之前必须先.flip()切换为读模式，否则position仍然是0
 * 2，3两种方法会自动切换为读模式
 */
public class TestByteBufferString {
    public static void main(String[] args) {
        // 1.字符串转为ByteBuffer
        /*ByteBuffer buffer1 = ByteBuffer.allocate(16);
        buffer1.put("hello".getBytes());
        debugAll(buffer1);*/

        // 2.Charset
        // Charset.defaultCharset()
        /*ByteBuffer buffer2 = StandardCharsets.UTF_8.encode("hello");
        debugAll(buffer2);*/

        // 3.wrap
        ByteBuffer buffer3 = ByteBuffer.wrap("hello".getBytes());
        debugAll(buffer3);

        // 转为字符串
        String str1 = StandardCharsets.UTF_8.decode(buffer3).toString();
        System.out.println(str1);
    }
}
