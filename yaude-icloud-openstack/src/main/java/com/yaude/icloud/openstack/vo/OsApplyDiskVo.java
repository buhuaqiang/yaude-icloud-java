package com.yaude.icloud.openstack.vo;

import com.yaude.common.aspect.annotation.Dict;
import com.yaude.icloud.openstack.entity.OsApplyDisk;
import com.yaude.icloud.openstack.entity.OsOption;
import lombok.Data;

/**
 * @author Ad
 * @version 1.0.0
 * @ClassName OsApplyVo.java
 * @Description TODO
 * @createTime 2021年09月24日 15:30:00
 */
@Data
public class OsApplyDiskVo extends OsApplyDisk {
    //镜像名称
    private String imgName;

    //镜像id
    private String imgId;

    //快照名称
    private String snapshotName;

    //快照id
    private String snapshotId;

    //卷名称
    private String volumeName;

    //卷id
    private String volumeId;

    //审核意见
    private String optionsText;
}
