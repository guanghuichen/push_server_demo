package com.websocket.example.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.websocket.example.common.Response;
import com.websocket.example.netty.ChannelHandlerMap;
import com.websocket.example.utils.Utils;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;

@RestController
@RequestMapping("/send")
public class TestController {
	@PostMapping("/{id}")
	public ResponseEntity send(@PathVariable(value = "id", required = true) Long id,
			@RequestParam(value = "data", required = true) String data) {
		if (!ChannelHandlerMap.biDirectionHashMap.containsKey(id)) {
			Utils.log("该ID未注册");
			return Response.notFound();
		}
		Channel channel = ChannelHandlerMap.biDirectionHashMap.getByKey(id);
		channel.writeAndFlush(new TextWebSocketFrame(data));
		Utils.log("向该ID发送消息:" + data);
		return Response.success();
	}
	
	@RequestMapping(value = "/htts",method = RequestMethod.GET)
    @ResponseBody
	public ResponseEntity inspectionTransferData(){
		System.out.println("textetetxtx");
		return Response.success();
	}
}
