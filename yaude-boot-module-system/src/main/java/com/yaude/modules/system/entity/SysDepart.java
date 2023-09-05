package com.yaude.modules.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.yaude.common.aspect.annotation.Dict;
import lombok.Data;
import org.jeecgframework.poi.excel.annotation.Excel;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 部門表
 * <p>
 * 
 * @Author Steve
 * @Since  2019-01-22
 */
@Data
@TableName("sys_depart")
public class SysDepart implements Serializable {
    private static final long serialVersionUID = 1L;
    
	/**ID*/
	@TableId(type = IdType.ASSIGN_ID)
	private String id;
	/**父機構ID*/
	private String parentId;
	/**機構/部門名稱*/
	@Excel(name="機構/部門名稱",width=15)
	private String departName;
	/**英文名*/
	@Excel(name="英文名",width=15)
	private String departNameEn;
	/**縮寫*/
	private String departNameAbbr;
	/**排序*/
	@Excel(name="排序",width=15)
	private Integer departOrder;
	/**描述*/
	@Excel(name="描述",width=15)
	private String description;
	/**機構類別 1公司，2組織機構，2崗位*/
	@Excel(name="機構類別",width=15,dicCode="org_category")
	private String orgCategory;
	/**機構類型*/
	private String orgType;
	/**機構編碼*/
	@Excel(name="機構編碼",width=15)
	private String orgCode;
	/**手機號*/
	@Excel(name="手機號",width=15)
	private String mobile;
	/**傳真*/
	@Excel(name="傳真",width=15)
	private String fax;
	/**地址*/
	@Excel(name="地址",width=15)
	private String address;
	/**備注*/
	@Excel(name="備注",width=15)
	private String memo;
	/**狀態（1啟用，0不啟用）*/
	@Dict(dicCode = "depart_status")
	private String status;
	/**刪除狀態（0，正常，1已刪除）*/
	@Dict(dicCode = "del_flag")
	private String delFlag;
	/**對接企業微信的ID*/
	private String qywxIdentifier;
	/**創建人*/
	private String createBy;
	/**創建日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date createTime;
	/**更新人*/
	private String updateBy;
	/**更新日期*/
	@JsonFormat(timezone = "GMT+8",pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")
	private Date updateTime;
	
	/**
	 * 重寫equals方法
	 */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
			return true;
		}
        if (o == null || getClass() != o.getClass()) {
			return false;
		}
        if (!super.equals(o)) {
			return false;
		}
        SysDepart depart = (SysDepart) o;
        return Objects.equals(id, depart.id) &&
                Objects.equals(parentId, depart.parentId) &&
                Objects.equals(departName, depart.departName) &&
                Objects.equals(departNameEn, depart.departNameEn) &&
                Objects.equals(departNameAbbr, depart.departNameAbbr) &&
                Objects.equals(departOrder, depart.departOrder) &&
                Objects.equals(description, depart.description) &&
                Objects.equals(orgCategory, depart.orgCategory) &&
                Objects.equals(orgType, depart.orgType) &&
                Objects.equals(orgCode, depart.orgCode) &&
                Objects.equals(mobile, depart.mobile) &&
                Objects.equals(fax, depart.fax) &&
                Objects.equals(address, depart.address) &&
                Objects.equals(memo, depart.memo) &&
                Objects.equals(status, depart.status) &&
                Objects.equals(delFlag, depart.delFlag) &&
                Objects.equals(createBy, depart.createBy) &&
                Objects.equals(createTime, depart.createTime) &&
                Objects.equals(updateBy, depart.updateBy) &&
                Objects.equals(updateTime, depart.updateTime);
    }

    /**
     * 重寫hashCode方法
     */
    @Override
    public int hashCode() {

        return Objects.hash(super.hashCode(), id, parentId, departName, 
        		departNameEn, departNameAbbr, departOrder, description,orgCategory, 
        		orgType, orgCode, mobile, fax, address, memo, status, 
        		delFlag, createBy, createTime, updateBy, updateTime);
    }
}
