package com.yaude.modules.system.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.common.system.vo.SysUserCacheInfo;
import com.yaude.common.api.vo.Result;
import com.yaude.modules.system.entity.SysUser;
import com.yaude.modules.system.model.SysUserSysDepartModel;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * 用戶表 服務類
 * </p>
 *
 * @Author scott
 * @since 2018-12-20
 */
public interface ISysUserService extends IService<SysUser> {

	/**
	 * 重置密碼
	 *
	 * @param username
	 * @param oldpassword
	 * @param newpassword
	 * @param confirmpassword
	 * @return
	 */
	public Result<?> resetPassword(String username, String oldpassword, String newpassword, String confirmpassword);

	/**
	 * 修改密碼
	 *
	 * @param sysUser
	 * @return
	 */
	public Result<?> changePassword(SysUser sysUser);

	/**
	 * 刪除用戶
	 * @param userId
	 * @return
	 */
	public boolean deleteUser(String userId);

	/**
	 * 批量刪除用戶
	 * @param userIds
	 * @return
	 */
	public boolean deleteBatchUsers(String userIds);
	
	public SysUser getUserByName(String username);
	
	/**
	 * 添加用戶和用戶角色關系
	 * @param user
	 * @param roles
	 */
	public void addUserWithRole(SysUser user,String roles);
	
	
	/**
	 * 修改用戶和用戶角色關系
	 * @param user
	 * @param roles
	 */
	public void editUserWithRole(SysUser user,String roles);

	/**
	 * 獲取用戶的授權角色
	 * @param username
	 * @return
	 */
	public List<String> getRole(String username);
	
	/**
	  * 查詢用戶信息包括 部門信息
	 * @param username
	 * @return
	 */
	public SysUserCacheInfo getCacheUser(String username);

	/**
	 * 根據部門Id查詢
	 * @param
	 * @return
	 */
	public IPage<SysUser> getUserByDepId(Page<SysUser> page, String departId, String username);

	/**
	 * 根據部門Ids查詢
	 * @param
	 * @return
	 */
	public IPage<SysUser> getUserByDepIds(Page<SysUser> page, List<String> departIds, String username);

	/**
	 * 根據 userIds查詢，查詢用戶所屬部門的名稱（多個部門名逗號隔開）
	 * @param
	 * @return
	 */
	public Map<String,String> getDepNamesByUserIds(List<String> userIds);

    /**
     * 根據部門 Id 和 QueryWrapper 查詢
     *
     * @param page
     * @param departId
     * @param queryWrapper
     * @return
     */
    public IPage<SysUser> getUserByDepartIdAndQueryWrapper(Page<SysUser> page, String departId, QueryWrapper<SysUser> queryWrapper);

	/**
	 * 根據 orgCode 查詢用戶，包括子部門下的用戶
	 *
	 * @param orgCode
	 * @param userParams 用戶查詢條件，可為空
	 * @param page 分頁參數
	 * @return
	 */
	IPage<SysUserSysDepartModel> queryUserByOrgCode(String orgCode, SysUser userParams, IPage page);

	/**
	 * 根據角色Id查詢
	 * @param
	 * @return
	 */
	public IPage<SysUser> getUserByRoleId(Page<SysUser> page,String roleId, String username);

	/**
	 * 通過用戶名獲取用戶角色集合
	 *
	 * @param username 用戶名
	 * @return 角色集合
	 */
	Set<String> getUserRolesSet(String username);

	/**
	 * 通過用戶名獲取用戶權限集合
	 *
	 * @param username 用戶名
	 * @return 權限集合
	 */
	Set<String> getUserPermissionsSet(String username);
	
	/**
	 * 根據用戶名設置部門ID
	 * @param username
	 * @param orgCode
	 */
	void updateUserDepart(String username,String orgCode);
	
	/**
	 * 根據手機號獲取用戶名和密碼
	 */
	public SysUser getUserByPhone(String phone);


	/**
	 * 根據郵箱獲取用戶
	 */
	public SysUser getUserByEmail(String email);


	/**
	 * 添加用戶和用戶部門關系
	 * @param user
	 * @param selectedParts
	 */
	void addUserWithDepart(SysUser user, String selectedParts);

	/**
	 * 編輯用戶和用戶部門關系
	 * @param user
	 * @param departs
	 */
	void editUserWithDepart(SysUser user, String departs);
	
	/**
	   * 校驗用戶是否有效
	 * @param sysUser
	 * @return
	 */
	Result checkUserIsEffective(SysUser sysUser);

	/**
	 * 查詢被邏輯刪除的用戶
	 */
	List<SysUser> queryLogicDeleted();

	/**
	 * 查詢被邏輯刪除的用戶（可拼裝查詢條件）
	 */
	List<SysUser> queryLogicDeleted(LambdaQueryWrapper<SysUser> wrapper);

	/**
	 * 還原被邏輯刪除的用戶
	 */
	boolean revertLogicDeleted(List<String> userIds, SysUser updateEntity);

	/**
	 * 徹底刪除被邏輯刪除的用戶
	 */
	boolean removeLogicDeleted(List<String> userIds);

    /**
     * 更新手機號、郵箱空字符串為 null
     */
    @Transactional(rollbackFor = Exception.class)
    boolean updateNullPhoneEmail();

	/**
	 * 保存第三方用戶信息
	 * @param sysUser
	 */
	void saveThirdUser(SysUser sysUser);

	/**
	 * 根據部門Ids查詢
	 * @param
	 * @return
	 */
	List<SysUser> queryByDepIds(List<String> departIds, String username);

	/**
	 * 保存用戶
	 * @param user 用戶
	 * @param selectedRoles 選擇的角色id，多個以逗號隔開
	 * @param selectedDeparts 選擇的部門id，多個以逗號隔開
	 */
	void saveUser(SysUser user, String selectedRoles, String selectedDeparts);

	/**
	 * 編輯用戶
	 * @param user 用戶
	 * @param roles 選擇的角色id，多個以逗號隔開
	 * @param departs 選擇的部門id，多個以逗號隔開
	 */
	void editUser(SysUser user, String roles, String departs);

	/** userId轉為username */
	List<String> userIdToUsername(Collection<String> userIdList);

}
