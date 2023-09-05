package com.yaude.icloud.licensePackage.controller;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.net.URLDecoder;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.yaude.common.api.vo.Result;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.util.oConvertUtils;
import com.yaude.icloud.licensePackage.entity.SysLicense;
import com.yaude.icloud.licensePackage.service.ISysLicenseService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import com.yaude.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.yaude.common.aspect.annotation.AutoLog;

/**
 * @Description: 認證證書
 * @Author: jeecg-boot
 * @Date:   2021-11-04
 * @Version: V1.0
 */
@Api(tags="認證證書")
@RestController
@RequestMapping("/licensePackage/sysLicense")
@Slf4j
public class SysLicenseController extends JeecgController<SysLicense, ISysLicenseService> {
	@Autowired
	private ISysLicenseService sysLicenseService;

	/**
	 * 證書生成路徑
	 */
	@Value("${license.licensePaths}")
	private String licensePaths;
	/**
	 * 分頁列表查詢
	 *
	 * @param sysLicense
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "認證證書-分頁列表查詢")
	@ApiOperation(value="認證證書-分頁列表查詢", notes="認證證書-分頁列表查詢")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(SysLicense sysLicense,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<SysLicense> queryWrapper = QueryGenerator.initQueryWrapper(sysLicense, req.getParameterMap());
		Page<SysLicense> page = new Page<SysLicense>(pageNo, pageSize);
		IPage<SysLicense> pageList = sysLicenseService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param sysLicense
	 * @return
	 */
	@AutoLog(value = "認證證書-添加")
	@ApiOperation(value="認證證書-添加", notes="認證證書-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody SysLicense sysLicense) {
		String name = sysLicense.getSubject();
		sysLicense.setConsumerAmount(1);
		sysLicense.setLicensePath(name+"_license.lic");
		sysLicenseService.save(sysLicense);
		return Result.OK("添加成功！");
	}

	/**
	 *  編輯
	 *
	 * @param sysLicense
	 * @return
	 */
	@AutoLog(value = "認證證書-編輯")
	@ApiOperation(value="認證證書-編輯", notes="認證證書-編輯")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody SysLicense sysLicense) {
		sysLicenseService.updateById(sysLicense);
		return Result.OK("編輯成功!");
	}

	/**
	 *   通過id刪除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "認證證書-通過id刪除")
	@ApiOperation(value="認證證書-通過id刪除", notes="認證證書-通過id刪除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		sysLicenseService.removeById(id);
		return Result.OK("刪除成功!");
	}

	/**
	 *  批量刪除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "認證證書-批量刪除")
	@ApiOperation(value="認證證書-批量刪除", notes="認證證書-批量刪除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.sysLicenseService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量刪除成功!");
	}

	/**
	 * 通過id查詢
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "認證證書-通過id查詢")
	@ApiOperation(value="認證證書-通過id查詢", notes="認證證書-通過id查詢")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		SysLicense sysLicense = sysLicenseService.getById(id);
		if(sysLicense==null) {
			return Result.error("未找到對應數據");
		}
		return Result.OK(sysLicense);
	}


	/**
	 * 下載認證
	 * @param request
	 * @param response
	 */
	@GetMapping(value = "/downloadCertificate")
	public void view(SysLicense sysLicense,HttpServletRequest request, HttpServletResponse response) {

		// 其餘處理略
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			String filePath = licensePaths+sysLicense.getLicensePath();
			File file = new File(filePath);
			if(!file.exists()){
				response.setStatus(404);
				throw new RuntimeException("文件不存在..");
			}
			response.setContentType("application/force-download");// 設置強制下載不打開
			response.addHeader("Content-Disposition", "attachment;fileName=" + new String(file.getName().getBytes("UTF-8"),"iso-8859-1"));
			inputStream = new BufferedInputStream(new FileInputStream(filePath));
			outputStream = response.getOutputStream();
			byte[] buf = new byte[1024];
			int len;
			while ((len = inputStream.read(buf)) > 0) {
				outputStream.write(buf, 0, len);
			}
			response.flushBuffer();
		} catch (IOException e) {
			log.error("預覽文件失敗" + e.getMessage());
			response.setStatus(404);
			e.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
			if (outputStream != null) {
				try {
					outputStream.close();
				} catch (IOException e) {
					log.error(e.getMessage(), e);
				}
			}
		}

	}





	/**
	 * 導出excel
	 *
	 * @param request
	 * @param sysLicense
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, SysLicense sysLicense) {
		return super.exportXls(request, sysLicense, SysLicense.class, "認證證書");
	}

	/**
	 * 通過excel導入數據
	 *
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
		return super.importExcel(request, response, SysLicense.class);
	}

}
