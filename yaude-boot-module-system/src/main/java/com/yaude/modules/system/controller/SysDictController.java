package com.yaude.modules.system.controller;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.common.constant.CacheConstant;
import com.yaude.common.constant.CommonConstant;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.common.system.vo.DictModel;
import com.yaude.common.system.vo.DictQuery;
import com.yaude.common.system.vo.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import com.yaude.common.api.vo.Result;
import com.yaude.common.util.ImportExcelUtil;
import com.yaude.common.util.SqlInjectionUtil;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.SysDict;
import com.yaude.modules.system.entity.SysDictItem;
import com.yaude.modules.system.model.SysDictTree;
import com.yaude.modules.system.model.TreeSelectModel;
import com.yaude.modules.system.service.ISysDictItemService;
import com.yaude.modules.system.service.ISysDictService;
import com.yaude.modules.system.vo.SysDictPage;
import org.jeecgframework.poi.excel.ExcelImportCheckUtil;
import org.jeecgframework.poi.excel.ExcelImportUtil;
import org.jeecgframework.poi.excel.def.NormalExcelConstants;
import org.jeecgframework.poi.excel.entity.ExportParams;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.jeecgframework.poi.excel.view.JeecgEntityExcelView;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.*;

/**
 * <p>
 * 字典表 前端控制器
 * </p>
 *
 * @Author zhangweijian
 * @since 2018-12-28
 */
@RestController
@RequestMapping("/sys/dict")
@Slf4j
public class SysDictController {

	@Autowired
	private ISysDictService sysDictService;
	@Autowired
	private ISysDictItemService sysDictItemService;
	@Autowired
	public RedisTemplate<String, Object> redisTemplate;

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysDict>> queryPageList(SysDict sysDict,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		Result<IPage<SysDict>> result = new Result<IPage<SysDict>>();
		QueryWrapper<SysDict> queryWrapper = QueryGenerator.initQueryWrapper(sysDict, req.getParameterMap());
		Page<SysDict> page = new Page<SysDict>(pageNo, pageSize);
		IPage<SysDict> pageList = sysDictService.page(page, queryWrapper);
		log.debug("查詢當前頁："+pageList.getCurrent());
		log.debug("查詢當前頁數量："+pageList.getSize());
		log.debug("查詢結果數量："+pageList.getRecords().size());
		log.debug("數據總數："+pageList.getTotal());
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

	/**
	 * @功能：獲取樹形字典數據
	 * @param sysDict
	 * @param pageNo
	 * @param pageSize
	 * @param req
	 * @return
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/treeList", method = RequestMethod.GET)
	public Result<List<SysDictTree>> treeList(SysDict sysDict,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		Result<List<SysDictTree>> result = new Result<>();
		LambdaQueryWrapper<SysDict> query = new LambdaQueryWrapper<>();
		// 構造查詢條件
		String dictName = sysDict.getDictName();
		if(oConvertUtils.isNotEmpty(dictName)) {
			query.like(true, SysDict::getDictName, dictName);
		}
		query.orderByDesc(true, SysDict::getCreateTime);
		List<SysDict> list = sysDictService.list(query);
		List<SysDictTree> treeList = new ArrayList<>();
		for (SysDict node : list) {
			treeList.add(new SysDictTree(node));
		}
		result.setSuccess(true);
		result.setResult(treeList);
		return result;
	}

	/**
	 * 獲取全部字典數據
	 *
	 * @return
	 */
	@RequestMapping(value = "/queryAllDictItems", method = RequestMethod.GET)
	public Result<?> queryAllDictItems(HttpServletRequest request) {
		Map<String, List<DictModel>> res = new HashMap<String, List<DictModel>>();
		res = sysDictService.queryAllDictItems();
		return Result.ok(res);
	}

	/**
	 * 獲取字典數據
	 * @param dictCode
	 * @return
	 */
	@RequestMapping(value = "/getDictText/{dictCode}/{key}", method = RequestMethod.GET)
	public Result<String> getDictText(@PathVariable("dictCode") String dictCode, @PathVariable("key") String key) {
		log.info(" dictCode : "+ dictCode);
		Result<String> result = new Result<String>();
		String text = null;
		try {
			text = sysDictService.queryDictTextByKey(dictCode, key);
			 result.setSuccess(true);
			 result.setResult(text);
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失敗");
			return result;
		}
		return result;
	}


	/**
	 * 獲取字典數據 【接口簽名驗證】
	 * @param dictCode 字典code
	 * @param dictCode 表名,文本字段,code字段  | 舉例：sys_user,realname,id
	 * @return
	 */
	@RequestMapping(value = "/getDictItems/{dictCode}", method = RequestMethod.GET)
	public Result<List<DictModel>> getDictItems(@PathVariable String dictCode, @RequestParam(value = "sign",required = false) String sign,HttpServletRequest request) {
		log.info(" dictCode : "+ dictCode);
		Result<List<DictModel>> result = new Result<List<DictModel>>();
		try {
			List<DictModel> ls = sysDictService.getDictItems(dictCode);
			if (ls == null) {
				result.error500("字典Code格式不正確！");
				return result;
			}
			result.setSuccess(true);
			result.setResult(ls);
			log.debug(result.toString());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			result.error500("操作失敗");
			return result;
		}
		return result;
	}

	/**
	 * 【接口簽名驗證】
	 * 【JSearchSelectTag下拉搜索組件專用接口】
	 * 大數據量的字典表 走異步加載  即前端輸入內容過濾數據
	 * @param dictCode 字典code格式：table,text,code
	 * @return
	 */
	@RequestMapping(value = "/loadDict/{dictCode}", method = RequestMethod.GET)
	public Result<List<DictModel>> loadDict(@PathVariable String dictCode,
			@RequestParam(name="keyword") String keyword,
			@RequestParam(value = "sign",required = false) String sign,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {
		log.info(" 加載字典表數據,加載關鍵字: "+ keyword);
		Result<List<DictModel>> result = new Result<List<DictModel>>();
		try {
			List<DictModel> ls = sysDictService.loadDict(dictCode, keyword, pageSize);
			if (ls == null) {
				result.error500("字典Code格式不正確！");
				return result;
			}
			result.setSuccess(true);
			result.setResult(ls);
			log.info(result.toString());
			return result;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失敗");
			return result;
		}
	}

	/**
	 * 【接口簽名驗證】
	 * 【給表單設計器的表字典使用】下拉搜索模式，有值時動態拼接數據
	 * @param dictCode
	 * @param keyword 當前控件的值，可以逗號分割
	 * @param sign
	 * @param pageSize
	 * @return
	 */
	@RequestMapping(value = "/loadDictOrderByValue/{dictCode}", method = RequestMethod.GET)
	public Result<List<DictModel>> loadDictOrderByValue(
			@PathVariable String dictCode,
			@RequestParam(name = "keyword") String keyword,
			@RequestParam(value = "sign", required = false) String sign,
			@RequestParam(value = "pageSize", required = false) Integer pageSize) {
		// 首次查詢查出來用戶選中的值，并且不分頁
		Result<List<DictModel>> firstRes = this.loadDict(dictCode, keyword, sign, null);
		if (!firstRes.isSuccess()) {
			return firstRes;
		}
		// 然后再查詢出第一頁的數據
		Result<List<DictModel>> result = this.loadDict(dictCode, "", sign, pageSize);
		if (!result.isSuccess()) {
			return result;
		}
		// 合并兩次查詢的數據
		List<DictModel> firstList = firstRes.getResult();
		List<DictModel> list = result.getResult();
		for (DictModel firstItem : firstList) {
			// anyMatch 表示：判斷的條件里，任意一個元素匹配成功，返回true
			// allMatch 表示：判斷條件里的元素，所有的都匹配成功，返回true
			// noneMatch 跟 allMatch 相反，表示：判斷條件里的元素，所有的都匹配失敗，返回true
			boolean none = list.stream().noneMatch(item -> item.getValue().equals(firstItem.getValue()));
			// 當元素不存在時，再添加到集合里
			if (none) {
				list.add(0, firstItem);
			}
		}
		return result;
	}

	/**
	 * 【接口簽名驗證】
	 * 根據字典code加載字典text 返回
	 * @param dictCode 順序：tableName,text,code
	 * @param keys 要查詢的key
	 * @param sign
	 * @param delNotExist 是否移除不存在的項，默認為true，設為false如果某個key不存在數據庫中，則直接返回key本身
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/loadDictItem/{dictCode}", method = RequestMethod.GET)
	public Result<List<String>> loadDictItem(@PathVariable String dictCode,@RequestParam(name="key") String keys, @RequestParam(value = "sign",required = false) String sign,@RequestParam(value = "delNotExist",required = false,defaultValue = "true") boolean delNotExist,HttpServletRequest request) {
		Result<List<String>> result = new Result<>();
		try {
			if(dictCode.indexOf(",")!=-1) {
				String[] params = dictCode.split(",");
				if(params.length!=3) {
					result.error500("字典Code格式不正確！");
					return result;
				}
				List<String> texts = sysDictService.queryTableDictByKeys(params[0], params[1], params[2], keys, delNotExist);

				result.setSuccess(true);
				result.setResult(texts);
				log.info(result.toString());
			}else {
				result.error500("字典Code格式不正確！");
			}
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失敗");
			return result;
		}

		return result;
	}

	/**
	 * 【接口簽名驗證】
	 * 根據表名——顯示字段-存儲字段 pid 加載樹形數據
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/loadTreeData", method = RequestMethod.GET)
	public Result<List<TreeSelectModel>> loadTreeData(@RequestParam(name="pid") String pid,@RequestParam(name="pidField") String pidField,
												  @RequestParam(name="tableName") String tbname,
												  @RequestParam(name="text") String text,
												  @RequestParam(name="code") String code,
												  @RequestParam(name="hasChildField") String hasChildField,
												  @RequestParam(name="condition") String condition,
												  @RequestParam(value = "sign",required = false) String sign,HttpServletRequest request) {
		Result<List<TreeSelectModel>> result = new Result<List<TreeSelectModel>>();
		Map<String, String> query = null;
		if(oConvertUtils.isNotEmpty(condition)) {
			query = JSON.parseObject(condition, Map.class);
		}
		// SQL注入漏洞 sign簽名校驗(表名,label字段,val字段,條件)
		String dictCode = tbname+","+text+","+code+","+condition;
        SqlInjectionUtil.filterContent(dictCode);
		List<TreeSelectModel> ls = sysDictService.queryTreeList(query,tbname, text, code, pidField, pid,hasChildField);
		result.setSuccess(true);
		result.setResult(ls);
		return result;
	}

	/**
	 * 【APP接口】根據字典配置查詢表字典數據（目前暫未找到調用的地方）
	 * @param query
	 * @param pageNo
	 * @param pageSize
	 * @return
	 */
	@Deprecated
	@GetMapping("/queryTableData")
	public Result<List<DictModel>> queryTableData(DictQuery query,
												  @RequestParam(name = "pageNo", defaultValue = "1") Integer pageNo,
												  @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
												  @RequestParam(value = "sign",required = false) String sign, HttpServletRequest request){
		Result<List<DictModel>> res = new Result<List<DictModel>>();
		// SQL注入漏洞 sign簽名校驗
		String dictCode = query.getTable()+","+query.getText()+","+query.getCode();
        SqlInjectionUtil.filterContent(dictCode);
		List<DictModel> ls = this.sysDictService.queryDictTablePageList(query,pageSize,pageNo);
		res.setResult(ls);
		res.setSuccess(true);
		return res;
	}

	/**
	 * @功能：新增
	 * @param sysDict
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public Result<SysDict> add(@RequestBody SysDict sysDict) {
		Result<SysDict> result = new Result<SysDict>();
		try {
			sysDict.setCreateTime(new Date());
			sysDict.setDelFlag(CommonConstant.DEL_FLAG_0);
			sysDictService.save(sysDict);
			result.success("保存成功！");
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			result.error500("操作失敗");
		}
		return result;
	}

	/**
	 * @功能：編輯
	 * @param sysDict
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/edit", method = RequestMethod.PUT)
	public Result<SysDict> edit(@RequestBody SysDict sysDict) {
		Result<SysDict> result = new Result<SysDict>();
		SysDict sysdict = sysDictService.getById(sysDict.getId());
		if(sysdict==null) {
			result.error500("未找到對應實體");
		}else {
			sysDict.setUpdateTime(new Date());
			boolean ok = sysDictService.updateById(sysDict);
			if(ok) {
				result.success("編輯成功!");
			}
		}
		return result;
	}

	/**
	 * @功能：刪除
	 * @param id
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/delete", method = RequestMethod.DELETE)
	@CacheEvict(value={CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries=true)
	public Result<SysDict> delete(@RequestParam(name="id",required=true) String id) {
		Result<SysDict> result = new Result<SysDict>();
		boolean ok = sysDictService.removeById(id);
		if(ok) {
			result.success("刪除成功!");
		}else{
			result.error500("刪除失敗!");
		}
		return result;
	}

	/**
	 * @功能：批量刪除
	 * @param ids
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
	@CacheEvict(value= {CacheConstant.SYS_DICT_CACHE, CacheConstant.SYS_ENABLE_DICT_CACHE}, allEntries=true)
	public Result<SysDict> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
		Result<SysDict> result = new Result<SysDict>();
		if(oConvertUtils.isEmpty(ids)) {
			result.error500("參數不識別！");
		}else {
			sysDictService.removeByIds(Arrays.asList(ids.split(",")));
			result.success("刪除成功!");
		}
		return result;
	}

	/**
	 * @功能：刷新緩存
	 * @return
	 */
	@RequestMapping(value = "/refleshCache")
	public Result<?> refleshCache() {
		Result<?> result = new Result<SysDict>();
		//清空字典緩存
		Set keys = redisTemplate.keys(CacheConstant.SYS_DICT_CACHE + "*");
		Set keys7 = redisTemplate.keys(CacheConstant.SYS_ENABLE_DICT_CACHE + "*");
		Set keys8 = redisTemplate.keys(CacheConstant.SYS_DICT_ALLTABLE_CACHE + "*");
		Set keys2 = redisTemplate.keys(CacheConstant.SYS_DICT_TABLE_CACHE + "*");
		Set keys21 = redisTemplate.keys(CacheConstant.SYS_DICT_TABLE_BY_KEYS_CACHE + "*");
		Set keys3 = redisTemplate.keys(CacheConstant.SYS_DEPARTS_CACHE + "*");
		Set keys4 = redisTemplate.keys(CacheConstant.SYS_DEPART_IDS_CACHE + "*");
		Set keys5 = redisTemplate.keys( "jmreport:cache:dict*");
		Set keys6 = redisTemplate.keys( "jmreport:cache:dictTable*");

		redisTemplate.delete(keys);
		redisTemplate.delete(keys2);
		redisTemplate.delete(keys21);
		redisTemplate.delete(keys3);
		redisTemplate.delete(keys4);
		redisTemplate.delete(keys5);
		redisTemplate.delete(keys6);
		redisTemplate.delete(keys7);
		redisTemplate.delete(keys8);
		return result;
	}

	/**
	 * 導出excel
	 *
	 * @param request
	 */
	@RequestMapping(value = "/exportXls")
	public ModelAndView exportXls(SysDict sysDict,HttpServletRequest request) {
		// Step.1 組裝查詢條件
		QueryWrapper<SysDict> queryWrapper = QueryGenerator.initQueryWrapper(sysDict, request.getParameterMap());
		//Step.2 AutoPoi 導出Excel
		ModelAndView mv = new ModelAndView(new JeecgEntityExcelView());
		List<SysDictPage> pageList = new ArrayList<SysDictPage>();

		List<SysDict> sysDictList = sysDictService.list(queryWrapper);
		for (SysDict dictMain : sysDictList) {
			SysDictPage vo = new SysDictPage();
			BeanUtils.copyProperties(dictMain, vo);
			// 查詢機票
			List<SysDictItem> sysDictItemList = sysDictItemService.selectItemsByMainId(dictMain.getId());
			vo.setSysDictItemList(sysDictItemList);
			pageList.add(vo);
		}

		// 導出文件名稱
		mv.addObject(NormalExcelConstants.FILE_NAME, "數據字典");
		// 注解對象Class
		mv.addObject(NormalExcelConstants.CLASS, SysDictPage.class);
		// 自定義表格參數
		LoginUser user = (LoginUser) SecurityUtils.getSubject().getPrincipal();
		mv.addObject(NormalExcelConstants.PARAMS, new ExportParams("數據字典列表", "導出人:"+user.getRealname(), "數據字典"));
		// 導出數據列表
		mv.addObject(NormalExcelConstants.DATA_LIST, pageList);
		return mv;
	}

	/**
	 * 通過excel導入數據
	 *
	 * @param request
	 * @param
	 * @return
	 */
	//@RequiresRoles({"admin"})
	@RequestMapping(value = "/importExcel", method = RequestMethod.POST)
	public Result<?> importExcel(HttpServletRequest request, HttpServletResponse response) {
 		MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
		Map<String, MultipartFile> fileMap = multipartRequest.getFileMap();
		for (Map.Entry<String, MultipartFile> entity : fileMap.entrySet()) {
			MultipartFile file = entity.getValue();// 獲取上傳文件對象
			ImportParams params = new ImportParams();
			params.setTitleRows(2);
			params.setHeadRows(2);
			params.setNeedSave(true);
			try {
				//導入Excel格式校驗，看匹配的字段文本概率
				Boolean t = ExcelImportCheckUtil.check(file.getInputStream(), SysDictPage.class, params);
				if(!t){
					throw new RuntimeException("導入Excel校驗失敗 ！");
				}
				List<SysDictPage> list = ExcelImportUtil.importExcel(file.getInputStream(), SysDictPage.class, params);
				// 錯誤信息
				List<String> errorMessage = new ArrayList<>();
				int successLines = 0, errorLines = 0;
				for (int i=0;i< list.size();i++) {
					SysDict po = new SysDict();
					BeanUtils.copyProperties(list.get(i), po);
					po.setDelFlag(CommonConstant.DEL_FLAG_0);
					try {
						Integer integer = sysDictService.saveMain(po, list.get(i).getSysDictItemList());
						if(integer>0){
							successLines++;
						}else{
							errorLines++;
							int lineNumber = i + 1;
							errorMessage.add("第 " + lineNumber + " 行：字典編碼已經存在，忽略導入。");
						}
					}  catch (Exception e) {
						errorLines++;
						int lineNumber = i + 1;
						errorMessage.add("第 " + lineNumber + " 行：字典編碼已經存在，忽略導入。");
					}
				}
				return ImportExcelUtil.imporReturnRes(errorLines,successLines,errorMessage);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
				return Result.error("文件導入失敗:"+e.getMessage());
			} finally {
				try {
					file.getInputStream().close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return Result.error("文件導入失敗！");
	}


	/**
	 * 查詢被刪除的列表
	 * @return
	 */
	@RequestMapping(value = "/deleteList", method = RequestMethod.GET)
	public Result<List<SysDict>> deleteList() {
		Result<List<SysDict>> result = new Result<List<SysDict>>();
		List<SysDict> list = this.sysDictService.queryDeleteList();
		result.setSuccess(true);
		result.setResult(list);
		return result;
	}

	/**
	 * 物理刪除
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/deletePhysic/{id}", method = RequestMethod.DELETE)
	public Result<?> deletePhysic(@PathVariable String id) {
		try {
			sysDictService.deleteOneDictPhysically(id);
			return Result.ok("刪除成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("刪除失敗!");
		}
	}

	/**
	 * 邏輯刪除的字段，進行取回
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "/back/{id}", method = RequestMethod.PUT)
	public Result<?> back(@PathVariable String id) {
		try {
			sysDictService.updateDictDelFlag(0,id);
			return Result.ok("操作成功!");
		} catch (Exception e) {
			e.printStackTrace();
			return Result.error("操作失敗!");
		}
	}

}
