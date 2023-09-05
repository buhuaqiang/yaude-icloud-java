package com.yaude.modules.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import com.yaude.modules.system.entity.SysThirdAccount;

import java.util.List;

/**
 * @Description: 第三方登錄賬號表
 * @Author: jeecg-boot
 * @Date: 2020-11-17
 * @Version: V1.0
 */
public interface SysThirdAccountMapper extends BaseMapper<SysThirdAccount> {

    /**
     * 通過 sysUsername 集合批量查詢
     *
     * @param sysUsernameArr username集合
     * @param thirdType       第三方類型
     * @return
     */
    List<SysThirdAccount> selectThirdIdsByUsername(@Param("sysUsernameArr") String[] sysUsernameArr, @Param("thirdType") String thirdType);

}
