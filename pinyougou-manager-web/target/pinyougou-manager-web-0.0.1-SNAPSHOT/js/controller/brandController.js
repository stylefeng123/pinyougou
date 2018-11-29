app.controller('brandController', function($scope,$controller,$http,brandService) {
	
	$controller('baseController',{$scope:$scope});//继承	
	
		$scope.findAll = function() {
			brandService.findAll().success(function(data) {
				$scope.list = data;
			});
		}

		$scope.findPage = function(page, size) {
			brandService.findPage(page, size).success(function(data) {
				$scope.list = data.rows;
				$scope.paginationConf.totalItems = data.total;
			});
		}
		

		$scope.save = function() {
			var object = null
			if ($scope.entity.id != null) {
				object = brandService.update($scope.entity);
			} else {
				object = brandService.add($scope.entity);
			}
			object.success(function(data) {
				if (data.success) {
					$scope.reloadList();
				} else {
					alert(data.message);
				}
			});

		}

		$scope.findOne = function(id) {
			brandService.findOne(id).success(function(data) {
				$scope.entity = data;
			});
		}

		$scope.del = function() {
			brandService.del($scope.selectIds).success(
					function(data) {
						if (data.success) {
							$scope.reloadList();
						} else {
							alert(data.message);
						}
					});
		}
		$scope.searchEntity = {};
		
		$scope.search = function(page, size) {
			brandService.search(page, size,$scope.searchEntity).success(function(data) {
				$scope.list = data.rows;
				$scope.paginationConf.totalItems = data.total;
			});
		}
	});