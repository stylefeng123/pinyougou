 //控制层 
app.controller('itemCatController' ,function($scope,$controller   ,itemCatService,typeTemplateService){	
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		itemCatService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		itemCatService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		itemCatService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=itemCatService.update( $scope.entity ); //修改  
		}else{
			serviceObject=itemCatService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.findByParentId($scope.entity.parentId);//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		itemCatService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.selectIds=[];
					$scope.findByParentId($scope.parentId);//刷新列表
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		itemCatService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
	
	$scope.grade=1;
	
	$scope.setGrade=function(value){
		$scope.grade=value;
	}		
	$scope.selectList=function(p_entity){			
		if($scope.grade==1){//如果为1级
			$scope.entity_1=null;	
			$scope.entity_2=null;
		}		
		if($scope.grade==2){//如果为2级
			$scope.entity_1=p_entity;	
			$scope.entity_2=null;
		}		
		if($scope.grade==3){//如果为3级
			$scope.entity_2=p_entity;	
		}
		
		$scope.findByParentId(p_entity.id);	//查询此级下级列表
	}

	$scope.getParentId=function(){
		if($scope.grade==3){
			$scope.entity.parentId=$scope.entity_2.id;
		}
		if($scope.grade==2){
			$scope.entity.parentId=$scope.entity_1.id;
		}
		if($scope.grade==1){
			$scope.entity.parentId=0;
		}
	}
	
	
	$scope.parentId=0;
	
	$scope.findByParentId=function(id){
		$scope.parentId=id;
		itemCatService.findByParentId(id).success(
			function(response){
				$scope.list=response;
			}	
		)
	}
	
	$scope.typeTemplateList={data:[]};
	
	$scope.findTypeTemplateList=function(){
		typeTemplateService.selectTypeList().success(function(response){
			$scope.typeTemplateList={data:response};
		})
	}
	
	$scope.selectIds = [];
	$scope.lists;
	
	$scope.updateSelection = function($event, id) {
		if ($event.target.checked) {
			itemCatService.findByParentId(id).success(function(response){
					if(response==''){
						$scope.selectIds.push(id);
						$scope.lists=response;
					}else{
						alert("该分类下有子类 ,不会被删除");
						$event.target.checked=false;
					}
				})
		}
		 else {
			$scope.selectIds.splice($scope.selectIds.indexOf(id), 1);
		}
	}
});	
