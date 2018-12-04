package com.pinyougou.page.service.impl;

import java.io.Serializable;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.ObjectMessage;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pinyougou.page.service.ItemPageService;
@Component
public class PageDeleteListener implements MessageListener {

	@Autowired
	private ItemPageService pageService;
	
	@Override
	public void onMessage(Message message) {
		ObjectMessage objectMessage=(ObjectMessage) message;
		try {
			Long[] ids = (Long[]) objectMessage.getObject();
			pageService.DeleteHtml(ids);
		} catch (JMSException e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
		}
	}

}
