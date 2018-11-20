package com.pinyougou.sellergoods.service;

import java.util.List;
import com.pinyougou.pojo.TbBrand;

import entity.PageResult;

public interface BrandService {
	List<TbBrand> findAll();
	
	PageResult findPage(int pageNum,int pageSize);
	
	PageResult findPage(TbBrand tbBrand, Integer page, Integer pageSize);
	
	void save(TbBrand tbBrand) throws Exception;

	TbBrand findById(Integer id);

	void update(TbBrand brand) throws Exception;

	void delete(Long [] ids) throws Exception;

	
}
