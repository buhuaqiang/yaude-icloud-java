package com.yaude.icloud.openstack.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.yaude.common.api.vo.Result;
import com.yaude.common.aspect.annotation.AutoLog;
import com.yaude.common.system.base.controller.JeecgController;
import com.yaude.common.system.query.QueryGenerator;
import com.yaude.icloud.openstack.entity.OsApply;
import com.yaude.icloud.openstack.entity.OsUserProject;
import com.yaude.icloud.openstack.service.IOsSecurityGroupService;
import com.yaude.icloud.openstack.service.IOsUserProjectService;
import com.yaude.icloud.openstack.vo.OsInstanceVo;
import com.yaude.icloud.openstack.vo.OsSecurityGroupRuleVo;
import com.yaude.icloud.openstack.vo.OsSecurityGroupVo;
import com.yaude.icloud.openstack.vo.OsUserProjectVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jclouds.openstack.neutron.v2.domain.Rule;
import org.jclouds.openstack.neutron.v2.domain.SecurityGroup;
import org.openstack4j.model.common.ActionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @Description: 用戶 項目關聯表
 * @Author: jeecg-boot
 * @Date:   2021-09-29
 * @Version: V1.0
 */
@Api(tags="用戶 項目關聯表")
@RestController
@RequestMapping("/openstack/securityGroup")
@Slf4j
public class SecurityGroupController {
   @Autowired
   private IOsSecurityGroupService osSecurityGroupService;

   /**
    * 安全組分頁列表查詢
    *
    * @param osSecurityGroupVo
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "安全組分頁列表查詢")
   @ApiOperation(value="安全組分頁列表查詢", notes="安全組分頁列表查詢")
   @GetMapping(value = "/list")
   public Result<?> queryPageList(OsSecurityGroupVo osSecurityGroupVo,
                                  @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                                  @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                                  HttpServletRequest req) {
      List<OsSecurityGroupVo> securityGroupList = new ArrayList<>();
      Page<OsSecurityGroupVo> page = new Page<OsSecurityGroupVo>(pageNo, pageSize);
      if(StringUtils.isNotEmpty(osSecurityGroupVo.getProjectId())){
         securityGroupList = osSecurityGroupService.getSecurityGroupList(osSecurityGroupVo);

      }
      return Result.OK(page.setRecords(securityGroupList));
   }



   /**
    * 安全組規則分頁列表查詢
    *
    * @param osSecurityGroupVo
    * @param pageNo
    * @param pageSize
    * @param req
    * @return
    */
   @AutoLog(value = "安全組規則分頁列表查詢")
   @ApiOperation(value="安全組規則分頁列表查詢", notes="安全組規則分頁列表查詢")
   @GetMapping(value = "/listRules")
   public Result<?> listRules(OsSecurityGroupVo osSecurityGroupVo,
                              @RequestParam(name="pageNo", defaultValue="1") Integer pageNo,
                              @RequestParam(name="pageSize", defaultValue="10") Integer pageSize,
                              HttpServletRequest req) {
      Page<OsSecurityGroupRuleVo> page = new Page<OsSecurityGroupRuleVo>(pageNo, pageSize);
      List<OsSecurityGroupRuleVo> res = new ArrayList<>();
      if(StringUtils.isNotEmpty(osSecurityGroupVo.getProjectId())
              &&StringUtils.isNotEmpty(osSecurityGroupVo.getSecurityGroupId())){
         res = osSecurityGroupService.getSecurityGroupById(osSecurityGroupVo);
      }
      return Result.OK(page.setRecords(res));
   }





   /**
    *   添加
    *
    * @param osUserProject
    * @return
    */
   @AutoLog(value = "用戶 項目關聯表-添加")
   @ApiOperation(value="用戶 項目關聯表-添加", notes="用戶 項目關聯表-添加")
   @PostMapping(value = "/add")
   public Result<?> add(@RequestBody OsUserProject osUserProject) {
      return Result.OK("添加成功！");
   }

   /**
    *  編輯
    *
    * @param osUserProject
    * @return
    */
   @AutoLog(value = "用戶 項目關聯表-編輯")
   @ApiOperation(value="用戶 項目關聯表-編輯", notes="用戶 項目關聯表-編輯")
   @PutMapping(value = "/edit")
   public Result<?> edit(@RequestBody OsUserProject osUserProject) {
      return Result.OK("編輯成功!");
   }




   /**
    *   通過id刪除
    *
    * @param id
    * @return
    */
   @AutoLog(value = "用戶 項目關聯表-通過id刪除")
   @ApiOperation(value="用戶 項目關聯表-通過id刪除", notes="用戶 項目關聯表-通過id刪除")
   @DeleteMapping(value = "/delete")
   public Result<?> delete(@RequestParam(name="id",required=true) String id) {
      return Result.OK("刪除成功!");
   }

   /**
    *  批量刪除
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "用戶 項目關聯表-批量刪除")
   @ApiOperation(value="用戶 項目關聯表-批量刪除", notes="用戶 項目關聯表-批量刪除")
   @DeleteMapping(value = "/deleteBatch")
   public Result<?> deleteBatch(@RequestParam(name="ids",required=true) String ids) {
      //this.osSecurityGroupService.removeByIds(Arrays.asList(ids.split(",")));
      return Result.OK("批量刪除成功!");
   }


   /**
    *   創建安全組
    *
    * @param osSecurityGroupVo
    * @return
    */
   @AutoLog(value = "創建安全組")
   @ApiOperation(value="創建安全組", notes="創建安全組")
   @GetMapping(value = "/createSecurityGroup")
   public Result<?> createSecurityGroup(OsSecurityGroupVo osSecurityGroupVo) {
      osSecurityGroupService.createSecurityGroup(osSecurityGroupVo);
      return Result.OK("創建成功");
   }


   /**
    *   更新安全組
    *
    * @param osSecurityGroupVo
    * @return
    */
   @AutoLog(value = "更新安全組")
   @ApiOperation(value="更新安全組", notes="更新安全組")
   @GetMapping(value = "/updateSecurityGroup")
   public Result<?> updateSecurityGroup(OsSecurityGroupVo osSecurityGroupVo) {
      osSecurityGroupService.updateSecurityGroup(osSecurityGroupVo);
      return Result.OK("更新成功");
   }

   /**
    *   刪除安全組
    *
    * @param osSecurityGroupVo
    * @return
    */
   @AutoLog(value = "刪除安全組")
   @ApiOperation(value="刪除安全組", notes="刪除安全組")
   @GetMapping(value = "/deleteSecurityGroup")
   public Result<?> deleteSecurityGroup(OsSecurityGroupVo osSecurityGroupVo) {
      ActionResponse actionResponse = osSecurityGroupService.deleteSecurityGroup(osSecurityGroupVo);
      if(actionResponse.isSuccess()){//正常

      }else if(actionResponse.getCode()==400){
         return  Result.error(actionResponse.getFault());
      }
      return Result.OK("刪除成功");
   }


   /**
    *   創建安全組規則
    *
    * @param osSecurityGroupRuleVo
    * @return
    */
   @AutoLog(value = "創建安全組規則")
   @ApiOperation(value="創建安全組規則", notes="創建安全組規則")
   @GetMapping(value = "/createSecurityGroupRule")
   public Result<?> createSecurityGroupRule(OsSecurityGroupRuleVo osSecurityGroupRuleVo) {
      osSecurityGroupService.createSecurityGroupRule(osSecurityGroupRuleVo);
      return Result.OK("創建成功");
   }


   /**
    *   刪除安全組規則
    *
    * @param osSecurityGroupRuleVo
    * @return
    */
   @AutoLog(value = "刪除安全組規則")
   @ApiOperation(value="刪除安全組規則", notes="刪除安全組規則")
   @GetMapping(value = "/deleteSecurityGroupRule")
   public Result<?> deleteSecurityGroupRule(OsSecurityGroupRuleVo osSecurityGroupRuleVo) {
      ActionResponse actionResponse = osSecurityGroupService.deleteSecurityGroupRule(osSecurityGroupRuleVo);
      if(actionResponse.isSuccess()){//正常

      }else if(actionResponse.getCode()==400){
         return  Result.error(actionResponse.getFault());
      }
      return Result.OK("刪除成功");
   }

   /**
    *  批量刪除安全組規則
    *
    * @param ids
    * @return
    */
   @AutoLog(value = "批量刪除安全組規則")
   @ApiOperation(value="批量刪除安全組規則", notes="批量刪除安全組規則")
   @DeleteMapping(value = "/deleteBatchSecurityGroupRule")
   public Result<?> deleteBatchSecurityGroupRule(@RequestParam(name="ids",required=true) String ids,@RequestParam(name="projectId",required=true) String projectId) {
      osSecurityGroupService.deleteBatchSecurityGroupRule(ids,projectId);
      return Result.OK("批量刪除成功!");
   }

}
