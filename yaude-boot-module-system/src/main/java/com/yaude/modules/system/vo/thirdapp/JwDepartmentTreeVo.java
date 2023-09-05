package com.yaude.modules.system.vo.thirdapp;

import com.jeecg.qywx.api.department.vo.Department;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * 企業微信樹結構的部門
 *
 * @author sunjianlei
 */
public class JwDepartmentTreeVo extends Department {

    private List<JwDepartmentTreeVo> children;

    public List<JwDepartmentTreeVo> getChildren() {
        return children;
    }

    public JwDepartmentTreeVo setChildren(List<JwDepartmentTreeVo> children) {
        this.children = children;
        return this;
    }

    public JwDepartmentTreeVo(Department department) {
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
    public static List<JwDepartmentTreeVo> listToTree(List<Department> allDepartment) {
        // 先找出所有的父級
        List<JwDepartmentTreeVo> treeList = getByParentId("1", allDepartment);
        getChildrenRecursion(treeList, allDepartment);
        return treeList;
    }

    private static List<JwDepartmentTreeVo> getByParentId(String parentId, List<Department> allDepartment) {
        List<JwDepartmentTreeVo> list = new ArrayList<>();
        for (Department department : allDepartment) {
            if (parentId.equals(department.getParentid())) {
                list.add(new JwDepartmentTreeVo(department));
            }
        }
        return list;
    }

    private static void getChildrenRecursion(List<JwDepartmentTreeVo> treeList, List<Department> allDepartment) {
        for (JwDepartmentTreeVo departmentTree : treeList) {
            // 遞歸尋找子級
            List<JwDepartmentTreeVo> children = getByParentId(departmentTree.getId(), allDepartment);
            if (children.size() > 0) {
                departmentTree.setChildren(children);
                getChildrenRecursion(children, allDepartment);
            }
        }
    }

}
