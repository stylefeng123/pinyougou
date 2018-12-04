package com.pinyougou.search.service.impl;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.Criteria;
import org.springframework.data.solr.core.query.FilterQuery;
import org.springframework.data.solr.core.query.GroupOptions;
import org.springframework.data.solr.core.query.HighlightOptions;
import org.springframework.data.solr.core.query.HighlightQuery;
import org.springframework.data.solr.core.query.Query;
import org.springframework.data.solr.core.query.SimpleFilterQuery;
import org.springframework.data.solr.core.query.SimpleHighlightQuery;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.core.query.SolrDataQuery;
import org.springframework.data.solr.core.query.result.GroupEntry;
import org.springframework.data.solr.core.query.result.GroupPage;
import org.springframework.data.solr.core.query.result.GroupResult;
import org.springframework.data.solr.core.query.result.HighlightEntry;
import org.springframework.data.solr.core.query.result.HighlightPage;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.search.service.ItemSearchService;


@Service(timeout=500000)
public class ItemSearchServiceImpl implements ItemSearchService{

	
	@Autowired
	private SolrTemplate solrTemplate;
	
	@Override
	public Map search(Map searchMap) {
		Map map = new HashMap();
		map.putAll(searchMaps(searchMap));
		List<String> categoryList = searchCategoryList(searchMap);
		map.put("categoryList",categoryList);
		
		//查询品牌和规格列表
		String categoryName = (String) searchMap.get("category");
		if(!"".equals(categoryName)) {
			map.putAll(searchBrandAndSpecList(categoryName));
		}else {
			if (categoryList.size()>0) {
				map.putAll(searchBrandAndSpecList(categoryList.get(0)));
			}
		}
		return map;
	}

	//高亮查询
	private Map searchMaps(Map searchMap) {
		Map map = new HashMap();
		String keywords = (String) searchMap.get("keywords");
		searchMap.put("keywords", keywords.replace(" ", ""));
		//高亮设置
		HighlightQuery query= new SimpleHighlightQuery();
		HighlightOptions highlightOptions = new HighlightOptions();
		highlightOptions.addField("item_title");
		highlightOptions.setSimplePrefix("<em style='color:red'>");
		highlightOptions.setSimplePostfix("</em>");
		query.setHighlightOptions(highlightOptions);
		
		//关键字查询
		Criteria criteria=new Criteria("item_keywords").is(keywords);
		query.addCriteria(criteria);
		
		//过滤查询
		//分类过滤
		if(!"".equals(searchMap.get("category"))) {
			Criteria filterCriteria=new Criteria("item_category").is(searchMap.get("category"));
			FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		//品牌过滤
		if(!"".equals(searchMap.get("brand"))) {
			Criteria filterCriteria=new Criteria("item_brand").is(searchMap.get("brand"));
			FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
			query.addFilterQuery(filterQuery);
		}
		
		//规格过滤
		if (searchMap.get("spec")!=null) {
			
			Map<String,String> specMap= (Map) searchMap.get("spec");
			for (String  key : specMap.keySet()) {
				Criteria filterCriteria=new Criteria("item_spec_"+key).is(specMap.get(key));
				FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
			
		}
		
		//价格过滤
		if (!"".equals(searchMap.get("price"))) {
			String priceStr = (String) searchMap.get("price");
			String[] price = priceStr.split("-");
			if (!"0".equals(price[0])) {
				Criteria filterCriteria = new Criteria("item_price").greaterThanEqual(price[0]);
				FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
			if (!"*".equals(price[1])) {
				Criteria filterCriteria = new Criteria("item_price").lessThanEqual(price[1]);
				FilterQuery filterQuery=new SimpleFilterQuery(filterCriteria);
				query.addFilterQuery(filterQuery);
			}
			
		}
		
		//分页查询
		Integer pageNo=(Integer) searchMap.get("pageNo");
		if (pageNo==null) {
			pageNo=1;
		}
		
		Integer pageSize=(Integer) searchMap.get("pageSize");
		if (pageSize==null) {
			pageSize=20;
		}
		
		query.setOffset((pageNo-1)*pageSize);
		query.setRows(pageSize);
		
		
		//排序
		String sortValue = (String) searchMap.get("sort");
		String sortField = (String) searchMap.get("sortField");
		if (sortValue!=null && !"".equals(sortValue)) {
			if ("ASC".equals(sortValue)) {
				Sort sort= new Sort(Sort.Direction.ASC, "item_"+sortField);
				query.addSort(sort);
			}else if ("DESC".equals(sortValue)) {
				Sort sort= new Sort(Sort.Direction.DESC, "item_"+sortField);
				query.addSort(sort);
			}
		}
		
		
		//获取结果集
		HighlightPage<TbItem> page = solrTemplate.queryForHighlightPage(query,TbItem.class);//原生的
		List<HighlightEntry<TbItem>> highlighted = page.getHighlighted();	//高亮的item集合
		for (HighlightEntry<TbItem> entry : highlighted) {			//遍历单个item
			TbItem entity = entry.getEntity();	//原生的item
			if(entry.getHighlights().size()>0 && entry.getHighlights().get(0).getSnipplets().size()>0) {
			//高亮域的集合 一个item有多个域              	 每个域可能有多个高亮值
				entity.setTitle(entry.getHighlights().get(0).getSnipplets().get(0));
			}
		}
		map.put("rows", page.getContent());
		map.put("totalPages", page.getTotalPages());
		map.put("total", page.getTotalElements());
		return map;
	}
	//查询分类列表
	private List searchCategoryList(Map searchMap) {
		List<String> list=new ArrayList<>();
		Query query=new SimpleQuery();
		Criteria criteria =new Criteria("item_keywords").is(searchMap.get("keywords"));
		query.addCriteria(criteria);
		
		GroupOptions groupOptions= new GroupOptions().addGroupByField("item_category");
		query.setGroupOptions(groupOptions);
		
		GroupPage<TbItem> page = solrTemplate.queryForGroupPage(query, TbItem.class);
		GroupResult<TbItem> groupResult = page.getGroupResult("item_category");
		Page<GroupEntry<TbItem>> groupEntries = groupResult.getGroupEntries();
		List<GroupEntry<TbItem>> content = groupEntries.getContent();
		for (GroupEntry<TbItem> groupEntry : content) {
			list.add(groupEntry.getGroupValue());
		}
		return list;
	}
	
	@Autowired
	private RedisTemplate redisTemplate;
	//查询品牌和规格
	private Map searchBrandAndSpecList(String category) {
		Map map =new HashMap<>();
		
		Long id = (Long) redisTemplate.boundHashOps("itemCat").get(category);
		
		if(id!=null) {
			List brandList = (List) redisTemplate.boundHashOps("brandList").get(id);
			
			List specList = (List) redisTemplate.boundHashOps("specList").get(id);
			
			map.put("brandList", brandList);
			map.put("specList", specList);
		}
		
		return map;
	}

	@Override
	public void importList(List list) {
		solrTemplate.saveBeans(list);
		solrTemplate.commit();
		
	}

	@Override
	public void deleteByGoodsIds(List goodsIdList) {
		Query query= new SimpleQuery();
		Criteria criteria=new Criteria("item_goodsid").in(goodsIdList);
		query.addCriteria(criteria);
		solrTemplate.delete(query);
		solrTemplate.commit();
	}
}
