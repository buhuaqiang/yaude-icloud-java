package com.yaude.modules.demo.test.entity;

import java.io.Serializable;

import com.yaude.common.system.base.entity.JeecgEntity;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @Description: jeecg 測試demo 
 * @Author: jeecg-boot 
 * @Date:	2018-12-29 
 * @Version:V1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@ApiModel(value="測試DEMO對象", description="測試DEMO")
@TableName("demo")
public class JeecgDemo extends JeecgEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/** 部門編碼 */
	@Excel(name="部門編碼",width=25)
	@ApiModelProperty(value = "部門編碼")
	private String sysOrgCode;
	/** 姓名 */
	@Excel(name="姓名",width=25)
	@ApiModelProperty(value = "姓名")
	private String name;
	/** 關鍵詞 */
	@ApiModelProperty(value = "關鍵詞")
	@Excel(name="關鍵詞",width=15)
	private String keyWord;
	/** 打卡時間 */
	@ApiModelProperty(value = "打卡時間")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
	@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Excel(name="打卡時間",width=20,format="yyyy-MM-dd HH:mm:ss")
	private java.util.Date punchTime;
	/** 工資 */
	@ApiModelProperty(value = "工資",example = "0")
	@Excel(name="工資",width=15)
	private java.math.BigDecimal salaryMoney;
	/** 獎金 */
	@ApiModelProperty(value = "獎金",example = "0")
	@Excel(name="獎金",width=15)
	private Double bonusMoney;
	/** 性別 {男:1,女:2} */
	@ApiModelProperty(value = "性別")
	@Excel(name = "性別", width = 15, dicCode = "sex")
	private String sex;
	/** 年齡 */
	@ApiModelProperty(value = "年齡",example = "0")
	@Excel(name="年齡",width=15)
	private Integer age;
	/** 生日 */
	@ApiModelProperty(value = "生日")
	@JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@Excel(name="生日",format="yyyy-MM-dd")
	private java.util.Date birthday;
	/** 郵箱 */
	@ApiModelProperty(value = "郵箱")
	@Excel(name="郵箱",width=30)
	private String email;
	/** 個人簡介 */
	@ApiModelProperty(value = "個人簡介")
	private String content;
}
