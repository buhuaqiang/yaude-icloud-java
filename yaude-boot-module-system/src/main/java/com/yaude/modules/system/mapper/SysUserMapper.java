package com.yaude.modules.system.mapper;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import com.yaude.modules.system.entity.SysUser;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.yaude.modules.system.model.SysUserSysDepartModel;
import com.yaude.modules.system.vo.SysUserDepVo;

import java.util.List;

/**
 * <p>
 * 用戶表 Mapper 接口
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
public interface SysUserMapper extends BaseMapper<SysUser> {
	/**
	  * 通過用戶賬號查詢用戶信息
	 * @param username
	 * @return
	 */
	public SysUser getUserByName(@Param("username") String username);

	/**
	 *  根據部門Id查詢用戶信息
	 * @param page
	 * @param departId
	 * @return
	 */
	IPage<SysUser> getUserByDepId(Page page, @Param("departId") String departId, @Param("username") String username);

	/**
	 *  根據用戶Ids,查詢用戶所屬部門名稱信息
	 * @param userIds
	 * @return
	 */
	List<SysUserDepVo> getDepNamesByUserIds(@Param("userIds")List<String> userIds);

	/**
	 *  根據部門Ids,查詢部門下用戶信息
	 * @param page
	 * @param departIds
	 * @return
	 */
	IPage<SysUser> getUserByDepIds(Page page, @Param("departIds") List<String> departIds, @Param("username") String username);

	/**
	 * 根據角色Id查詢用戶信息
	 * @param page
	 * @param
	 * @return
	 */
	IPage<SysUser> getUserByRoleId(Page page, @Param("roleId") String roleId, @Param("username") String username);
	
	/**
	 * 根據用戶名設置部門ID
	 * @param username
	 * @param departId
	 */
	void updateUserDepart(@Param("username") String username,@Param("orgCode") String orgCode);
	
	/**
	 * 根據手機號查詢用戶信息
	 * @param phone
	 * @return
	 */
	public SysUser getUserByPhone(@Param("phone") String phone);
	
	
	/**
	 * 根據郵箱查詢用戶信息
	 * @param email
	 * @return
	 */
	public SysUser getUserByEmail(@Param("email")String email);

	/**
	 * 根據 orgCode 查詢用戶，包括子部門下的用戶
	 *
	 * @param page 分頁對象, xml中可以從里面進行取值,傳遞參數 Page 即自動分頁,必須放在第一位(你可以繼承Page實現自己的分頁對象)
	 * @param orgCode
	 * @param userParams 用戶查詢條件，可為空
	 * @return
	 */
	List<SysUserSysDepartModel> getUserByOrgCode(IPage page, @Param("orgCode") String orgCode, @Param("userParams") SysUser userParams);


    /**
     * 查詢 getUserByOrgCode 的Total
     *
     * @param orgCode
     * @param userParams 用戶查詢條件，可為空
     * @return
     */
    Integer getUserByOrgCodeTotal(@Param("orgCode") String orgCode, @Param("userParams") SysUser userParams);

    /**
     * @Author scott
     * @Date 2019/12/13 16:10
     * @Description: 批量刪除角色與用戶關系
     */
	void deleteBathRoleUserRelation(@Param("roleIdArray") String[] roleIdArray);

    /**
     * @Author scott
     * @Date 2019/12/13 16:10
     * @Description: 批量刪除角色與權限關系
     */
	void deleteBathRolePermissionRelation(@Param("roleIdArray") String[] roleIdArray);

	/**
	 * 查詢被邏輯刪除的用戶
	 */
	List<SysUser> selectLogicDeleted(@Param(Constants.WRAPPER) Wrapper<SysUser> wrapper);

	/**
	 * 還原被邏輯刪除的用戶
	 */
	int revertLogicDeleted(@Param("userIds") String userIds, @Param("entity") SysUser entity);

	/**
	 * 徹底刪除被邏輯刪除的用戶
	 */
	int deleteLogicDeleted(@Param("userIds") String userIds);

    /** 更新空字符串為null【此寫法有sql注入風險，禁止隨便用】 */
    @Deprecated
    int updateNullByEmptyString(@Param("fieldName") String fieldName);
    
	/**
	 *  根據部門Ids,查詢部門下用戶信息
	 * @param departIds
	 * @return
	 */
	List<SysUser> queryByDepIds(@Param("departIds")List<String> departIds,@Param("username") String username);
}
