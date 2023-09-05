package com.yaude.icloud.openstack.service;

import com.yaude.icloud.openstack.entity.OsApply;
import com.yaude.icloud.openstack.entity.OsOption;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.icloud.openstack.vo.OsApplyVo;
import com.yaude.icloud.openstack.vo.OsOptionVo;

import java.util.List;

/**
 * @Description: 審核意見細檔
 * @Author: jeecg-boot
 * @Date:   2021-09-23
 * @Version: V1.0
 */
public interface IOsOptionService extends IService<OsOption> {
    void upStatus(OsApplyVo osApplyVo) throws InterruptedException;

    List<OsOptionVo> getOsOptionVoListByOsApply(List<OsOption> osOptionList);
    String getProjectId(String applyId,String applyType);
    void deleteOption(String applyId,String applyType);

    void getStatus(OsOptionVo osOptionVo) throws InterruptedException;

}
