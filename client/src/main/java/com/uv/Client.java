package com.uv;
/**
 * <liuwei> [2019/1/11 10:19]
 * java nio
 * key.isWritable()是表示Socket可写,网络不出现阻塞情况下,一直是可以写的,所认一直为true.一般不注册OP_WRITE事件.
 */
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Client {

    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();
        SocketChannel socketChannel = SocketChannel.open();
        socketChannel.configureBlocking(false);
        socketChannel.connect(new InetSocketAddress("127.0.0.1", 8010));
        socketChannel.register(selector, SelectionKey.OP_CONNECT);
        while (true) {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            if(iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove();
                if(key.isConnectable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    if(channel.isConnectionPending()) { //如果正在连接，则完成连接
                        channel.finishConnect();
                    }
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    channel.configureBlocking(false);
                    channel.write(ByteBuffer.wrap(dateFormat.format(new Date()).getBytes("utf-8")));
                    channel.register(selector, SelectionKey.OP_READ);
                } else if(key.isReadable()) {
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024);
                    channel.read(buffer);
                    System.out.println(new String(buffer.array()));
                }
            }

        }
    }
}
