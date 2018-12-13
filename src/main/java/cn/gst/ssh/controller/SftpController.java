package cn.gst.ssh.controller;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.sshd.server.SshServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ch.qos.logback.classic.Logger;
import cn.gst.ssh.entity.User;
import cn.gst.ssh.init.FileConfig;
import cn.gst.ssh.init.LoggerBuilder;
import cn.gst.ssh.service.SftpService;
import cn.gst.ssh.thread.SftpThread;

@Controller
public class SftpController {

	private static Logger logger = null;
	
	@Autowired
	private SftpService service;
	
	@RequestMapping("addUser")
	@ResponseBody
	public Map<String, Object> addUser(User user){
		logger = LoggerBuilder.initLogger(logger, SftpController.class);
		logger.info("增加用户的方法正在执行,参数user:{}",user);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<User> listUser = FileConfig.users;
			for (User u : listUser) {
				if(u.getName().equals(user.getName())) {
					map.put("code", "fail");
					map.put("msg", "用户名重复");
					map.put("tip", "name");
					return map;
				}
			}
			File f = new File(user.getFilePath());
			if(!f.exists()||!f.isDirectory()) {
				map.put("code", "fail");
				map.put("msg", "不是有效的目录!");
				map.put("tip", "url");
				return map;
			}
			service.addUser(user);
		} catch (Exception e) {
			logger.error("增加用户出错",e);
			map.put("code", "fail");
			map.put("msg", "系统出错,请联系管理员!");
		}
		map.put("code", "success");
		return map;
	}
	
	@RequestMapping("getAllUser")
	@ResponseBody
	public Map<String,Object> getAllUser(){
		logger = LoggerBuilder.initLogger(logger, SftpController.class);
		logger.info("获取全部用户的方法正在执行...");
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			List<User> users = FileConfig.users;
			if(users.size()>0) {
				map.put("data", users);
				map.put("size", users.size());
			}
			map.put("code", "success");
		} catch (Exception e) {
			logger.error("获取全部用户的方法出错",e);
			map.put("code", "fail");
		}
		logger.info(map.toString());
		return map;
	}
	
	@RequestMapping("deleteUser")
	@ResponseBody
	public Map<String,Object> deleteUser(User user){
		logger = LoggerBuilder.initLogger(logger, SftpController.class);
		logger.info("删除用户的方法正在执行..,user:{}",user);
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			service.deleteUser(user);
			map.put("code", "success");
		} catch (Exception e) {
			logger.error("删除用户的方法出错",e);
			map.put("code", "fail");
		}
		return map;
	}
	
	@RequestMapping("sftpRun")
	@ResponseBody
	public Map<String,Object> sftpRun(){
		Map<String, Object> map = new HashMap<String, Object>();
		logger = LoggerBuilder.initLogger(logger, SftpController.class);
		logger.info("启动sftp的方法开始执行");
		try {
			if(FileConfig.sshPool.size() != 0) {
				map.put("code", "fail");
				map.put("msg", "sftp已经启动,不能重复启动");
			}else {
				SftpThread t = new SftpThread();
				t.start();
				map.put("code", "success");
				map.put("msg", "sftp已经启动");
				FileConfig.time = System.currentTimeMillis();
			}
		} catch (Exception e) {
			logger.error("sftp启动失败",e);
			map.put("code", "fail");
			map.put("msg", "sftp启动失败");
		}
		return map;
	}
	
	@RequestMapping("stopRun")
	@ResponseBody
	public Map<String,Object> stopRun(){
		Map<String, Object> map = new HashMap<String, Object>();
		logger = LoggerBuilder.initLogger(logger, SftpController.class);
		logger.info("停止sftp的方法开始执行");
		try {
			Map<String, SshServer> sshPool = FileConfig.sshPool;
			if(sshPool.size()==1) {
				sshPool.get("defaut1").stop();
				sshPool.get("defaut1").close();
				sshPool.clear();
			}else if(sshPool.size()==0) {
				map.put("code", "success");
				map.put("msg", "sftp未启动");
				return map;
			}
			map.put("code", "success");
			map.put("msg", "sftp停止成功");
			FileConfig.time = System.currentTimeMillis();
		} catch (Exception e) {
			logger.error("sftp停止失败",e);
			map.put("code", "fail");
			map.put("msg", "sftp停止失败");
		}
		return map;
	}
}