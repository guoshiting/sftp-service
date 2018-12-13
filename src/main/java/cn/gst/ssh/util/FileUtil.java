package cn.gst.ssh.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtil {

	public static final Logger logger = LoggerFactory.getLogger(FileUtil.class);

	public static List<String> readLine(String url) {
		List<String> list = new ArrayList<String>();

		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader br = null; // 用于包装InputStreamReader,提高处理性能。因为BufferedReader有缓冲的，而InputStreamReader没有。

		try {
			String str = "";
			fis = new FileInputStream(url);// FileInputStream
			// 从文件系统中的某个文件中获取字节
			isr = new InputStreamReader(fis);// InputStreamReader 是字节流通向字符流的桥梁,
			br = new BufferedReader(isr);// 从字符输入流中读取文件中的内容,封装了一个new InputStreamReader的对象
			while ((str = br.readLine()) != null) {
				list.add(str);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != br) {
				try {
					br.close();
				} catch (IOException e) {
					logger.error("流关闭错误");
				}
			}
			if (null != isr) {
				try {
					isr.close();
				} catch (IOException e) {
					logger.error("流关闭错误");
				}
			}
			if (null != fis) {
				try {
					fis.close();
				} catch (IOException e) {
					logger.error("流关闭错误");
				}
			}
		}
		return list;
	}

	public static void writhEnd(String str, String url,boolean end) {
		FileWriter writer = null;
		try {
			writer = new FileWriter(url, end);
			writer.write(str);
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			try {
				if(writer != null)
					writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
