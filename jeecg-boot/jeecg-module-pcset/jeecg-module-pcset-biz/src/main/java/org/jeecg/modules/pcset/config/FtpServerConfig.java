package org.jeecg.modules.pcset.config;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.ftpserver.ConnectionConfigFactory;
import org.apache.ftpserver.DataConnectionConfigurationFactory;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.Ftplet;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.ClearTextPasswordEncryptor;
import org.apache.ftpserver.usermanager.DbUserManagerFactory;
import org.jeecg.common.util.SpringContextUtils;
import org.jeecg.modules.pcset.service.FtpFileServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ProjectName MyFtpServer
 * @author Administrator
 * @version 1.0.0
 * @Description
 * 注意：被@Configuration标记的类会被加入ioc容器中，而且类中所有带 @Bean注解的方法都会被动态代理，因此调用该方法返回的都是同一个实例。
 * ftp服务访问地址：
 * ftp://localhost:2121/
 * @createTime 2022/4/21 0021 23:21
 */
@Configuration
public class FtpServerConfig {
	private static final Logger logger = LoggerFactory.getLogger(FtpServerConfig.class);

	@Autowired
	private DataSource dataSource;

	@Value(value = "${apache.ftp.homedirectory}")
	private String ftp_homedirectory;

	@Value(value = "${apache.ftp.ftpport}")
	private Integer ftp_ftpport;

	@Value(value = "${apache.ftp.passiveports}")
	private String ftp_passiveports;

	@Value(value = "${apache.ftp.maxlogins}")
	private Integer ftp_maxlogins;

	@Value(value = "${apache.ftp.adminname}")
	private String ftp_adminname;

	@Value(value = "${apache.ftp.sqluseradmin}")
	private String ftp_sqluseradmin;

	@Value(value = "${apache.ftp.sqluserinsert}")
	private String ftp_sqluserinsert;

	@Value(value = "${apache.ftp.sqluserdelete}")
	private String ftp_sqluserdelete;

	@Value(value = "${apache.ftp.sqluserupdate}")
	private String ftp_sqluserupdate;

	@Value(value = "${apache.ftp.sqluserselect}")
	private String ftp_sqluserselect;

	@Value(value = "${apache.ftp.sqluserselectall}")
	private String ftp_sqluserselectall;

	@Value(value = "${apache.ftp.sqluserauthenticate}")
	private String ftp_sqluserauthenticate;


	@Value("${apache.ftpclient.ip}")
	private String client_ip;
	@Value("${apache.ftpclient.port}")
	private int client_port;
	@Value("${apache.ftpclient.username}")
	private String client_username;
	@Value("${apache.ftpclient.password}")
	private String client_password;
	@Value("${apache.ftpclient.conntimeout}")
	private int client_conntimeout;
	@Value("${apache.ftpclient.encoding}")
	private String client_encoding;

	@Bean
	public FtpPlet ftpPlet(){
		return new FtpPlet();
	}
	/**
	 * ftp server
	 * @throws IOException
	 */
	@Bean
	public FtpServer ftpServer() throws FtpException {
		FtpServer ftpServer = null;
		FtpServerFactory serverFactory = new FtpServerFactory();
		ListenerFactory listenerFactory = new ListenerFactory();
		// 1、设置服务端口
		listenerFactory.setPort(ftp_ftpport);
		
		// 2、设置被动模式数据上传的接口范围,云服务器需要开放对应区间的端口给客户端
		DataConnectionConfigurationFactory dataConnectionConfFactory = new DataConnectionConfigurationFactory();
		dataConnectionConfFactory.setPassivePorts(ftp_passiveports);
		listenerFactory.setDataConnectionConfiguration(dataConnectionConfFactory.createDataConnectionConfiguration());
		
		// 3、增加SSL安全配置
		/*
		SslConfigurationFactory ssl = new SslConfigurationFactory();
		ssl.setKeystoreFile(new File("src/main/resources/ftpserver.jks"));
		ssl.setKeystorePassword("password");
		ssl.setSslProtocol("SSL");
		//set the SSL configuration for the listener
		listenerFactory.setSslConfiguration(ssl.createSslConfiguration());
		listenerFactory.setImplicitSsl(true);
		*/
		
		//替换默认的监听器
		serverFactory.addListener("default", listenerFactory.createListener());
		
		// 4、设置最大连接数
		ConnectionConfigFactory connectionConfigFactory = new ConnectionConfigFactory();
		connectionConfigFactory.setMaxLogins(ftp_maxlogins);
		serverFactory.setConnectionConfig(connectionConfigFactory.createConnectionConfig());
		
		// 5、配置自定义用户事件
		Map<String, Ftplet> ftpLets = new HashMap<String, Ftplet>();
		ftpLets.put("ftpService", ftpPlet());
		serverFactory.setFtplets(ftpLets);
		
		// 6、读取用户的配置信息
		// 注意：配置文件位于resources目录下，如果项目使用内置容器打成jar包发布，FTPServer无法直接直接读取Jar包中的配置文件。
		// 解决办法：将文件复制到指定目录(本文指定到根目录)下然后FTPServer才能读取到。
		/*
		PropertiesUserManagerFactory userManagerFactory = new PropertiesUserManagerFactory();
		String tempPath = System.getProperty("java.io.tmpdir") + System.currentTimeMillis() + ".properties";
		File tempConfig = new File(tempPath);
		ClassPathResource resource = new ClassPathResource("users.properties");
		IOUtils.copy(resource.getInputStream(), new FileOutputStream(tempConfig));
		userManagerFactory.setFile(tempConfig);
		userManagerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());  //密码以明文的方式
		serverFactory.setUserManager(userManagerFactory.createUserManager());
		*/
		
		// 6.2、基于数据库来存储用户实例
		DbUserManagerFactory dbUserManagerFactory = new DbUserManagerFactory();
		dbUserManagerFactory.setDataSource(dataSource);
		dbUserManagerFactory.setAdminName(ftp_adminname);
		dbUserManagerFactory.setSqlUserAdmin(ftp_sqluseradmin);
		dbUserManagerFactory.setSqlUserInsert(ftp_sqluserinsert);
		dbUserManagerFactory.setSqlUserDelete(ftp_sqluserdelete);
		dbUserManagerFactory.setSqlUserUpdate(ftp_sqluserupdate);
		dbUserManagerFactory.setSqlUserSelect(ftp_sqluserselect);
		dbUserManagerFactory.setSqlUserSelectAll(ftp_sqluserselectall);
		dbUserManagerFactory.setSqlUserAuthenticate(ftp_sqluserauthenticate);
		dbUserManagerFactory.setPasswordEncryptor(new ClearTextPasswordEncryptor());  //密码以明文的方式
		serverFactory.setUserManager(dbUserManagerFactory.createUserManager());
		
		// 7、实例化FTP Server
		ftpServer = serverFactory.createServer();
		ftpServer.start();
		return ftpServer;
	}

	/**
	 *   获取ftpclient
	 */
	@Bean
	public FTPClient ftpClient() throws IOException {
		FTPClient ftpClient = new FTPClient();
		ftpClient.connect(client_ip, client_port);
		ftpClient.login(client_username, client_password);
		ftpClient.setConnectTimeout(client_conntimeout);// 设置连接超时时间,5000毫秒
		ftpClient.setControlEncoding(client_encoding);// 设置中文编码集，防止中文乱码
		return ftpClient;
	}
}
