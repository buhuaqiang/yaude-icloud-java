package com.yaude.icloud.openstack.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import com.yaude.icloud.openstack.entity.OsResourceUsage;
import com.yaude.icloud.openstack.entity.OsUserProject;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;

/**
 * @author Ad
 * @version 1.0.0
 * @ClassName OsUserProjectVo.java
 * @Description TODO
 * @createTime 2021年10月07日 15:30:00
 */
@Data
public class OsResourceUsageVo extends OsResourceUsage {

    private String startTime;
    private String endTime;

    //月份
    private String mon;
    //年份
    private String years;



}
