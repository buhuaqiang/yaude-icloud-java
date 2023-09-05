package com.yaude.icloud.openstack.vo;

import com.yaude.common.aspect.annotation.Dict;
import com.yaude.icloud.openstack.entity.OsApply;
import com.yaude.icloud.openstack.entity.OsOption;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @author Ad
 * @version 1.0.0
 * @ClassName OsApplyVo.java
 * @Description TODO
 * @createTime 2021年09月24日 15:30:00
 */
@Data
public class OsOptionVo extends OsOption {

    //名称
    private String applyName;

    /**申請类型*/
    @Dict(dicCode = "options")
    private String options;

    /**狀態*/
    @Dict(dicCode = "options_status")
    private String status;

    //项目id
    private String projectId;

    //申请后是否生成
    private String signId;

    //調整後的類型
    private String adjustType;

}
