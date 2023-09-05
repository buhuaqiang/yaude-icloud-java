package com.yaude.icloud.openstack.controller;

import com.yaude.common.api.vo.Result;
import com.yaude.common.aspect.annotation.AutoLog;
import com.yaude.common.system.base.controller.JeecgController;
import com.yaude.icloud.openstack.entity.OsInstance;
import com.yaude.icloud.openstack.service.IOsInstanceService;
import com.yaude.icloud.openstack.vo.OsInstanceVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.openstack4j.model.common.ActionResponse;
import org.openstack4j.model.compute.Limits;
import org.openstack4j.model.compute.SimpleTenantUsage;
import org.openstack4j.model.network.NetFloatingIP;
import org.openstack4j.model.storage.block.Volume;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description: 申請明細檔
 * @Author: jeecg-boot
 * @Date:   2021-09-24
 * @Version: V1.0
 */
@Api(tags="申請明細檔")
@RestController
@RequestMapping("/openstack/projectSurvey")
@Slf4j
public class ProjectSurveyController extends JeecgController<OsInstance, IOsInstanceService> {
    @Autowired
    private IOsInstanceService osInstanceService;

    /**
     * 分頁列表查詢
     *
     * @param osInstanceVo
     * @param pageNo
     * @param pageSize
     * @param req
     * @return
     */
    @AutoLog(value = "申請明細檔-分頁列表查詢")
    @ApiOperation(value="申請明細檔-分頁列表查詢", notes="申請明細檔-分頁列表查詢")
    @GetMapping(value = "/list")
    public Result<?> queryPageList(OsInstanceVo osInstanceVo,
                                   @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                   @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                   HttpServletRequest req) {
        SimpleTenantUsage tenantUsage = osInstanceService.getTenantUsage(osInstanceVo);
        return Result.OK(tenantUsage);
    }


    /**
     *   獲取項目資源使用概況
     * @param osInstanceVo
     * @return
     */
    @AutoLog(value = "獲取項目資源使用概況")
    @ApiOperation(value="獲取項目資源使用概況", notes="獲取項目資源使用概況")
    @GetMapping(value = "/getProjectLimits")
    public Result<?> getProjectLimits(OsInstanceVo osInstanceVo) {
        Limits projectLimits = osInstanceService.getProjectLimits(osInstanceVo);
        return Result.OK(projectLimits);
    }








}
