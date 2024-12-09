package org.jeecg.modules.pcset.config;

import org.apache.ftpserver.ftplet.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;

public class FtpPlet extends DefaultFtplet {

	private static final Logger logger = LoggerFactory.getLogger(FtpPlet.class);
	
	@Override
	public FtpletResult onLogin(FtpSession session, FtpRequest request) throws FtpException, IOException {
		// 获取上传文件的上传路径
		String path = session.getUser().getHomeDirectory();
		// 获取上传用户
		String name = session.getUser().getName();
		//校验文件夹路径是否存在
		viladateDir(path);
		
		logger.info("用户:'{}'登录成功, 目录地址: '{}'", name, path);
		return super.onLogin(session, request);
	}

	@Override
	public FtpletResult onUploadStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
		// 获取上传文件的上传路径
		String path = session.getUser().getHomeDirectory();
		//校验文件夹路径是否存在
		viladateDir(path);
		
		// 获取上传用户
		String name = session.getUser().getName();
		// 获取上传文件名
		String filename = request.getArgument();
		logger.info("用户:'{}'，上传文件到目录：'{}'，文件名称为：'{}，状态：开始上传~'", name, path, filename);
		return super.onUploadStart(session, request);
	}

	@Override
	public FtpletResult onUploadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
		// 获取上传文件的上传路径
		String path = session.getUser().getHomeDirectory();
		// 获取上传用户
		String name = session.getUser().getName();
		// 获取上传文件名
		String filename = request.getArgument();
		logger.info("用户:'{}'，上传文件到目录：'{}'，文件名称为：'{}，状态：成功！'", name, path, filename);
		return super.onUploadEnd(session, request);
	}

	@Override
	public FtpletResult onDownloadStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
		// todo servies...
		return super.onDownloadStart(session, request);
	}

	@Override
	public FtpletResult onDownloadEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
		// todo servies...
		return super.onDownloadEnd(session, request);
	}

	@Override
	public FtpletResult onDeleteEnd(FtpSession session, FtpRequest request) throws FtpException, IOException {
		return super.onDeleteEnd(session,request);
	}
	
	//判断文件夹是否存在，不存在则创建文件夹
	public void viladateDir(String path) {
		File file = new File(path);

		// 如果文件夹不存在则创建
		if (!file.exists() && !file.isDirectory()){
			System.out.println("文件夹不存在，创建新的文件夹");
			file.mkdir();
		}
	}

}