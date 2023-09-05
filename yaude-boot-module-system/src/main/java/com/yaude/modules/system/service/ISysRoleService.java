package com.yaude.modules.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yaude.common.api.vo.Result;
import com.yaude.modules.system.entity.SysRole;
import org.jeecgframework.poi.excel.entity.ImportParams;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 角色表 服務類
 * </p>
 *
 * @Author scott
 * @since 2018-12-19
 */
public interface ISysRoleService extends IService<SysRole> {

    /**
     * 導入 excel ，檢查 roleCode 的唯一性
     *
     * @param file
     * @param params
     * @return
     * @throws Exception
     */
    Result importExcelCheckRoleCode(MultipartFile file, ImportParams params) throws Exception;

    /**
     * 刪除角色
     * @param roleid
     * @return
     */
    public boolean deleteRole(String roleid);

    /**
     * 批量刪除角色
     * @param roleids
     * @return
     */
    public boolean deleteBatchRole(String[] roleids);

}
