app.controller('cartController',function($scope,cartService){
	
	
	$scope.findCartList=function(){
		cartService.findCartList().success(function(response){
			$scope.cartList=response;
			$scope.totalValue=cartService.sum($scope.cartList);//求合计数
		})
	}
	
	$scope.addGoodsToCartList=function(itemId,num){
		cartService.addGoodsToCartList(itemId,num).success(function(response){
				if(response.success){
					$scope.findCartList();//刷新列表
				}else{
					alert(response.message);//弹出错误提示
				}	
		})
	}
})
