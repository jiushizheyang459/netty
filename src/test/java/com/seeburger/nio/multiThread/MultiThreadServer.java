package com.seeburger.nio.multiThread;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.util.Iterator;

import static com.seeburger.bytebuffer.ByteBufferUtil.debugAll;

@Slf4j
public class MultiThreadServer {
    public static void main(String[] args) throws IOException {
        Thread.currentThread().setName("boss");
        ServerSocketChannel ssc = ServerSocketChannel.open();
        ssc.configureBlocking(false);
        Selector boss = Selector.open();
        SelectionKey bossKey = ssc.register(boss, 0, null);
        bossKey.interestOps(SelectionKey.OP_ACCEPT);
        ssc.bind(new InetSocketAddress(8080));

        // 创建固定数量的worker 并初始化
        Worker worker = new Worker("worker-0");
        worker.register();

        while (true) {
            boss.select();
            Iterator<SelectionKey> iter = boss.selectedKeys().iterator();
            while (iter.hasNext()) {
                SelectionKey key = iter.next();
                iter.remove();
                if (key.isAcceptable()) {
                    SocketChannel sc = ssc.accept();
                    sc.configureBlocking(false);
                    log.debug("connected...{}", sc.getRemoteAddress());
                    // 关联selector
                    log.debug("before register...{}", sc.getRemoteAddress());
                    sc.register(worker.selector, SelectionKey.OP_READ);
                    log.debug("after register...{}", sc.getRemoteAddress());

                }
            }
        }
    }

    static class Worker implements  Runnable {
        private Thread thread;
        private Selector selector;
        private String Name;
        private boolean strat = false; // 还未初始化

        public Worker(String name) {
            Name = name;
        }

        // 初始化线程和selector
        public void register() throws IOException {
            if (!strat) {
                thread = new Thread(this, Name);
                thread.start();
                selector = Selector.open();
                strat = true;
            }

        }

        @Override
        public void run() {
            while (true) {
                try {
                    selector.select();
                    Iterator<SelectionKey> iter = selector.selectedKeys().iterator();
                    while (iter.hasNext()) {
                        SelectionKey key = iter.next();
                        iter.remove();
                        if (key.isReadable()) {
                            ByteBuffer buffer = ByteBuffer.allocate(16);
                            SocketChannel channel = (SocketChannel) key.channel();
                            log.debug("read...{}", channel.getRemoteAddress());
                            channel.read(buffer);
                            buffer.flip();
                            debugAll(buffer);
                        }
                    }
                } catch (IOException e) {
//                    throw new RuntimeException(e);
                    e.printStackTrace();
                }
            }
        }
    }
}
