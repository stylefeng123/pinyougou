package com.pinyougou.cart.service;

import java.util.List;

import com.pinyougou.pojogroup.Cart;

public interface CartService {

	 List<Cart> addGoodsToCartList(List<Cart> cartList,Long itemId,Integer num);
	 
	 List<Cart> findCartListFromRedis(String username);
	 
	 void saveCartListToRedis(List<Cart> cartList,String username);
	 
	 List<Cart> mergeCartList(List<Cart> cartList1,List<Cart> cartList2);
	 
}
