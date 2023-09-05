package com.yaude.icloud.openstack.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.yaude.icloud.openstack.entity.OsApply;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.icloud.openstack.vo.OsApplyVo;
import com.yaude.icloud.openstack.vo.OsInstanceVo;

import java.text.ParseException;
import java.util.List;

/**
 * @Description: 申請明細檔
 * @Author: jeecg-boot
 * @Date:   2021-09-24
 * @Version: V1.0
 */
public interface IOsApplyService extends IService<OsApply> {

    List<OsApplyVo> getOsApplyVoListByOsApply(List<OsApply> osApplyList);

    List<OsApplyVo> getProject();
    List<OsApplyVo> getImg(String projectId);
    List<OsApplyVo> getFlavor(String projectId);
    List<OsApply> getSecurity(String projectId);
    List<OsApplyVo> getNetwork(String projectId);
    List<OsApplyVo> getPrivateKey(String projectId);
    int getStatus(String id);
    int getCountStatus(List<String> ids);




}
