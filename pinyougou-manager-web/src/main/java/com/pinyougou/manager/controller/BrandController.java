package com.pinyougou.manager.controller;

import java.util.ArrayList;
import java.util.List;

import org.apache.zookeeper.data.Id;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pinyougou.pojo.TbBrand;
import com.pinyougou.sellergoods.service.BrandService;

import entity.PageResult;
import entity.Result;


@RestController
@RequestMapping("brand")
public class BrandController {
	
	@Reference
	private BrandService brandService;

	@RequestMapping("findAll")
	public List<TbBrand> findAll() {
		return brandService.findAll();
	}
	
	@RequestMapping("findPage")
	public PageResult findPage(Integer page,Integer pageSize) {
		PageResult findPage = brandService.findPage(page, pageSize);
		return findPage;
	}
	
	@RequestMapping("save")
	public Result savebrand(@RequestBody TbBrand brand) {
		try {
			brandService.save(brand);
			return new Result(true,"添加成功");
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return new Result(false,"添加失败");
		}
	}
	
	@RequestMapping("findById")
	public TbBrand findById (Integer id) {
		TbBrand tbBrand = brandService.findById(id);
		return tbBrand;
	}
	
	@RequestMapping("update")
	public Result update(@RequestBody TbBrand brand) {
		try {
			brandService.update(brand);
			return new Result(true,"修改成功");
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return new Result(false,"修改失败");
		}
	}
	
	@RequestMapping("del")
	public Result delete(@RequestBody Long [] ids) {
		try {
			brandService.delete(ids);
			return new Result(true,"删除成功");
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			return new Result(false,"删除失败");
		}
	}
	
	@RequestMapping("search")
	public PageResult search(@RequestBody TbBrand tbBrand,Integer page,Integer pageSize) {
		return brandService.findPage(tbBrand,page, pageSize);
	}
}
