package com.ruoyi.attendance.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.TreeSelect;
import com.ruoyi.common.core.domain.entity.SysDept;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.SecurityUtils;
import com.ruoyi.system.service.ISysDeptService;

/**
 * 微信小程序部门控制器
 */
@RestController
@RequestMapping("/api/dept")
public class WxDeptController extends BaseController {
    
    @Autowired
    private ISysDeptService deptService;
    
    /**
     * 获取部门树结构
     */
    @GetMapping("/tree")
    public AjaxResult getDeptTree() {
        // 获取完整的部门列表（不进行过滤）
        List<SysDept> allDepts = deptService.selectDeptList(new SysDept());

        // 获取用户部门ID
        Long userDeptId = SecurityUtils.getLoginUser().getDeptId();

        // 根据用户权限进行筛选
        List<SysDept> depts;
        if (SysUser.isAdmin(SecurityUtils.getUserId())) {
            // 管理员可以看到所有部门
            depts = allDepts;
        } else {
            // 非管理员只看当前部门及下级
            depts = allDepts.stream()
                    .filter(d -> {
                        // 筛选当前部门及其子部门
                        return d.getDeptId().equals(userDeptId) ||
                                (d.getAncestors() != null &&
                                        d.getAncestors().contains(userDeptId.toString()));
                    })
                    .collect(Collectors.toList());
        }

        // 构建树
        List<SysDept> deptTree = deptService.buildDeptTree(depts);
        List<TreeData> treeData = new ArrayList<>();
        for (SysDept dept : deptTree) {
            treeData.add(convertToTreeData(dept));
        }

        return AjaxResult.success(treeData);
    }
    
    /**
     * 将SysDept对象转换为TreeData对象
     */
    private TreeData convertToTreeData(SysDept dept) {
        TreeData treeData = new TreeData();
        treeData.setId(dept.getDeptId());
        treeData.setLabel(dept.getDeptName());
        treeData.setDisabled("1".equals(dept.getStatus())); // 如果状态为1表示停用，则设置为disabled
        
        if (dept.getChildren() != null && !dept.getChildren().isEmpty()) {
            List<TreeData> children = new ArrayList<>();
            for (SysDept child : dept.getChildren()) {
                children.add(convertToTreeData(child));
            }
            treeData.setChildren(children);
        }
        
        return treeData;
    }
    
    /**
     * 树形数据结构
     */
    private static class TreeData {
        private Long id;
        private String label;
        private boolean disabled;
        private List<TreeData> children;
        
        public Long getId() {
            return id;
        }
        
        public void setId(Long id) {
            this.id = id;
        }
        
        public String getLabel() {
            return label;
        }
        
        public void setLabel(String label) {
            this.label = label;
        }
        
        public boolean isDisabled() {
            return disabled;
        }
        
        public void setDisabled(boolean disabled) {
            this.disabled = disabled;
        }
        
        public List<TreeData> getChildren() {
            return children;
        }
        
        public void setChildren(List<TreeData> children) {
            this.children = children;
        }
    }
    
    /**
     * 获取部门及其下级部门列表
     */
    @GetMapping("/children")
    public AjaxResult getDeptChildren(@RequestParam(value = "deptId",required = false) Long deptId) {
        // 如果部门ID为空，使用当前用户的部门ID
        if (deptId == null) {
            deptId = SecurityUtils.getLoginUser().getDeptId();
        }
        
        // 获取所有部门
        List<SysDept> allDepts = deptService.selectDeptList(new SysDept());
        
        // 查找指定部门
        SysDept targetDept = null;
        for (SysDept dept : allDepts) {
            if (dept.getDeptId().equals(deptId)) {
                targetDept = dept;
                break;
            }
        }
        
        if (targetDept == null) {
            return AjaxResult.error("部门不存在");
        }
        
        // 获取目标部门的ancestors字段
        String ancestors = targetDept.getAncestors() + "," + deptId;
        
        // 过滤获取子部门
        List<SysDept> childDepts = allDepts.stream()
                .filter(dept -> dept.getAncestors() != null && dept.getAncestors().startsWith(ancestors))
                .collect(Collectors.toList());
        
        // 添加当前部门
        childDepts.add(targetDept);
        
        return AjaxResult.success(childDepts);
    }
}
