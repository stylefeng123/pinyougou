package com.pinyougou.solrutil;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.jdbc.Null;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbItemExample;
import com.pinyougou.pojo.TbItemExample.Criteria;

@Component
public class SolrUtil {
	@Autowired
	private TbItemMapper tbItemMapper;
	
	@Autowired
	private SolrTemplate solrTemplate;
	
	
	public void importItemData() {
		TbItemExample example= new TbItemExample();
		Criteria criteria = example.createCriteria();
		criteria.andStatusEqualTo("1");
		List<TbItem> itemList = tbItemMapper.selectByExample(example);
		System.out.println("开始");
		for (TbItem tbItem : itemList) {
			System.out.println(tbItem.getTitle());
			String spec = tbItem.getSpec();
			Map specMap = JSON.parseObject(spec);
			tbItem.setSpecMap(specMap);
		}
		solrTemplate.saveBeans(itemList);
		solrTemplate.commit();
		System.out.println("结束");
	}
	
	public static void main(String[] args) {
		ApplicationContext ac = new ClassPathXmlApplicationContext("classpath*:spring/applicationContext*.xml");
		SolrUtil  solrUtil = (SolrUtil) ac.getBean("solrUtil");
		solrUtil.importItemData();
	}
}
