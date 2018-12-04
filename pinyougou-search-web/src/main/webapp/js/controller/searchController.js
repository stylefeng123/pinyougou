app.controller('searchController',function($scope, $location ,searchService){
	
	$scope.searchMap={'keywords':'','category':'','brand':'','spec':{},'price':'','pageNo':1,'pageSize':40,'sortField':'','sort':''};
	
	
	$scope.search=function(){
		$scope.searchMap.pageNo=parseInt($scope.searchMap.pageNo);
		searchService.search($scope.searchMap).success(function(response){
			
			$scope.resultMap=response;
			
			buildPageLabel();
			
			
		})
	}
	
	buildPageLabel=function(){
		$scope.pageLabel=[];
		var firstPage=1;
		var lastPage=$scope.resultMap.totalPages;
		$scope.firstDot=true;
		$scope.lastDot=true;
		if($scope.resultMap.totalPages>5){
			if($scope.searchMap.pageNo<=3){
				lastPage=5;
				$scope.firstDot=false;
			}else if($scope.searchMap.pageNo>=$scope.resultMap.totalPages-2){
				firstPage=$scope.resultMap.totalPages-4;
				$scope.lastDot=false;
			}else{
				firstPage=$scope.searchMap.pageNo-2;
				lastPage=$scope.searchMap.pageNo+2;
			}
		}else{
			$scope.firstDot=false;
			$scope.lastDot=false;
		}
		for(var i=firstPage;i<=lastPage;i++){
			$scope.pageLabel.push(i);
		}
	}
	
	$scope.addSearchItem=function(key,value){
		if(key=='category' || key=='brand'||key=='price'){
			$scope.searchMap[key]=value;
		}else{
			$scope.searchMap.spec[key]=value;
		}
		
		$scope.search();//执行搜索 
	}
	
	$scope.removeSearchItem=function(key){
		if(key=='category' || key=='brand'||key=='price'){
			$scope.searchMap[key]="";
		}else{
			delete $scope.searchMap.spec[key];
		}
		
		$scope.search();//执行搜索 
	}
	
	$scope.queryByPage=function(pageNo){
		if(pageNo<1 || pageNo>$scope.resultMap.totalPages){
			return;
		}
		$scope.searchMap.pageNo=pageNo;
		$scope.search();
	}
	
	$scope.isTopPage=function(){
		if($scope.searchMap.pageNo==1){
			return true;
		}else{
			return false;
		}
	}

	$scope.isEndPage=function(){
		if($scope.searchMap.pageNo==$scope.resultMap.totalPages){
			return true;
		}else{
			return false;
		}
	}
	
	
	$scope.sortSearch=function(sort,sortField){
		$scope.searchMap.sort=sort;
		$scope.searchMap.sortField=sortField;
		$scope.search();
	}
	
	$scope.sortFlag=true;
	
	$scope.changeSortFlag=function(){
		if($scope.sortFlag==true){
			$scope.sortFlag=false;
		}else if($scope.sortFlag==false){
			$scope.sortFlag=true;
		}
	}
	
	$scope.keywordsIsBrand=function(){
		for(var i=0; i<$scope.resultMap.brandList.length;i++){
			if($scope.searchMap.keywords.indexOf($scope.resultMap.brandList[i].text)>=0){
				return true;
			}
		}
		return false;
	}
	
	$scope.loadkeywords=function(){
		$scope.searchMap.keywords=$location.search()['keywords'];
		$scope.search();
	}

})