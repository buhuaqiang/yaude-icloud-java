package com.yaude.icloud.openstack.vo;

import com.yaude.icloud.openstack.entity.OsApply;
import com.yaude.icloud.openstack.entity.OsApplyFloatip;
import lombok.Data;

/**
 * @author Ad
 * @version 1.0.0
 * @ClassName OsApplyVo.java
 * @Description TODO
 * @createTime 2021年09月24日 15:30:00
 */
@Data
public class OsApplyFloatipVo extends OsApplyFloatip {


    //网络id
    private String networkId;

    //审核意见
    private String optionsText;

}
