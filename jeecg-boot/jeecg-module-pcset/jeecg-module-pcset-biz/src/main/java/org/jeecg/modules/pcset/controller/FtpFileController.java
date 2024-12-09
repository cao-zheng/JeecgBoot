package org.jeecg.modules.pcset.controller;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.pcset.entity.FtpFile;
import org.jeecg.common.system.base.controller.JeecgController;
import org.jeecg.modules.pcset.service.FtpFileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: ftp附件索引表
 * @Author: jeecg-boot
 * @Date:   2024-12-09
 * @Version: V1.0
 */
@Api(tags="ftp附件索引表")
@RestController
@RequestMapping("/org/jeecg/modules/pcset/ftpFile")
@Slf4j
public class FtpFileController extends JeecgController<FtpFile, FtpFileServiceImpl> {
	@Autowired
	private FtpFileServiceImpl ftpFileService;
	
	/**
	 * 分页列表查询
	 *
	 * @param ftpFile
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "ftp附件索引表-分页列表查询")
	@ApiOperation(value="ftp附件索引表-分页列表查询", notes="ftp附件索引表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FtpFile>> queryPageList(FtpFile ftpFile,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<FtpFile> queryWrapper = QueryGenerator.initQueryWrapper(ftpFile, req.getParameterMap());
		Page<FtpFile> page = new Page<FtpFile>(pageNo, pageSize);
		IPage<FtpFile> pageList = ftpFileService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param ftpFile
	 * @return
	 */
	@AutoLog(value = "ftp附件索引表-添加")
	@ApiOperation(value="ftp附件索引表-添加", notes="ftp附件索引表-添加")
	@RequiresPermissions("org.jeecg.modules.pcset:ftp_file:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FtpFile ftpFile) {
		ftpFileService.save(ftpFile);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param ftpFile
	 * @return
	 */
	@AutoLog(value = "ftp附件索引表-编辑")
	@ApiOperation(value="ftp附件索引表-编辑", notes="ftp附件索引表-编辑")
	@RequiresPermissions("org.jeecg.modules.pcset:ftp_file:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FtpFile ftpFile) {
		ftpFileService.updateById(ftpFile);
		return Result.OK("编辑成功!");
	}
	
	/**
	 *   通过id删除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "ftp附件索引表-通过id删除")
	@ApiOperation(value="ftp附件索引表-通过id删除", notes="ftp附件索引表-通过id删除")
	@RequiresPermissions("org.jeecg.modules.pcset:ftp_file:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		ftpFileService.removeById(id);
		return Result.OK("删除成功!");
	}
	
	/**
	 *  批量删除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "ftp附件索引表-批量删除")
	@ApiOperation(value="ftp附件索引表-批量删除", notes="ftp附件索引表-批量删除")
	@RequiresPermissions("org.jeecg.modules.pcset:ftp_file:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.ftpFileService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量删除成功!");
	}
	
	/**
	 * 通过id查询
	 *
	 * @param id
	 * @return
	 */
	//@AutoLog(value = "ftp附件索引表-通过id查询")
	@ApiOperation(value="ftp附件索引表-通过id查询", notes="ftp附件索引表-通过id查询")
	@GetMapping(value = "/queryById")
	public Result<FtpFile> queryById(@RequestParam(name="id",required=true) String id) {
		FtpFile ftpFile = ftpFileService.getById(id);
		if(ftpFile==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(ftpFile);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param ftpFile
    */
    @RequiresPermissions("org.jeecg.modules.pcset:ftp_file:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FtpFile ftpFile) {
        return super.exportXls(request, ftpFile, FtpFile.class, "ftp附件索引表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("org.jeecg.modules.pcset:ftp_file:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, FtpFile.class);
    }

}
