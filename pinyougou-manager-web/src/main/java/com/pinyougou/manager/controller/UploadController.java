package com.pinyougou.manager.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import entity.Result;
import util.FastDFSClient;

@RestController
public class UploadController {
	
	private String FILE_SERVER_URL="http://192.168.25.133/";

	
	@RequestMapping("/upload")
	public Result upload(MultipartFile file) {
		Result result = new Result();
		String originalFilename = file.getOriginalFilename();
		String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
		try {
			FastDFSClient client = new FastDFSClient("classpath:config/fdfs_client.conf");
			String uploadFile = client.uploadFile(file.getBytes(),extName);
			String url =FILE_SERVER_URL+uploadFile;
			result.setSuccess(true);
			result.setMessage(url);
			return result;
		} catch (Exception e) {
			// TODO 自动生成的 catch 块
			e.printStackTrace();
			result.setSuccess(false);
			result.setMessage("上传失败");
			return result;
		}
	}

}
