package cn.gst.ssh.thread;

import cn.gst.ssh.util.ServiceUtils;

public class SftpThread extends Thread {

	public void run() {
//		FileConfig.sftpCount++;
//		FileConfig.sftpFalg=true;
		ServiceUtils.createSftp();
	}
}
