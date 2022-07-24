package com.example.eventapp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import com.slack.api.Slack;

@Component
public class PostSlackMessage {
	
	@Autowired
	private Environment environment;
	
	public void postMessage(String eventName, String storeName) throws Exception {
		
		Slack slack = Slack.getInstance();
		
		slack.methods(environment.getProperty("slack.token")).chatPostMessage(req -> req
			.channel(environment.getProperty("slack.channelId"))
			.text(eventName + "の整理券発行枚数が上限に達しました。")
			.username(storeName));
		
	}
	
}
