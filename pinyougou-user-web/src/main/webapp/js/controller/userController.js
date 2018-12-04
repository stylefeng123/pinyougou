 //控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	
	$scope.reg=function(){
		
		if($scope.advice!=true){
			alert("请阅读用户协议");
			return;
		}
		
		if($scope.password!=$scope.entity.password){
			alert("两次密码输入不一致");
			return;
		}
		
		userService.add($scope.entity,$scope.checkCode).success(function(response){
			alert(response.message);
		})
	}
	
	$scope.sendCode=function(){
		if($scope.entity.phone==null){
			alert("请输入手机号！");
			return ;
		}
		userService.sendCode($scope.entity.phone).success(function(response){
			alert(response.message);
		})
	}
});	
