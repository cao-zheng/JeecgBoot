package org.jeecg.modules.pcset.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.twmacinta.util.MD5;
import org.apache.commons.lang3.StringUtils;
import org.apache.ftpserver.ftplet.*;
import org.jeecg.modules.pcset.entity.FtpFileMain;
import org.jeecg.modules.pcset.service.FtpFileMainServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Objects;


public class FtpPlet extends DefaultFtplet {

	@Value(value = "${apache.ftp.homedirectory}")
	private String ftp_homedirectory;

	@Autowired
	private FtpFileMainServiceImpl ftpFileMainService;


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
			String absolutePath = (ftp_homedirectory + fileMsg.getAbsolutePath()).replace("\\", "/");
			//md5计算
			String md5 = MD5.asHex(MD5.getHash(new File(absolutePath)));

			//查询db是否存在记录 (根据路径和md5)
			LambdaQueryWrapper<FtpFileMain> queryWrapper = new LambdaQueryWrapper<>();
			queryWrapper.eq(FtpFileMain::getFileMd5,md5)
					.eq(FtpFileMain::getFilePath,absolutePath);
			FtpFileMain ftpFileMain = ftpFileMainService.getOne(queryWrapper,false);
			if(Objects.nonNull(ftpFileMain)){
				ftpFileMain.setUpdateBy(name);
				ftpFileMain.setUpdateTime(new Date());
				ftpFileMainService.updateById(ftpFileMain);
			}else{
				//ftp存储db
				FtpFileMain ftpFileMainNew = new FtpFileMain();
				ftpFileMainNew.setFileName(filename);
				ftpFileMainNew.setFilePath(absolutePath);
				ftpFileMainNew.setCreateBy(name);
				ftpFileMainNew.setCreateTime(new Date());
				ftpFileMainNew.setFileSize(String.valueOf(fileMsg.getSize()));
				ftpFileMainNew.setFileMd5(md5);
				ftpFileMainService.save(ftpFileMainNew);
			}

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
	public FtpletResult onDeleteStart(FtpSession session, FtpRequest request) throws FtpException, IOException {
		// 获取上传文件的上传路径
		String path = session.getUser().getHomeDirectory();
		// 获取上传用户
		String name = session.getUser().getName();
		// 获取上传文件名
		String filename = request.getArgument();
		//获取删除附件信息
		FtpFile fileMsg = session.getFileSystemView().getFile(filename);

		System.out.println(fileMsg.isFile());

		String absolutePath = ftp_homedirectory + fileMsg.getAbsolutePath();
		String md5 = MD5.asHex(MD5.getHash(new File(absolutePath)));

		//根据md5和绝对路径删除附件
		if(StringUtils.isNotEmpty(absolutePath) && StringUtils.isNotEmpty(md5)){
			LambdaUpdateWrapper<FtpFileMain> updateWrapper = new LambdaUpdateWrapper<>();
			updateWrapper.eq(FtpFileMain::getFileMd5,md5)
							.eq(FtpFileMain::getFilePath,absolutePath);
			ftpFileMainService.remove(updateWrapper);
		}
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