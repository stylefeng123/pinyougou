package com.pinyougou.sellergoods.service;

import java.util.List;
import java.util.Map;

import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

public interface BrandService {
	
	
	List<TbBrand> findAll();
	
	PageResult findByPage(TbBrand brand, int pageNum, int pageSize);
	
	void save(TbBrand tbBrand) throws Exception;

	TbBrand findById(Long id) ;

	void update(TbBrand brand) throws Exception;

	void delete(Long [] ids) throws Exception;

	List<Map> selectOptionList();
}
