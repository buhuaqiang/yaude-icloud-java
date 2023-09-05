package com.yaude.modules.system.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.modules.system.entity.SysDepart;
import com.yaude.modules.system.model.DepartIdModel;
import com.yaude.modules.system.model.SysDepartTreeModel;
import java.util.List;

/**
 * <p>
 * 部門表 服務實現類
 * <p>
 * 
 * @Author:Steve
 * @Since：   2019-01-22
 */
public interface ISysDepartService extends IService<SysDepart>{

    /**
     * 查詢我的部門信息,并分節點進行顯示
     * @return
     */
    List<SysDepartTreeModel> queryMyDeptTreeList(String departIds);

    /**
     * 查詢所有部門信息,并分節點進行顯示
     * @return
     */
    List<SysDepartTreeModel> queryTreeList();

    /**
     * 查詢所有部門DepartId信息,并分節點進行顯示
     * @return
     */
    public List<DepartIdModel> queryDepartIdTreeList();

    /**
     * 保存部門數據
     * @param sysDepart
     */
    void saveDepartData(SysDepart sysDepart,String username);

    /**
     * 更新depart數據
     * @param sysDepart
     * @return
     */
    Boolean updateDepartDataById(SysDepart sysDepart,String username);
    
    /**
     * 刪除depart數據
     * @param id
     * @return
     */
	/* boolean removeDepartDataById(String id); */
    
    /**
     * 根據關鍵字搜索相關的部門數據
     * @param keyWord
     * @return
     */
    List<SysDepartTreeModel> searhBy(String keyWord,String myDeptSearch,String departIds);
    
    /**
     * 根據部門id刪除并刪除其可能存在的子級部門
     * @param id
     * @return
     */
    boolean delete(String id);
    
    /**
     * 查詢SysDepart集合
     * @param userId
     * @return
     */
	public List<SysDepart> queryUserDeparts(String userId);

    /**
     * 根據用戶名查詢部門
     *
     * @param username
     * @return
     */
    List<SysDepart> queryDepartsByUsername(String username);

	 /**
     * 根據部門id批量刪除并刪除其可能存在的子級部門
     * @param id
     * @return
     */
	void deleteBatchWithChildren(List<String> ids);

    /**
     *  根據部門Id查詢,當前和下級所有部門IDS
     * @param departId
     * @return
     */
    List<String> getSubDepIdsByDepId(String departId);

    /**
     * 獲取我的部門下級所有部門IDS
     * @return
     */
    List<String> getMySubDepIdsByDepId(String departIds);
    /**
     * 根據關鍵字獲取部門信息（通訊錄）
     * @return
     */
    List<SysDepartTreeModel> queryTreeByKeyWord(String keyWord);
    /**
     * 獲取我的部門下級所有部門
     * @return
     */
    List<SysDepartTreeModel> queryTreeListByPid(String parentId);

    /**
     * 獲取某個部門的所有父級部門的ID
     *
     * @param departId 根據departId查
     */
    JSONObject queryAllParentIdByDepartId(String departId);

    /**
     * 獲取某個部門的所有父級部門的ID
     *
     * @param orgCode 根據orgCode查
     */
    JSONObject queryAllParentIdByOrgCode(String orgCode);
    /**
     * 獲取公司信息
     * @return
     */
    SysDepart queryCompByOrgCode(String orgCode);
    /**
     * 獲取下級部門
     * @return
     */
    List<SysDepart> queryDeptByPid(String pid);
}
