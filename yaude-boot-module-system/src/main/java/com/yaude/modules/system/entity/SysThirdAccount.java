package com.yaude.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import org.jeecgframework.poi.excel.annotation.Excel;

/**
 * @Description: 第三方登錄賬號表
 * @Author: jeecg-boot
 * @Date:   2020-11-17
 * @Version: V1.0
 */
@Data
@TableName("sys_third_account")
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="sys_third_account對象", description="第三方登錄賬號表")
public class SysThirdAccount {
 
	/**編號*/
	@TableId(type = IdType.ASSIGN_ID)
  @ApiModelProperty(value = "編號")
	private java.lang.String id;
	/**第三方登錄id*/
	@Excel(name = "第三方登錄id", width = 15)
	@ApiModelProperty(value = "第三方登錄id")
	private java.lang.String sysUserId;
	/**登錄來源*/
	@Excel(name = "登錄來源", width = 15)
	@ApiModelProperty(value = "登錄來源")
	private java.lang.String thirdType;
	/**頭像*/
	@Excel(name = "頭像", width = 15)
	@ApiModelProperty(value = "頭像")
	private java.lang.String avatar;
	/**狀態(1-正常,2-凍結)*/
	@Excel(name = "狀態(1-正常,2-凍結)", width = 15)
	@ApiModelProperty(value = "狀態(1-正常,2-凍結)")
	private java.lang.Integer status;
	/**刪除狀態(0-正常,1-已刪除)*/
	@Excel(name = "刪除狀態(0-正常,1-已刪除)", width = 15)
	@ApiModelProperty(value = "刪除狀態(0-正常,1-已刪除)")
	private java.lang.Integer delFlag;
	/**真實姓名*/
	@Excel(name = "真實姓名", width = 15)
	@ApiModelProperty(value = "真實姓名")
	private java.lang.String realname;
	/**真實姓名*/
	@Excel(name = "真實姓名", width = 15)
	@ApiModelProperty(value = "真實姓名")
	private java.lang.String thirdUserUuid;
	/**真實姓名*/
	@Excel(name = "第三方用戶賬號", width = 15)
	@ApiModelProperty(value = "第三方用戶賬號")
	private java.lang.String thirdUserId;
}
