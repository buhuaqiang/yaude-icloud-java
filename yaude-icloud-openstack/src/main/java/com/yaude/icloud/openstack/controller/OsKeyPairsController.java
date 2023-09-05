package com.yaude.icloud.openstack.controller;

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
import com.yaude.icloud.openstack.entity.OsKeyPairs;
import com.yaude.icloud.openstack.service.IOsKeyPairsService;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.icloud.openstack.vo.OsApplyVo;
import lombok.extern.slf4j.Slf4j;

import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import com.yaude.common.system.base.controller.JeecgController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import com.alibaba.fastjson.JSON;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import com.yaude.common.aspect.annotation.AutoLog;

/**
 * @Description: 秘鑰對
 * @Author: jeecg-boot
 * @Date:   2021-10-18
 * @Version: V1.0
 */
@Api(tags="秘鑰對")
@RestController
@RequestMapping("/openstack/osKeyPairs")
@Slf4j
public class OsKeyPairsController extends JeecgController<OsKeyPairs, IOsKeyPairsService> {
	@Autowired
	private IOsKeyPairsService osKeyPairsService;

	/**
	 * 分頁列表查詢
	 *
	 * @param osKeyPairs
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@AutoLog(value = "秘鑰對-分頁列表查詢")
	@ApiOperation(value="秘鑰對-分頁列表查詢", notes="秘鑰對-分頁列表查詢")
	@GetMapping(value = "/list")
	public Result<?> queryPageList(OsKeyPairs osKeyPairs,
								   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
								   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
								   HttpServletRequest req) {
		QueryWrapper<OsKeyPairs> queryWrapper = QueryGenerator.initQueryWrapper(osKeyPairs, req.getParameterMap());
		Page<OsKeyPairs> page = new Page<OsKeyPairs>(pageNo, pageSize);
		IPage<OsKeyPairs> pageList = osKeyPairsService.page(page, queryWrapper);
		return Result.OK(pageList);
	}

	/**
	 *   添加
	 *
	 * @param osKeyPairs
	 * @return
	 */
	@AutoLog(value = "秘鑰對-添加")
	@ApiOperation(value="秘鑰對-添加", notes="秘鑰對-添加")
	@PostMapping(value = "/add")
	public Result<?> add(@RequestBody OsKeyPairs osKeyPairs) {
		//osKeyPairsService.save(osKeyPairs);
		OsKeyPairs KeyPairs = osKeyPairsService.getkey(osKeyPairs);
		osKeyPairsService.save(KeyPairs);

		return Result.OK("添加成功！");
	}

	/**
	 *  編輯
	 *
	 * @param osKeyPairs
	 * @return
	 */
	@AutoLog(value = "秘鑰對-編輯")
	@ApiOperation(value="秘鑰對-編輯", notes="秘鑰對-編輯")
	@PutMapping(value = "/edit")
	public Result<?> edit(@RequestBody OsKeyPairs osKeyPairs) {
		osKeyPairsService.updateById(osKeyPairs);
		return Result.OK("編輯成功!");
	}


	/**
	 *  根據userid獲取項目
	 *
	 * @param osKeyPairs
	 * @return
	 */
	@AutoLog(value = "獲取項目")
	@ApiOperation(value="獲取項目", notes="獲取項目")
	@PostMapping(value = "/getProject")
	public Result<?> getProject(@RequestBody OsKeyPairs osKeyPairs) {
		List<OsKeyPairs> osKeyPairses = osKeyPairsService.getProjects();
		return Result.OK(osKeyPairses);
	}


	/**
	 *   通過id刪除
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "秘鑰對-通過id刪除")
	@ApiOperation(value="秘鑰對-通過id刪除", notes="秘鑰對-通過id刪除")
	@DeleteMapping(value = "/delete")
	public Result<?> delete(@RequestParam(name="id",required=true) String id) {
		osKeyPairsService.removeById(id);
		return Result.OK("刪除成功!");
	}

	/**
	 *  批量刪除
	 *
	 * @param ids
	 * @return
	 */
	@AutoLog(value = "秘鑰對-批量刪除")
	@ApiOperation(value="秘鑰對-批量刪除", notes="秘鑰對-批量刪除")
	@DeleteMapping(value = "/deleteBatch")
	public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		this.osKeyPairsService.removeByIds(Arrays.asList(ids.split(",")));
		return Result.OK("批量刪除成功!");
	}

	/**
	 * 通過id查詢
	 *
	 * @param id
	 * @return
	 */
	@AutoLog(value = "秘鑰對-通過id查詢")
	@ApiOperation(value="秘鑰對-通過id查詢", notes="秘鑰對-通過id查詢")
	@GetMapping(value = "/queryById")
	public Result<?> queryById(@RequestParam(name="id",required=true) String id) {
		OsKeyPairs osKeyPairs = osKeyPairsService.getById(id);
		if(osKeyPairs==null) {
			return Result.error("未找到對應數據");
		}
		return Result.OK(osKeyPairs);
	}

	/**
	 * 導出excel
	 *
	 * @param request
	 * @param osKeyPairs
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, OsKeyPairs osKeyPairs) {
		return super.exportXls(request, osKeyPairs, OsKeyPairs.class, "秘鑰對");
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
		return super.importExcel(request, response, OsKeyPairs.class);
	}



	/**
	 * 下載秘鑰
	 *
	 * @param request
	 * @param response
	 */
	@GetMapping(value = "/getPrivateKey")
	public void getPrivateKey(OsKeyPairs osKeyPairs,HttpServletRequest request, HttpServletResponse response) {
		// 其餘處理略
		InputStream inputStream = null;
		OutputStream outputStream = null;
		try {
			response.setContentType("application/force-download");// 設置強制下載不打開
			response.addHeader("Content-Disposition", "attachment;fileName=" + new String(osKeyPairs.getKeyName().getBytes("UTF-8"),"iso-8859-1"));
			inputStream = new ByteArrayInputStream(osKeyPairs.getPrivateKey().getBytes());
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

}
