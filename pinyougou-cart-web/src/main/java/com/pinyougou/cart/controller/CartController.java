package com.pinyougou.cart.controller;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.opensaml.xml.encryption.Public;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.pinyougou.cart.service.CartService;
import com.pinyougou.pojogroup.Cart;

import entity.Result;

@RestController
@RequestMapping("/cart")
public class CartController {

	@Reference
	private CartService cartService;
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private HttpServletResponse response;
	
	
	@RequestMapping("/findCartList")
	public  List<Cart> findCartList(){
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		System.out.println("当前登录用户："+name);
		
		String cookieValue = util.CookieUtil.getCookieValue(request, "cartList", "UTF-8");
		if(cookieValue==null || cookieValue.equals("")){
			cookieValue="[]";
		}
		List<Cart> cartList = JSON.parseArray(cookieValue, Cart.class);
		
		if ("anonymousUser".equals(name)){
			//从cookie中取出购物车
			System.out.println("向cookie提取数据");
			return cartList;
		}else {
			List<Cart> cartList1 = cartService.findCartListFromRedis(name);
			if (cartList.size()>0) {
				System.out.println("合并数据");
				cartList1 = cartService.mergeCartList(cartList1, cartList);
				cartService.saveCartListToRedis(cartList1, name);
				util.CookieUtil.deleteCookie(request, response, "cartList");
			}
			return cartList1;
		}
	}
	
	@RequestMapping("/addGoodsToCartList")
	public Result addGoodsToCartList(Long itemId,Integer num) {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		try {
			List<Cart> cartList = findCartList();
			//添加购物车
			List<Cart> carts = cartService.addGoodsToCartList(cartList, itemId, num);
			if("anonymousUser".equals(name)) {
				//cookie
				//将购物车加回cookie
				String cartsString = JSON.toJSONString(carts);
				util.CookieUtil.setCookie(request, response, "cartList", cartsString, 3600*24, "UTF-8");
				System.out.println("向cookie存入数据");
			}else {
				cartService.saveCartListToRedis(carts, name);
			}
			return new Result(true, "添加成功");
		} catch (Exception e) {
			e.printStackTrace();
			return new Result(false, "添加失败");
		}
	}
}
