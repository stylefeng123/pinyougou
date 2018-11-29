app.service('brandService', function($http) {
		
		this.findAll = function() {
			return $http.get('../brand/findAll.do');
		}

		this.findPage = function(page, size) {
			return $http.get('../brand/findPage.do?page=' + page + '&pageSize='+ size);
		}

		this.add = function(entity) {
			return $http.post('../brand/save.do', entity);
		}

		this.update = function(entity) {
			return $http.post('../brand/update.do', entity);
		}
		
		this.findOne = function(id){
			return $http.get('../brand/findById.do?id=' + id);
		}
		
		this.del = function(ids){
			return $http.post('../brand/del.do', ids);
		}
		
		this.search = function(page,size,searchEntity){
			return $http.post('../brand/search.do?page=' + page + '&pageSize=' + size,searchEntity);
		}
		
		this.selectOptionList = function(){
			return $http.get('../brand/selectOptionList.do');
		}
	});