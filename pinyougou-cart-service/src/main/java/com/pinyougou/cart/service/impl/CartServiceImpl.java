package com.pinyougou.cart.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import com.alibaba.dubbo.config.annotation.Service;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.mapper.TbItemMapper;
import com.pinyougou.pojo.TbItem;
import com.pinyougou.pojo.TbOrderItem;
import com.pinyougou.pojogroup.Cart;
@Service
public class CartServiceImpl implements CartService {

	@Autowired
	private TbItemMapper itemMapper;
	
	@Override
	public List<Cart> addGoodsToCartList(List<Cart> cartList, Long itemId, Integer num) {
		//1.从itemId获取item
		TbItem item = itemMapper.selectByPrimaryKey(itemId);
		if (item==null) {
			throw new RuntimeException("无此商品");
		}if (!"1".equals(item.getStatus())) {
			throw new RuntimeException("此商品已下架");
		}
		//2.从item获取sellerid
		String sellerId = item.getSellerId();
		//3.根据商家ID判断购物车列表中是否存在该商家的购物车
		Cart cart = searchCartBySellerId(cartList, sellerId);
		//4.如果不存在 创建新的cart 添加到cartList
		if(cart==null) {
			cart = creatCart(item,num);
			cartList.add(cart);
			return cartList;
		}
		//5.如果存在
		else {
			//5.1 判断cart中orderItemList是否有该item
			TbOrderItem orderItem = searchOrderItemByItemId(cart.getOrderItemList(),itemId);
			if(orderItem==null) {
				//5.2 如果不存在 将item添加到orderItemList 
				orderItem = createOrderItem(item, num);
				cart.getOrderItemList().add(orderItem);
			}else {
				//5.3 如果存在 增加数量和价格
				orderItem.setNum(orderItem.getNum()+num);
				double newPrice= orderItem.getTotalFee().doubleValue()+(orderItem.getPrice().doubleValue()*num);
				orderItem.setTotalFee(new BigDecimal(newPrice));
				if (orderItem.getNum()<=0) {
					cart.getOrderItemList().remove(orderItem);
				}
				if(cart.getOrderItemList().size()==0) {
					cartList.remove(cart);
				}
			}
			return cartList;
		}
		
	}
	
	// 创建新的cart
	private Cart creatCart(TbItem item,Integer num) {
		TbOrderItem orderItem = createOrderItem(item, num);
		Cart cart = new Cart();
		ArrayList<TbOrderItem> arrayList = new ArrayList<>();
		arrayList.add(orderItem);
		cart.setOrderItemList(arrayList);
		cart.setSellerId(item.getSellerId());
		cart.setSellerName(item.getSeller());
		return cart;
	}
	
	//创建TbOrderItem
	private TbOrderItem createOrderItem(TbItem item,Integer num) {
		if(num<=0){
			throw new RuntimeException("数量非法");
		}
		TbOrderItem orderItem = new TbOrderItem();
		orderItem.setItemId(item.getId());
		orderItem.setGoodsId(item.getGoodsId());
		orderItem.setTitle(item.getTitle());
		orderItem.setNum(num);
		orderItem.setPicPath(item.getImage());
		orderItem.setPrice(item.getPrice());
		orderItem.setSellerId(item.getSellerId());
		orderItem.setTotalFee(new BigDecimal(item.getPrice().doubleValue()*num));
		return orderItem;
	}
	
	//3.根据商家ID判断购物车列表中是否存在该商家的购物车
	private Cart searchCartBySellerId(List<Cart> cartList,String sellerId) {
		for (Cart cart : cartList) {
			if (cart.getSellerId().equals(sellerId)) {
				return cart;
			}
		}
		return null;
	}
	
	//5.1 判断cart中orderItemList是否有该item
	private TbOrderItem searchOrderItemByItemId(List<TbOrderItem> orderItems,Long itemId) {
		for (TbOrderItem orderItem : orderItems) {
			if (orderItem.getItemId().longValue()==itemId.longValue()) {
				return orderItem;
			}
		}
		return null;
	}

	
	@Autowired
	private RedisTemplate redisTemplate;
	
	@Override
	public List<Cart> findCartListFromRedis(String username) {
		System.out.println("从redis中提取购物车数据....."+username);
		List<Cart> cartList  = (List<Cart>) redisTemplate.boundHashOps("cartList").get(username);
		if(cartList==null){
			cartList=new ArrayList();
		}
		return cartList ;
	}

	@Override
	public void saveCartListToRedis(List<Cart> cartList, String username) {
		System.out.println("向redis存入购物车数据....."+username);
		redisTemplate.boundHashOps("cartList").put(username, cartList);
	}

	@Override
	public List<Cart> mergeCartList(List<Cart> cartList1, List<Cart> cartList2) {
		for (Cart cart : cartList2) {
			for (TbOrderItem item : cart.getOrderItemList()) {
				cartList1 = addGoodsToCartList(cartList1,item.getItemId(),item.getNum());
			}
		}
		return cartList1;
	}
	
}
