package cn.gst.ssh.init;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.core.ConsoleAppender;
import ch.qos.logback.core.rolling.RollingFileAppender;
import ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy;
import ch.qos.logback.core.util.FileSize;
import ch.qos.logback.core.util.OptionHelper;
import cn.gst.ssh.controller.SftpController; 

@Component
public class LoggerBuilder {

	private static final Map<String,Logger> container = new HashMap<String,Logger>();  
	public static String logPath = null;
	private static final String logName = "ssh.log";
	  
	public static Logger initLogger(Logger log,Class clazz) {
		if(log == null) {
			log = LoggerBuilder.getLogger(clazz);
		}
		return log;
	}
	
	
    public static Logger getLogger(Class clazz) {
    	if(Initializer.logPath == null)
    		return null;
    	logPath = Initializer.logPath + File.separator;
    	String name = logName.replace(".log", "");
//        Logger logger = container.get(clazz.getName());  
//        if(logger != null) {  
//            return logger;  
//        }  
//        synchronized (LoggerBuilder.class) {  
//            logger = container.get(clazz.getName());  
//            if(logger != null) {  
//                return logger;  
//            }  
//            logger = build(clazz.getName());  
//            container.put(name,logger);  
//        }  
        return build(clazz.getName());  
    }  
  
    private static Logger build(String name) {  
        LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();  
        Logger logger = context.getLogger(name);  
        logger.setAdditive(false);  
        RollingFileAppender appender = new RollingFileAppender();  
        appender.setContext(context);  
        appender.setName(name);  
        appender.setFile(OptionHelper.substVars(logPath+logName,context));  
        appender.setAppend(true);  
        appender.setPrudent(false);  
        SizeAndTimeBasedRollingPolicy policy = new SizeAndTimeBasedRollingPolicy();  
        String fp = OptionHelper.substVars(logPath+logName+".%d{yyyy-MM-dd}.%i",context);  
  
        policy.setMaxFileSize(FileSize.valueOf("100MB"));  
        policy.setFileNamePattern(fp);  
        policy.setMaxHistory(15);  
        policy.setTotalSizeCap(FileSize.valueOf("5GB"));  
        policy.setParent(appender);  
        policy.setContext(context);  
        policy.start();  
  
        PatternLayoutEncoder encoder = new PatternLayoutEncoder();  
        encoder.setContext(context);  
        encoder.setPattern("%d{YY-MM-dd HH:mm:ss}[%level]%logger{36}-%msg%n");  
        encoder.start();  
  
        appender.setRollingPolicy(policy);  
        appender.setEncoder(encoder);  
        appender.start();  
        
        ConsoleAppender console = new ConsoleAppender();
        console.setContext(context);  
        console.setName(name); 
        console.setEncoder(encoder);
        console.start();
        
        logger.addAppender(appender);  
        logger.addAppender(console); 
        return logger;  
    }  
}
