package org.jeecg.modules.pcset.config;

import com.twmacinta.util.MD5;
import org.apache.ftpserver.ftplet.*;
import org.jeecg.common.util.Md5Util;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.modules.pcset.entity.FtpFile;
import org.jeecg.modules.pcset.service.FtpFileServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.util.Date;


public class FtpPlet extends DefaultFtplet {

	@Autowired
	private FtpFileServiceImpl ftpFileService;


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

		org.apache.ftpserver.ftplet.FtpFile fileMsg = session.getFileSystemView().getFile(filename);
		if(fileMsg.isFile()) {
			String absolutePath = path + fileMsg.getAbsolutePath();
			//md5计算
			String md5 = MD5.asHex(MD5.getHash(new File(absolutePath)));

			//ftp存储db
			FtpFile ftpFile = new FtpFile();
			ftpFile.setFileName(filename);
			ftpFile.setFilePath(absolutePath);
			ftpFile.setCreateBy(name);
			ftpFile.setCreateTime(new Date());
			ftpFile.setFileSize(String.valueOf(fileMsg.getSize()));
			ftpFile.setFileMd5(md5);
			ftpFileService.save(ftpFile);

			logger.info("用户:'{}'，上传文件到目录：'{}'，文件名称为：'{}，状态：成功！'", name, path, filename);
		}

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