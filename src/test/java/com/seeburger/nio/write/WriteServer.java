package com.seeburger.nio.write;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.Charset;
import java.util.Iterator;

public class WriteServer {
    public static void main(String[] args) throws IOException {
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);

        Selector selector = Selector.open();
        ssc.register(selector, SelectionKey.OP_ACCEPT);

        ssc.bind(new InetSocketAddress(8080));

        while (true) {
            selector.select();

            Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    SelectionKey sckey = sc.register(selector, 0, null);

                    // 像客户端发送大量数据
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < 100000000; i++) {
                        sb.append("a");
                    }
                    ByteBuffer buffer = Charset.defaultCharset().encode(sb.toString());

                    // write()方法可能不会一次写完，返回值代表实际写入的字节数
                    int write = sc.write(buffer);
                    System.out.println(write);

                    // 是否有剩余内容
                    if (buffer.hasRemaining()) {
                        // 关注可写事件
                        sckey.interestOps(sckey.interestOps() | SelectionKey.OP_WRITE);
                        // 把未写完的数据挂到sckey上
                        sckey.attach(buffer);
                    }
                } else if (key.isWritable()) {
                    ByteBuffer buffer = (ByteBuffer) key.attachment();
                    SocketChannel sc = (SocketChannel) key.channel();

                    int write = sc.write(buffer);
                    System.out.println(write);

                    if (!buffer.hasRemaining()) {
                        // 清除buffer
                        key.attach(null);
                        // 不需再关注可写事件
                        key.interestOps(key.interestOps() & ~SelectionKey.OP_WRITE);
                    }
                }
            }
        }
    }
}
