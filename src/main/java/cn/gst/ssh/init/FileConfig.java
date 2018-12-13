package cn.gst.ssh.init;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.sshd.server.SshServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.gst.ssh.entity.User;
import cn.gst.ssh.util.FileUtil;

public class FileConfig {

	private final static Logger logger = LoggerFactory.getLogger(FileConfig.class);
//	public static boolean sftpFalg = false;//sftp开启或关闭指示
//	public static int sftpCount = 0; //sftp开启数量,暂时只能开启一个服务
	public static long time = System.currentTimeMillis();
	public static Map<String,SshServer> sshPool = new HashMap<String,SshServer>();
	
	public static List<User> users = new ArrayList<User>();
	
	public void addUsers(User user) {
		logger.info("添加用户,user:{}",user);
		users.add(user);
		StringBuffer sb = new StringBuffer();
		sb.append(user.getName());
		sb.append("&");
		sb.append(user.getPassword());
		sb.append("&");
		sb.append(user.getFilePath());
		sb.append("\r\n");
		FileUtil.writhEnd(sb.toString(),Initializer.confPath+ File.separator + "user",true);
	}
	
	public static void initUsers() {
		String url = Initializer.confPath + File.separator + "user";
		File file = new File(url);
		if(file.exists()) {
			List<String> list = FileUtil.readLine(url);
			for (String line : list) {
				String[] split = line.split("&");
				if(split.length == 3) {
					User user = new User();
					user.setName(split[0]);
					user.setPassword(split[1]);
					user.setFilePath(split[2]);
					users.add(user);
				}else {
					logger.error("文件损毁!!!");
				}
			}
		}
	}
}
