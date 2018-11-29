 //控制层 
app.controller('goodsController' ,function($scope,$controller,$location,goodsService,itemCatService,typeTemplateService){	
	
	$controller('baseController',{$scope:$scope});//继承
	
    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		goodsService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		goodsService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	

	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id!=null){//如果有ID
			serviceObject=goodsService.update( $scope.entity ); //修改  
		}else{
			serviceObject=goodsService.add( $scope.entity  );//增加 
		}				
		serviceObject.success(
			function(response){
				if(response.success){
					//重新查询 
		        	$scope.reloadList();//重新加载
				}else{
					alert(response.message);
				}
			}		
		);				
	}
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		goodsService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		goodsService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	};
    
  	$scope.status=['未审核','已审核','未通过','关闭'];
  	
  	$scope.itemCatList=[];
  	
  	$scope.findItemCatList=function(){
  		itemCatService.findAll().success(function(data){
  			for(var i=0;i<data.length;i++){
  				$scope.itemCatList[data[i].id]=data[i].name;
  			}
  		})
  	}
  	
  	$scope.findOne=function(){	
		var id = $location.search()['id'];
		if(id==null){
			return;
		}
		goodsService.findOne(id).success(
			function(response){
				$scope.entity= response;	
				editor.html(response.goodsDesc.introduction);
				$scope.entity.goodsDesc.itemImages=JSON.parse($scope.entity.goodsDesc.itemImages);
				$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.entity.goodsDesc.customAttributeItems);
				$scope.entity.goodsDesc.specificationItems=JSON.parse($scope.entity.goodsDesc.specificationItems);
				
				for(var i=0;i<$scope.entity.itemList.length;i++){
					$scope.entity.itemList[i].spec=JSON.parse($scope.entity.itemList[i].spec);
				}
			}
		);				
	}
  	

  	$scope.checkAttributeValue=function(specName,optionName){
  		var items= $scope.entity.goodsDesc.specificationItems;
  		var object = $scope.searchObjectByKey(items,'attributeName',specName);
  		if(object !=null){
  			if(object.attributeValue.indexOf(optionName)>=0){
  				return true;
  			}else{
  				return false;
  			}
  		}else{
  			return false;
  		}
  	}
  	
  	$scope.entity={goods:{},goodsDesc:{itemImages:[],specificationItems:[]}};//定义页面实体结构
    //添加图片列表
    $scope.add_image_entity=function(){    	
        $scope.entity.goodsDesc.itemImages.push($scope.image_entity);
    }

    $scope.remove_image_entity=function(index){
	    $scope.entity.goodsDesc.itemImages.splice(index,1);
    }

    $scope.selectItemCat1List=function(){
    	itemCatService.findByParentId(0).success(function(response){
    		$scope.itemCat1List=response;
    	});
    }
    
    $scope.selectItemCat1List=function(){
    	itemCatService.findByParentId(0).success(function(response){
    		$scope.itemCat1List=response;
    	});
    }
  	
    $scope.$watch('entity.goods.category1Id',function(newValue,oldValue){
    	itemCatService.findByParentId(newValue).success(function(response){
    		$scope.itemCat2List=response;
    	});
    })
    
    $scope.$watch('entity.goods.category2Id',function(newValue,oldValue){
    	itemCatService.findByParentId(newValue).success(function(response){
    		$scope.itemCat3List=response;
    	});
    })
    
    $scope.$watch('entity.goods.category3Id',function(newValue,oldValue){
    	itemCatService.findOne(newValue).success(function(response){
    		$scope.entity.goods.typeTemplateId=response.typeId;
    	});
    })
   
    $scope.$watch('entity.goods.typeTemplateId',function(newValue,oldValue){
    	typeTemplateService.findOne(newValue).success(function(response){
    		$scope.typeTemplate=response;
    		$scope.typeTemplate.brandIds=JSON.parse($scope.typeTemplate.brandIds);
    		if($location.search()['id']==null){
        		$scope.entity.goodsDesc.customAttributeItems=JSON.parse($scope.typeTemplate.customAttributeItems);
    		}
    	});
    });
    
    
    $scope.updateStatus=function(status){
    	goodsService.updateStatus($scope.selectIds,status).success(function(response){
    		if(response.success){
				//重新查询 
	        	$scope.reloadList();//重新加载
	        	$scope.selectIds=[];
	        	alert(response.message);
			}else{
				alert(response.message);
			}
    	});
    }
});	
