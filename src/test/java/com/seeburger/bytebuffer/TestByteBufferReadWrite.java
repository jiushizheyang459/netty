package com.seeburger.bytebuffer;

import java.nio.ByteBuffer;

import static com.seeburger.bytebuffer.ByteBufferUtil.debugAll;

public class TestByteBufferReadWrite {
    public static void main(String[] args) {
        ByteBuffer buffer = ByteBuffer.allocate(10);

        buffer.put((byte) 0x61);// a
//        debugAll(buffer);
        buffer.put(new byte[]{(byte) 0x62, (byte) 0x63, (byte) 0x64});
//        debugAll(buffer);

//        System.out.println(buffer.get());

        buffer.flip();
        System.out.println(buffer.get());
        debugAll(buffer);

        buffer.compact();
        debugAll(buffer);

        buffer.put(new byte[]{(byte) 0x65, (byte) 0x6f});
        debugAll(buffer);
    }
}
