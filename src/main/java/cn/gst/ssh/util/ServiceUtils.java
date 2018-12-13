package cn.gst.ssh.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.sshd.common.NamedFactory;
import org.apache.sshd.common.file.virtualfs.VirtualFileSystemFactory;
import org.apache.sshd.server.Command;
import org.apache.sshd.server.CommandFactory;
import org.apache.sshd.server.SshServer;
//import org.apache.sshd.server.auth.UserAuthPassword;
import org.apache.sshd.server.auth.password.PasswordAuthenticator;
import org.apache.sshd.server.auth.password.PasswordChangeRequiredException;
import org.apache.sshd.server.config.keys.DefaultAuthorizedKeysAuthenticator;
import org.apache.sshd.server.keyprovider.SimpleGeneratorHostKeyProvider;
import org.apache.sshd.server.scp.ScpCommandFactory;
import org.apache.sshd.server.session.ServerSession;
import org.apache.sshd.server.shell.ProcessShellFactory;
import org.apache.sshd.server.subsystem.sftp.SftpSubsystemFactory;
//import org.apache.sshd.SshServer;

import ch.qos.logback.classic.Logger;
import cn.gst.ssh.entity.User;
import cn.gst.ssh.init.FileConfig;
import cn.gst.ssh.init.LoggerBuilder;

public class ServiceUtils {

	private static Logger logger = null;

	public static void createSSH() throws InterruptedException {
		SshServer sshd = SshServer.setUpDefaultServer();
		sshd.setPort(22);

		// *give host key generator a path, when sshd server restart, the same key will
		// be load and used to authenticate the server
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));

		sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
			@Override
			public boolean authenticate(String username, String password, ServerSession session) {
				logger.info("authen:  user=" + username + "  password=" + password);
				if ("admin".equals(username) && "123456".equals(password))
					return true;
				return false;
			}
		});

		// use file ~/.ssh/authorized_keys
		sshd.setPublickeyAuthenticator(new DefaultAuthorizedKeysAuthenticator(false));

		// * CommandFactory can be userd in addition to the ShellFactory,
		// * it can also be used instead of the ShellFactory.
		// * The CommandFactory is used when direct commands are sent to the SSH server,
		// * as this is the case when running ssh localhost shutdown or scp xxx
		ScpCommandFactory scpCmdFactory = new ScpCommandFactory();
		sshd.setShellFactory(new ProcessShellFactory(new String[] { "cmd.exe" }));
		scpCmdFactory.setDelegateCommandFactory(new CommandFactory() {
			public Command createCommand(String command) {
				System.out.println("command = \"" + command + "\"");
				return new ProcessShellFactory(("cmd /c " + command).split(" ")).create();
			}
		});
		sshd.setCommandFactory(scpCmdFactory);

		try {
			sshd.start();
			Thread.sleep(100000l);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void createSftp() {
		logger = LoggerBuilder.initLogger(logger, ServiceUtils.class);
//		new UserAuthPassword.Factory();
		// 工厂方法，使用默认属性创建ssh服务对象
		SshServer sshd = SshServer.setUpDefaultServer();
		FileConfig.sshPool.put("defaut1", sshd);
		sshd.setPort(22);
		// String stest = "F:\\MyEclipse\\SFTPServer\\key.pem";
		// String []sKyepPath={stest};
		sshd.setKeyPairProvider(new SimpleGeneratorHostKeyProvider(Paths.get("hostkey.ser")));
		// 保存会话安全校验信息，key.pem和key.ser为文件名
//		sshd.setKeyPairProvider(new FileKeyPairProvider(Paths.get("hostkey.ser")));
		List<NamedFactory<Command>> namedFactoryList = new ArrayList<NamedFactory<Command>>();
		// new SftpSubsystem.Factory()
		namedFactoryList.add(new SftpSubsystemFactory());
		sshd.setSubsystemFactories(namedFactoryList);
		sshd.setCommandFactory(new ScpCommandFactory());
		sshd.setShellFactory(new ProcessShellFactory(new String[] { "cmd.exe" }));
		sshd.setPasswordAuthenticator(new PasswordAuthenticator() {
			@Override
			public boolean authenticate(String username, String password, ServerSession session)
					throws PasswordChangeRequiredException {
				logger.info("authen:  user=" + username + "  password=" + password);
				List<User> users = FileConfig.users;
				for (User user : users) {
					if(username.equals(user.getName())&&password.equals(user.getPassword())) {
						sshd.setFileSystemFactory(new VirtualFileSystemFactory(new File(user.getFilePath()).toPath()));
						return true;
					}
				}
				return false;
			}
		});
		// use file ~/.ssh/authorized_keys
		sshd.setPublickeyAuthenticator(new DefaultAuthorizedKeysAuthenticator(false));
		try {
			sshd.start();
			
		} catch (Exception e) {
			logger.error("sftp服务错误");
		}
	}

//	public static void main(String[] args) throws InterruptedException {
//		createSftp();
//		createSSH();
//		System.out.println(new File("D:\\eclipse\\conf").toPath());
//	}
}
