package com.uv;

/**
 * <uv> [2019/1/10 16:23]
 * java nio
 * key.isWritable()是表示Socket可写,网络不出现阻塞情况下,一直是可以写的,所认一直为true.一般不注册OP_WRITE事件.
 */

import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server {

    public static void main(String[] args) throws Exception {
        Selector selector = Selector.open();  //多路复用器，Selector 可以同时监控多个 SelectableChannel 的 IO 状况
        ServerSocketChannel socketChannel = ServerSocketChannel.open();
        socketChannel.socket().bind(new InetSocketAddress(8010)); //绑定监听端口
        socketChannel.configureBlocking(false); // 设置通道为非阻塞
        socketChannel.register(selector, SelectionKey.OP_ACCEPT); //将当前channel注册到selector，设置selector对channel感兴趣的事件
        while (true) {
            selector.select(); // 当注册的事件到达时，方法返回；否则,该方法会一直阻塞
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator(); //调用selectedKeys方法来返回已就绪通道的集合
            while (iterator.hasNext()) {
                SelectionKey key = iterator.next();
                iterator.remove(); // 删除已选的key,以防重复处理
                if(key.isAcceptable()) { // 客户端请求连接事件
                    ServerSocketChannel acceptServerSocketChannel = (ServerSocketChannel) key.channel();
                    SocketChannel channel = acceptServerSocketChannel.accept(); // 获得和客户端连接的通道
                    channel.configureBlocking(false); //设置成非阻塞
                    channel.register(selector, SelectionKey.OP_READ); //将连接到客户端的channel由selector监听读操作

                } else if(key.isReadable() && key.isValid()) { //读事件
                    SocketChannel channel = (SocketChannel) key.channel();
                    ByteBuffer buffer = ByteBuffer.allocate(1024); //设置缓存区
                    channel.read(buffer);
                    System.out.println("msg is " + new String(buffer.array()));
                    channel.write(ByteBuffer.wrap("check".getBytes())); //向客户端写数据
                }
            }
        }
    }
}
