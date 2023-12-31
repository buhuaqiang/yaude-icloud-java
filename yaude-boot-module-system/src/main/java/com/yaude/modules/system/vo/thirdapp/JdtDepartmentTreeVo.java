package com.yaude.modules.system.vo.thirdapp;

import com.jeecg.dingtalk.api.department.vo.Department;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 釘釘樹結構的部門
 *
 * @author sunjianlei
 */
public class JdtDepartmentTreeVo extends Department {

    private List<JdtDepartmentTreeVo> children;

    public List<JdtDepartmentTreeVo> getChildren() {
        return children;
    }

    public JdtDepartmentTreeVo setChildren(List<JdtDepartmentTreeVo> children) {
        this.children = children;
        return this;
    }

    public JdtDepartmentTreeVo(Department department) {
        BeanUtils.copyProperties(department, this);
    }

    /**
     * 是否有子項
     */
    public boolean hasChildren() {
        return children != null && children.size() > 0;
    }

    @Override
    public String toString() {
        return "JwDepartmentTree{" +
                "children=" + children +
                "} " + super.toString();
    }

    /**
     * 靜態輔助方法，將list轉為tree結構
     */
    public static List<JdtDepartmentTreeVo> listToTree(List<Department> allDepartment) {
        // 先找出所有的父級
        List<JdtDepartmentTreeVo> treeList = getByParentId(1, allDepartment);
        getChildrenRecursion(treeList, allDepartment);
        return treeList;
    }

    private static List<JdtDepartmentTreeVo> getByParentId(Integer parentId, List<Department> allDepartment) {
        List<JdtDepartmentTreeVo> list = new ArrayList<>();
        for (Department department : allDepartment) {
            if (parentId.equals(department.getParent_id())) {
                list.add(new JdtDepartmentTreeVo(department));
            }
        }
        return list;
    }

    private static void getChildrenRecursion(List<JdtDepartmentTreeVo> treeList, List<Department> allDepartment) {
        for (JdtDepartmentTreeVo departmentTree : treeList) {
            // 遞歸尋找子級
            List<JdtDepartmentTreeVo> children = getByParentId(departmentTree.getDept_id(), allDepartment);
            if (children.size() > 0) {
                departmentTree.setChildren(children);
                getChildrenRecursion(children, allDepartment);
            }
        }
    }

}
