package com.websocket.example.netty;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import java.util.Date;

import com.websocket.example.utils.Utils;

public class WebSocketHandler extends SimpleChannelInboundHandler<TextWebSocketFrame> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        Utils.log("与客户端建立连接，通道开启！");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        if (!ChannelHandlerMap.biDirectionHashMap.containsValue(channel)) {
            Utils.log("该客户端未注册");
            return;
        }
        Long id = ChannelHandlerMap.biDirectionHashMap.getByValue(channel);
        Utils.log("客户端断开连接 id -> " + id);
        ChannelHandlerMap.biDirectionHashMap.removeByValue(channel);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        super.channelRead(ctx, msg);
    }

    /**
     * 刷新最后一次通信时间
     * @param channel 通道
     */
    private void freshTime (Channel channel) {
        if (ChannelHandlerMap.biDirectionHashMap.containsValue(channel)) {
            Utils.log("update time");
            long id = ChannelHandlerMap.biDirectionHashMap.getByValue(channel);
            ChannelHandlerMap.lastUpdate.put(id, new Date());
        }
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, TextWebSocketFrame textWebSocketFrame) throws Exception {
        Channel channel = ctx.channel();
        freshTime(channel);

        Utils.log("read0: " + textWebSocketFrame.text());
        String text = textWebSocketFrame.text();

        // 收到生成ID的指令, 返回 id:xxxxxxxx
        if (text.equals("getID")) {
            // 已建立连接, 则返回已有ID
            if (ChannelHandlerMap.biDirectionHashMap.containsValue(channel)) {
                Long id = ChannelHandlerMap.biDirectionHashMap.getByValue(channel);
                channel.writeAndFlush(new TextWebSocketFrame("id:" + id));
                return;
            }
            Long id = Utils.generateID();  // 创建ID
            Utils.log("id ->  " + id);
            channel.writeAndFlush(new TextWebSocketFrame("id:" + id));
            ChannelHandlerMap.biDirectionHashMap.put(id, ctx.channel());
            ChannelHandlerMap.lastUpdate.put(id, new Date());
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Utils.log("异常，断开");
        ctx.close();
    }
}
