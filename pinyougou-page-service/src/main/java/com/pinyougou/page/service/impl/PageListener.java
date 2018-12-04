package com.pinyougou.page.service.impl;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;
@Component
public class PageListener implements MessageListener {

	@Autowired
	private ItemPageService pageService;
	@Override
	public void onMessage(Message message) {
		TextMessage textMessage=(TextMessage)message;
		try {
			String id = textMessage.getText();
			pageService.genItemHtml(Long.parseLong(id));
		} catch (JMSException e) {
			e.printStackTrace();
		}
	}
}
