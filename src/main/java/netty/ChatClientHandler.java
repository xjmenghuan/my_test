package netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import java.util.Scanner;
import java.util.concurrent.*;

/**
 * @author wangxj
 * @version 2.0
 * @Description:
 * @date 2021/11
 */
public class ChatClientHandler extends ChannelInboundHandlerAdapter {
    private static final ThreadPoolExecutor threadPool = new ThreadPoolExecutor(10,100,0, TimeUnit.MILLISECONDS,new LinkedBlockingDeque<>(), Executors.defaultThreadFactory(),new ThreadPoolExecutor.AbortPolicy());

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("服务器连接成功");
        threadPool.execute(()->{
            while (true){
                Scanner scanner = new Scanner(System.in);
                String input = scanner.nextLine();
                ctx.writeAndFlush(input);
            }
        });
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        System.out.println(msg);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("挂了-----------------");
        System.out.println("又去连了-----------------");
        NioEventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception{
                        ch.pipeline().addLast(new StringEncoder());
                        ch.pipeline().addLast(new StringDecoder());
                        ch.pipeline().addLast(new ChatClientHandler());
                    }
                });
        System.out.println("netty client start.....");
        ChannelFuture future = bootstrap.connect("127.0.0.1", 9000).sync();
        future.channel().closeFuture().sync();
    }
}
