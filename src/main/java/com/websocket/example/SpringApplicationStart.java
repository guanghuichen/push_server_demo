package com.websocket.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.websocket.example.netty.NettyServerConfig;

@SpringBootApplication
public class SpringApplicationStart {
	public static void main(String[] args) throws Exception {
		SpringApplication.run(SpringApplicationStart.class);
		new NettyServerConfig(7777).start();
	}
}
