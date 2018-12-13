package cn.gst.ssh.init;

import java.io.File;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Logger;
import cn.gst.ssh.controller.SftpController;
@Component
@Order(1)
public class Initializer implements CommandLineRunner{

	private static Logger logger = null;
	
	public static String logPath = null;
	public static String confPath = null;
	
	
	@Override
	public void run(String... args) throws Exception {
		String path = System.getProperty("user.dir");
		String basePath = path;//.substring(0, path.lastIndexOf(File.separator))
		logPath = basePath + File.separator + "log";
		confPath = basePath + File.separator + "conf";
		logger = LoggerBuilder.initLogger(logger, SftpController.class);
		logger.info("初始化用户系统");
		FileConfig.initUsers();
		logger.info("程序启动完成");
	}
}
