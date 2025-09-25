package com.seeburger.nio.selector;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

public class Client {
    public static void main(String[] args) throws IOException {
//        client1();
        client2();
    }

    /**
     * 处理消息边界的测试客户端
     * @throws IOException
     */
    private static void client2() throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));
        SocketAddress address = sc.getLocalAddress();
//        sc.write(Charset.defaultCharset().encode("hello\nworld\n"));
        sc.write(Charset.defaultCharset().encode("0123\n456789abcdef"));
        sc.write(Charset.defaultCharset().encode("0123456789abcdef3333\n"));
        System.in.read();
    }

    private static void client1() throws IOException {
        SocketChannel sc = SocketChannel.open();
        sc.connect(new InetSocketAddress("localhost", 8080));
        System.out.println("waiting...");
    }
}
