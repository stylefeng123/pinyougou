package com.pinyougou.sellergoods.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pinyougou.mapper.TbBrandMapper;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.pojo.TbBrandExample;
import com.pinyougou.pojo.TbBrandExample.Criteria;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;

@Service
@Transactional
public class BrandServiceImpl implements BrandService {

	@Autowired
	private TbBrandMapper brandMapper;
	
	@Override
	public List<TbBrand> findAll() {
		// TODO 自动生成的方法存根
		List<TbBrand> list =brandMapper.selectByExample(null);
		return list;
	}

	@Override
	public PageResult findPage(int pageNum, int pageSize) {
		PageHelper.startPage(pageNum,pageSize);
		Page<TbBrand> pages = (Page<TbBrand>) brandMapper.selectByExample(null);
		// TODO 自动生成的方法存根
		return new PageResult(pages.getTotal(), pages.getResult());
	}

	@Override
	public void save(TbBrand tbBrand) throws Exception {
		// TODO 自动生成的方法存根
		List<TbBrand> list =brandMapper.selectByExample(null);
		for (TbBrand tbBrands : list) {
			if(tbBrand.getName().equals(tbBrands.getName())) {
				throw new Exception("品牌名称重复");
			}
		}
		brandMapper.insert(tbBrand);
		
	}

	@Override
	public TbBrand findById(Integer id) {
		// TODO 自动生成的方法存根
		long ids =id;
		TbBrand tbBrand = brandMapper.selectByPrimaryKey(ids);
		return tbBrand;
	}

	@Override
	public void update(TbBrand brand) throws Exception {
		// TODO 自动生成的方法存根
		List<TbBrand> list =brandMapper.selectByExample(null);
		for (TbBrand tbBrands : list) {
			if(brand.getName().equals(tbBrands.getName())) {
				throw new Exception("品牌名称重复");
			}
		}
		brandMapper.updateByPrimaryKey(brand);
		
	}

	@Override
	public void delete(Long [] ids) throws Exception {
		// TODO 自动生成的方法存根
		for (Long id : ids) {
		try {
			brandMapper.deleteByPrimaryKey(id);
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			throw new Exception();
		}
		}
		
	}

	@Override
	public PageResult findPage(TbBrand tbBrand, Integer page, Integer pageSize) {
		PageHelper.startPage(page,pageSize);
		TbBrandExample example = new TbBrandExample();
		Criteria createCriteria = example.createCriteria();
		if (tbBrand!=null) {
			if(tbBrand.getFirstChar()!=null&&tbBrand.getFirstChar().length()>0) {
				createCriteria.andFirstCharLike("%"+tbBrand.getFirstChar()+"%");
			}
			if(tbBrand.getName()!=null&&tbBrand.getName().length()>0) {
				createCriteria.andNameLike("%"+tbBrand.getName()+"%");
			}
		}
		Page<TbBrand> list = (Page<TbBrand>) brandMapper.selectByExample(example);
		return new PageResult(list.getTotal(),list.getResult());
	}
}
