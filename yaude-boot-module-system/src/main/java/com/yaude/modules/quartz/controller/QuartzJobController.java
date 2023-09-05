package com.yaude.modules.quartz.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.common.api.vo.Result;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.util.ImportExcelUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import com.yaude.modules.quartz.entity.QuartzJob;
import com.yaude.modules.quartz.service.IQuartzJobService;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Description: 定時任務在線管理
 * @Author: jeecg-boot
 * @Date: 2019-01-02
 * @Version:V1.0
 */
@RestController
@RequestMapping("/sys/quartzJob")
@Slf4j
@Api(tags = "定時任務接口")
public class QuartzJobController {
	@Autowired
	private IQuartzJobService quartzJobService;
	@Autowired
	private Scheduler scheduler;

	/**
	 * 分頁列表查詢
	 * 
	 * @param quartzJob
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<?> queryPageList(QuartzJob quartzJob, @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
								   @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize, HttpServletRequest req) {
		QueryWrapper<QuartzJob> queryWrapper = QueryGenerator.initQueryWrapper(quartzJob, req.getParameterMap());
		Page<QuartzJob> page = new Page<QuartzJob>(pageNo, pageSize);
		IPage<QuartzJob> pageList = quartzJobService.page(page, queryWrapper);
        return Result.ok(pageList);

	}

	/**
	 * 添加定時任務
	 * 
	 * @param quartzJob
	 * @return
	 */
	//@RequiresRoles("admin")
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Result<?> add(@RequestBody QuartzJob quartzJob) {
		quartzJobService.saveAndScheduleJob(quartzJob);
		return Result.ok("創建定時任務成功");
	}

	/**
	 * 更新定時任務
	 * 
	 * @param quartzJob
	 * @return
	 */
	//@RequiresRoles("admin")
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	public Result<?> eidt(@RequestBody QuartzJob quartzJob) {
		try {
			quartzJobService.editAndScheduleJob(quartzJob);
		} catch (SchedulerException e) {
			log.error(e.getMessage(),e);
			return Result.error("更新定時任務失敗!");
		}
	    return Result.ok("更新定時任務成功!");
	}

	/**
	 * 通過id刪除
	 * 
	 * @param id
	 * @return
	 */
	//@RequiresRoles("admin")
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	public Result<?> delete(@RequestParam(name = "id", required = true) String id) {
		QuartzJob quartzJob = quartzJobService.getById(id);
		if (quartzJob == null) {
			return Result.error("未找到對應實體");
		}
		quartzJobService.deleteAndStopJob(quartzJob);
        return Result.ok("刪除成功!");

	}

	/**
	 * 批量刪除
	 * 
	 * @param ids
	 * @return
	 */
	//@RequiresRoles("admin")
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	public Result<?> deleteBatch(@RequestParam(name = "ids", required = true) String ids) {
		if (ids == null || "".equals(ids.trim())) {
			return Result.error("參數不識別！");
		}
		for (String id : Arrays.asList(ids.split(","))) {
			QuartzJob job = quartzJobService.getById(id);
			quartzJobService.deleteAndStopJob(job);
		}
        return Result.ok("刪除定時任務成功!");
	}

	/**
	 * 暫停定時任務
	 * 
	 * @param id
	 * @return
	 */
	//@RequiresRoles("admin")
	@GetMapping(value = "/pause")
	@ApiOperation(value = "暫停定時任務")
	public Result<Object> pauseJob(@RequestParam(name = "id") String id) {
		QuartzJob job = quartzJobService.getById(id);
		if (job == null) {
			return Result.error("定時任務不存在！");
		}
		quartzJobService.pause(job);
		return Result.ok("暫停定時任務成功");
	}

	/**
	 * 啟動定時任務
	 * 
	 * @param id
	 * @return
	 */
	//@RequiresRoles("admin")
	@GetMapping(value = "/resume")
	@ApiOperation(value = "恢復定時任務")
	public Result<Object> resumeJob(@RequestParam(name = "id") String id) {
		QuartzJob job = quartzJobService.getById(id);
		if (job == null) {
			return Result.error("定時任務不存在！");
		}
		quartzJobService.resumeJob(job);
		//scheduler.resumeJob(JobKey.jobKey(job.getJobClassName().trim()));
		return Result.ok("恢復定時任務成功");
	}

	/**
	 * 通過id查詢
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/queryById", method = RequestMethod.GET)
	public Result<?> queryById(@RequestParam(name = "id", required = true) String id) {
		QuartzJob quartzJob = quartzJobService.getById(id);
        return Result.ok(quartzJob);
	}

	/**
	 * 導出excel
	 * 
	 * @param request
	 * @param quartzJob
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(HttpServletRequest request, QuartzJob quartzJob) {
		// Step.1 組裝查詢條件
		QueryWrapper<QuartzJob> queryWrapper = QueryGenerator.initQueryWrapper(quartzJob, request.getParameterMap());
		// Step.2 AutoPoi 導出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<QuartzJob> pageList = quartzJobService.list(queryWrapper);
		// 導出文件名稱
		mv.addObject(NormalExcelConstants.FILE_NAME, "定時任務列表");
		mv.addObject(NormalExcelConstants.CLASS, QuartzJob.class);
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("定時任務列表數據", "導出人:Jeecg", "導出信息"));
		mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
		return mv;
	}

	/**
	 * 通過excel導入數據
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) throws IOException {
		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		// 錯誤信息
		List<String> errorMessage = new ArrayList<>();
		int successLines = 0, errorLines = 0;
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			MultipartFile file = entity.getValue();// 獲取上傳文件對象
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(1);
			params.setNeedSave(true);
			try {
				List<Object> listQuartzJobs = ExcelImportUtil.importExcel(file.getInputStream(), QuartzJob.class, params);
				List<String> list = ImportExcelUtil.importDateSave(listQuartzJobs, IQuartzJobService.class, errorMessage, CommonConstant.SQL_INDEX_UNIQ_JOB_CLASS_NAME);
				errorLines+=list.size();
				successLines+=(listQuartzJobs.size()-errorLines);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				return Result.error("文件導入失敗！");
			} finally {
				try {
					file.getInputStream().close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorMessage);
	}

	/**
	 * 立即執行
	 * @param id
	 * @return
	 */
	//@RequiresRoles("admin")
	@GetMapping("/execute")
	public Result<?> execute(@RequestParam(name = "id", required = true) String id) {
		QuartzJob quartzJob = quartzJobService.getById(id);
		if (quartzJob == null) {
			return Result.error("未找到對應實體");
		}
		try {
			quartzJobService.execute(quartzJob);
		} catch (Exception e) {
			//e.printStackTrace();
			log.info("定時任務 立即執行失敗>>"+e.getMessage());
			return Result.error("執行失敗!");
		}
		return Result.ok("執行成功!");
	}
}
