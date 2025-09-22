package com.seeburger.nio.blocking;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.ArrayList;
import java.util.Iterator;

import static com.seeburger.bytebuffer.ByteBufferUtil.debugRead;

@Slf4j
public class Server {
    public static void main(String[] args) throws IOException {
        blocking();
//        selector();
    }

    private static void selector() throws IOException {
        // 创建 selector，管理多个channel
        Selector selector = Selector.open();
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        // 建立 selector 和 channel 的联系（注册）
        // SelectionKey 就是将来事件发生后，通过他可以知道事件和哪个channel的事件
        SelectionKey sscKey = ssc.register(selector, 0, null);
        // 指明这个 key 只关注accept事件
        sscKey.interestOps(SelectionKey.OP_ACCEPT);
        log.debug("register key: {}", sscKey);

        ssc.bind(new InetSocketAddress(8080));
        while (true) {
            // 调用 selector 的 select 方法，没有事件发生，线程阻塞，有事件，线程才会恢复运行
            selector.select();

            // 处理事件，selectedKeys 内部包含了所有发生的事件
            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                log.debug("key: {}", key);
                ServerSocketChannel channel = (ServerSocketChannel) key.channel();
                SocketChannel sc = channel.accept();
                log.debug("accept key: {}", sc);
            }
        }
    }

    private static void blocking() throws IOException {
        // 使用nio理解阻塞模式

        //ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();

        // 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));

        // 连接集合
        ArrayList<SocketChannel> channels = new ArrayList<>();
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
        // 使用nio理解阻塞模式

        //ByteBuffer
        ByteBuffer buffer = ByteBuffer.allocate(16);
        // 创建服务器
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false); // 非阻塞模式，线程还会继续运行

        // 绑定监听端口
        ssc.bind(new InetSocketAddress(8080));

        // 连接集合
        ArrayList<SocketChannel> channels = new ArrayList<>();
        while (true) {
            // accept建立与客户端连接，SocketChannel用来与客户端之间通信
//            log.debug("connecting...");
            SocketChannel sc = ssc.accept(); // 阻塞方法，线程停止运行
            if (sc != null) {
                log.debug("connected... {} ", sc);
                sc.configureBlocking(false); // 将SocketChannel设为非阻塞模式
                channels.add(sc);
            }
            for (SocketChannel channel : channels) {
                // 接收客户端发送的数据
//                log.debug("before read... {} ", channel);
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

}
