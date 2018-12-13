package cn.gst.ssh.service;

import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.springframework.stereotype.Service;

import ch.qos.logback.classic.Logger;
import cn.gst.ssh.entity.User;
import cn.gst.ssh.init.FileConfig;
import cn.gst.ssh.init.Initializer;
import cn.gst.ssh.init.LoggerBuilder;
import cn.gst.ssh.util.FileUtil;

@Service
public class SftpService {

	private static Logger logger = null;
	
	public void addUser(User user) {
		logger = LoggerBuilder.initLogger(logger, SftpService.class);
		logger.info("添加用户,user:{}",user);
		FileConfig.users.add(user);
		StringBuffer sb = new StringBuffer();
		sb.append(user.getName());
		sb.append("&");
		sb.append(user.getPassword());
		sb.append("&");
		sb.append(user.getFilePath());
		sb.append("\r\n");
		FileUtil.writhEnd(sb.toString(),Initializer.confPath+ File.separator + "user",true);
	}

	public void deleteUser(User user) {
		List<User> users = FileConfig.users;
		Iterator<User> iterator = users.iterator();
		while(iterator.hasNext()) {
			User user1 = iterator.next();
			if(user1.getName().equals(user.getName())) {
				iterator.remove();
			}
		}
		StringBuffer sb = new StringBuffer();
		for (User u : users) {
			sb.append(u.getName());
			sb.append("&");
			sb.append(u.getPassword());
			sb.append("&");
			sb.append(u.getFilePath());
			sb.append("\r\n");
		}
		FileUtil.writhEnd(sb.toString(),Initializer.confPath+ File.separator + "user",false);
	}
}
