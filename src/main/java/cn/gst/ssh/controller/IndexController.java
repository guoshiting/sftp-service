package cn.gst.ssh.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import ch.qos.logback.classic.Logger;
import cn.gst.ssh.init.FileConfig;
import cn.gst.ssh.init.LoggerBuilder;
import cn.gst.ssh.service.SftpService;

@Controller
public class IndexController {

	private static Logger logger = null;
	
	@RequestMapping("/")
	public String index(Model model) {
		logger = LoggerBuilder.initLogger(logger, IndexController.class);
		logger.info("访问首页的方法执行...");
		return "index";
	}
}
