package org.jeecg.modules.pcset.controller;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jeecg.common.api.vo.Result;
import org.jeecg.common.system.query.QueryGenerator;
import org.jeecg.common.system.query.QueryRuleEnum;
import org.jeecg.common.util.oConvertUtils;
import org.jeecg.modules.pcset.entity.FtpFileMain;
import org.jeecg.modules.pcset.service.FtpFileMainServiceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecg.modules.pcset.service.FtpFileMainServiceImpl;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.jeecg.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.jeecg.common.aspect.annotation.AutoLog;
import org.apache.shiro.authz.annotation.RequiresPermissions;

 /**
 * @Description: ftp附件索引表
 * @Author: jeecg-boot
 * @Date:   2024-12-10
 * @Version: V1.0
 */
@Api(tags="ftp附件索引表")
@RestController
@RequestMapping("/pcset/ftpFileMain")
@Slf4j
public class FtpFileMainController extends JeecgController<FtpFileMain, FtpFileMainServiceImpl> {
	@Autowired
	private FtpFileMainServiceImpl ftpFileMainService;
	
	/**
	 * 分页列表查询
	 *
	 * @param ftpFileMain
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	//@AutoLog(value = "ftp附件索引表-分页列表查询")
	@ApiOperation(value="ftp附件索引表-分页列表查询", notes="ftp附件索引表-分页列表查询")
	@GetMapping(value = "/list")
	public Result<IPage<FtpFileMain>> queryPageList(FtpFileMain ftpFileMain,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
        QueryWrapper<FtpFileMain> queryWrapper = QueryGenerator.initQueryWrapper(ftpFileMain, req.getParameterMap());
		Page<FtpFileMain> page = new Page<FtpFileMain>(pageNo, pageSize);
		IPage<FtpFileMain> pageList = ftpFileMainService.page(page, queryWrapper);
		return Result.OK(pageList);
	}
	
	/**
	 *   添加
	 *
	 * @param ftpFileMain
	 * @return
	 */
	@AutoLog(value = "ftp附件索引表-添加")
	@ApiOperation(value="ftp附件索引表-添加", notes="ftp附件索引表-添加")
	@RequiresPermissions("pcset:ftp_file_main:add")
	@PostMapping(value = "/add")
	public Result<String> add(@RequestBody FtpFileMain ftpFileMain) {
		ftpFileMainService.save(ftpFileMain);
		return Result.OK("添加成功！");
	}
	
	/**
	 *  编辑
	 *
	 * @param ftpFileMain
	 * @return
	 */
	@AutoLog(value = "ftp附件索引表-编辑")
	@ApiOperation(value="ftp附件索引表-编辑", notes="ftp附件索引表-编辑")
	@RequiresPermissions("pcset:ftp_file_main:edit")
	@RequestMapping(value = "/edit", method = {RequestMethod.PUT,RequestMethod.POST})
	public Result<String> edit(@RequestBody FtpFileMain ftpFileMain) {
		ftpFileMainService.updateById(ftpFileMain);
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
	@RequiresPermissions("pcset:ftp_file_main:delete")
	@DeleteMapping(value = "/delete")
	public Result<String> delete(@RequestParam(name="id",required=true) String id) {
		ftpFileMainService.removeById(id);
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
	@RequiresPermissions("pcset:ftp_file_main:deleteBatch")
	@DeleteMapping(value = "/deleteBatch")
	public Result<String> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.ftpFileMainService.removeByIds(Arrays.asList(ids.split(",")));
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
	public Result<FtpFileMain> queryById(@RequestParam(name="id",required=true) String id) {
		FtpFileMain ftpFileMain = ftpFileMainService.getById(id);
		if(ftpFileMain==null) {
			return Result.error("未找到对应数据");
		}
		return Result.OK(ftpFileMain);
	}

    /**
    * 导出excel
    *
    * @param request
    * @param ftpFileMain
    */
    @RequiresPermissions("pcset:ftp_file_main:exportXls")
    @RequestMapping(value = "/exportXls")
    public ModelAndView exportXls(HttpServletRequest request, FtpFileMain ftpFileMain) {
        return super.exportXls(request, ftpFileMain, FtpFileMain.class, "ftp附件索引表");
    }

    /**
      * 通过excel导入数据
    *
    * @param request
    * @param response
    * @return
    */
    @RequiresPermissions("pcset:ftp_file_main:importExcel")
    @RequestMapping(value = "/importExcel", method = RequestMethod.POST)
    public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
        return super.importExcel(request, response, FtpFileMain.class);
    }

}
