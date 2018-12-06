app.service('cartService',function($http){
	
	this.findCartList=function(){
		return $http.get('cart/findCartList.do');
	}
	
	this.addGoodsToCartList=function(itemId,num){
		return $http.get('cart/addGoodsToCartList.do?itemId='+itemId+'&num='+num);
	}
	
	
	this.sum=function(cartList){
		var totalValue = {totalNum:0,totalMoney:0};
		for(var i=0;i<cartList.length;i++){
			var items = cartList[i].orderItemList;
			for(var j=0;j<items.length;j++){
				totalValue.totalNum+=items[j].num;
				totalValue.totalMoney+=items[j].totalFee;
			}
		}
		return totalValue;
	}
})