package com.yaude.modules.system.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.common.system.query.QueryGenerator;
import lombok.extern.slf4j.Slf4j;
import com.yaude.common.api.vo.Result;
import com.yaude.common.aspect.annotation.PermissionData;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.SysTenant;
import com.yaude.modules.system.service.ISysTenantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

/**
 * 租戶配置信息
 */
@Slf4j
@RestController
@RequestMapping("/sys/tenant")
public class SysTenantController {

    @Autowired
    private ISysTenantService sysTenantService;

    /**
     * 獲取列表數據
     * @param sysTenant
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @PermissionData(pageComponent = "system/TenantList")
	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public Result<IPage<SysTenant>> queryPageList(SysTenant sysTenant,@RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
									  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,HttpServletRequest req) {
		Result<IPage<SysTenant>> result = new Result<IPage<SysTenant>>();
		QueryWrapper<SysTenant> queryWrapper = QueryGenerator.initQueryWrapper(sysTenant, req.getParameterMap());
		Page<SysTenant> page = new Page<SysTenant>(pageNo, pageSize);
		IPage<SysTenant> pageList = sysTenantService.page(page, queryWrapper);
		result.setSuccess(true);
		result.setResult(pageList);
		return result;
	}

    /**
     *   添加
     * @param
     * @return
     */
    @RequestMapping(value = "/add", method = RequestMethod.POST)
    public Result<SysTenant> add(@RequestBody SysTenant sysTenant) {
        Result<SysTenant> result = new Result<SysTenant>();
        if(sysTenantService.getById(sysTenant.getId())!=null){
            return result.error500("該編號已存在!");
        }
        try {
            sysTenantService.save(sysTenant);
            result.success("添加成功！");
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.error500("操作失敗");
        }
        return result;
    }

    /**
     *  編輯
     * @param
     * @return
     */
    @RequestMapping(value = "/edit", method = RequestMethod.PUT)
    public Result<SysTenant> edit(@RequestBody SysTenant tenant) {
        Result<SysTenant> result = new Result<SysTenant>();
        SysTenant sysTenant = sysTenantService.getById(tenant.getId());
        if(sysTenant==null) {
            result.error500("未找到對應實體");
        }else {
            boolean ok = sysTenantService.updateById(tenant);
            if(ok) {
                result.success("修改成功!");
            }
        }
        return result;
    }

    /**
     *   通過id刪除
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.DELETE)
    public Result<?> delete(@RequestParam(name="id",required=true) String id) {
        sysTenantService.removeTenantById(id);
        return Result.ok("刪除成功");
    }

    /**
     *  批量刪除
     * @param ids
     * @return
     */
    @RequestMapping(value = "/deleteBatch", method = RequestMethod.DELETE)
    public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
        Result<?> result = new Result<>();
        if(oConvertUtils.isEmpty(ids)) {
            result.error500("未選中租戶！");
        }else {
            String[] ls = ids.split(",");
            // 過濾掉已被引用的租戶
            List<String> idList = new ArrayList<>();
            for (String id : ls) {
                int userCount = sysTenantService.countUserLinkTenant(id);
                if (userCount == 0) {
                    idList.add(id);
                }
            }
            if (idList.size() > 0) {
                sysTenantService.removeByIds(idList);
                if (ls.length == idList.size()) {
                    result.success("刪除成功！");
                } else {
                    result.success("部分刪除成功！（被引用的租戶無法刪除）");
                }
            }else  {
                result.error500("選擇的租戶都已被引用，無法刪除！");
            }
        }
        return result;
    }

    /**
     * 通過id查詢
     * @param id
     * @return
     */
    @RequestMapping(value = "/queryById", method = RequestMethod.GET)
    public Result<SysTenant> queryById(@RequestParam(name="id",required=true) String id) {
        Result<SysTenant> result = new Result<SysTenant>();
        SysTenant sysTenant = sysTenantService.getById(id);
        if(sysTenant==null) {
            result.error500("未找到對應實體");
        }else {
            result.setResult(sysTenant);
            result.setSuccess(true);
        }
        return result;
    }


    /**
     * 查詢有效的 租戶數據
     * @return
     */
    @RequestMapping(value = "/queryList", method = RequestMethod.GET)
    public Result<List<SysTenant>> queryList(@RequestParam(name="ids",required=false) String ids) {
        Result<List<SysTenant>> result = new Result<List<SysTenant>>();
        LambdaQueryWrapper<SysTenant> query = new LambdaQueryWrapper<>();
        query.eq(SysTenant::getStatus, 1);
        if(oConvertUtils.isNotEmpty(ids)){
            query.in(SysTenant::getId, ids.split(","));
        }
        //此處查詢忽略時間條件
        List<SysTenant> ls = sysTenantService.list(query);
        result.setSuccess(true);
        result.setResult(ls);
        return result;
    }
}
