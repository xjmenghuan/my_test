package netty;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangxj
 * @version 2.0
 * @Description:
 * @date 2021/1/11
 */
public class ChatServerHandler extends ChannelInboundHandlerAdapter {
    private static List<ChannelHandlerContext> chatGroup =  new ArrayList<>();

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception{
        String name = ctx.channel().remoteAddress().toString();
        System.out.println(name + "上线了.........");
        chatGroup.stream().forEach(item -> {
            item.writeAndFlush(name+ "上线了.........");
        });
        chatGroup.add(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception{
        String line = String.format("%s发送了消息:%s", ctx.channel().remoteAddress(), msg);
        String self = String.format("您发送了消息:%s", msg);
        chatGroup.stream().forEach(item -> {
            if(item.equals(ctx)){
                item.writeAndFlush(self);
            }else{
                item.writeAndFlush(line);
            }
        });
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        chatGroup.remove(ctx);
        chatGroup.stream().forEach(item -> {
            item.writeAndFlush(String.format("%s下线了",ctx.channel().remoteAddress()));
        });
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        chatGroup.remove(ctx);
        chatGroup.stream().forEach(item -> {
            item.writeAndFlush(String.format("%s下线了",ctx.channel().remoteAddress()));
        });
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        System.out.println(ctx.channel().remoteAddress()+"下线了");
        ctx.close();
    }
}
