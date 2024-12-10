package org.jeecg.modules.pcset.entity;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableLogic;
import org.jeecg.common.constant.ProvinceCityArea;
import org.jeecg.common.util.SpringContextUtils;
import lombok.Data;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.springframework.format.annotation.DateTimeFormat;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.jeecg.common.aspect.annotation.Dict;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: ftp附件索引表
 * @Author: jeecg-boot
 * @Date:   2024-12-10
 * @Version: V1.0
 */
@Data
@TableName("ftp_file_main")
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@ApiModel(value="ftp_file_main对象", description="ftp附件索引表")
public class FtpFileMain implements Serializable {
    private static final long serialVersionUID = 1L;

	/**主键*/
	@TableId(type = IdType.ASSIGN_ID)
    @ApiModelProperty(value = "主键")
    private String id;
	/**上传人*/
    @ApiModelProperty(value = "上传人")
    private String createBy;
	/**上传日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "上传日期")
    private Date createTime;
	/**更新人*/
    @ApiModelProperty(value = "更新人")
    private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
    @ApiModelProperty(value = "更新日期")
    private Date updateTime;
	/**所属部门*/
    @ApiModelProperty(value = "所属部门")
    private String sysOrgCode;
	/**附件名称*/
	@Excel(name = "附件名称", width = 15)
    @ApiModelProperty(value = "附件名称")
    private String fileName;
	/**附件大小*/
	@Excel(name = "附件大小", width = 15)
    @ApiModelProperty(value = "附件大小")
    private String fileSize;
	/**附件路径*/
	@Excel(name = "附件路径", width = 15)
    @ApiModelProperty(value = "附件路径")
    private String filePath;
	/**附件md5*/
	@Excel(name = "附件md5", width = 15)
    @ApiModelProperty(value = "附件md5")
    private String fileMd5;
	/**附件状态*/
	@Excel(name = "附件状态", width = 15)
    @ApiModelProperty(value = "附件状态")
    private Integer fileStatus;
	/**附件描述*/
	@Excel(name = "附件描述", width = 15)
    @ApiModelProperty(value = "附件描述")
    private String fileNotes;
	/**附件存储方式*/
	@Excel(name = "附件存储方式", width = 15)
    @ApiModelProperty(value = "附件存储方式")
    private String fileStoreType;
	/**附件类型*/
	@Excel(name = "附件类型", width = 15)
    @ApiModelProperty(value = "附件类型")
    private String fileType;
}
