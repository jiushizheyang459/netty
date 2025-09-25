package com.seeburger.nio.blocking;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.seeburger.bytebuffer.ByteBufferUtil.debugAll;
import static com.seeburger.bytebuffer.ByteBufferUtil.debugRead;

@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        unblocking();
//        blocking();
    }

    //region 阻塞模式
    private static void blocking() throws IOException {
        // 使用nio理解阻塞模式

        //ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();

        // 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));

        // 连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            // accept建立与客户端连接，SocketChannel用来与客户端之间通信（读写操作）
            log.debug("connecting...");
            SocketChannel sc = ssc.accept(); // 阻塞方法，线程停止运行
            log.debug("connected... {} ", sc);
            channels.add(sc);
            for (SocketChannel channel : channels) {
                // 接收客户端发送的数据
                log.debug("before read... {} ", channel);
                channel.read(buffer); // 阻塞方法，线程停止运行
                buffer.flip();
                debugRead(buffer);
                buffer.clear();
                log.debug("after read... {} ", channel);
            }
        }
    }

    private static void unblocking() throws IOException {
        // 使用nio理解非阻塞模式

        //ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false); // 非阻塞模式

        // 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));

        // 连接集合
        List<SocketChannel> channels = new ArrayList<>();
        while (true) {
            // accept建立与客户端连接，SocketChannel用来与客户端之间通信
            SocketChannel sc = ssc.accept(); // 非阻塞模式，线程还会继续运行，但sc返回值为null
            if (sc != null) {
                log.debug("connected... {} ", sc);
                sc.configureBlocking(false); // 将SocketChannel设为非阻塞模式
                channels.add(sc);
            }
            for (SocketChannel channel : channels) {
                // 接收客户端发送的数据
                int read = channel.read(buffer);// 非阻塞方法，线程仍然运行，如果没有读到数据，read 返回 0
                if (read > 0) {
                    buffer.flip();
                    debugRead(buffer);
                    buffer.clear();
                    log.debug("after read... {} ", channel);
                }
            }
        }
    }
    //endregion

}
