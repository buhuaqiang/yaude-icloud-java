package com.yaude.modules.system.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yaude.common.constant.FillRuleConstant;
import com.yaude.common.exception.JeecgBootException;
import com.yaude.common.util.FillRuleUtil;
import com.yaude.common.util.oConvertUtils;
import com.yaude.modules.system.entity.SysCategory;
import com.yaude.modules.system.mapper.SysCategoryMapper;
import com.yaude.modules.system.model.TreeSelectModel;
import com.yaude.modules.system.service.ISysCategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Description: 分類字典
 * @Author: jeecg-boot
 * @Date:   2019-05-29
 * @Version: V1.0
 */
@Service
public class SysCategoryServiceImpl extends ServiceImpl<SysCategoryMapper, SysCategory> implements ISysCategoryService {

	@Override
	public void addSysCategory(SysCategory sysCategory) {
		String categoryCode = "";
		String categoryPid = ISysCategoryService.ROOT_PID_VALUE;
		String parentCode = null;
		if(oConvertUtils.isNotEmpty(sysCategory.getPid())){
			categoryPid = sysCategory.getPid();

			//PID 不是根節點 說明需要設置父節點 hasChild 為1
			if(!ISysCategoryService.ROOT_PID_VALUE.equals(categoryPid)){
				SysCategory parent = baseMapper.selectById(categoryPid);
				parentCode = parent.getCode();
				if(parent!=null && !"1".equals(parent.getHasChild())){
					parent.setHasChild("1");
					baseMapper.updateById(parent);
				}
			}
		}
		//update-begin--Author:baihailong  Date:20191209 for：分類字典編碼規則生成器做成公用配置
		JSONObject formData = new JSONObject();
		formData.put("pid",categoryPid);
		categoryCode = (String) FillRuleUtil.executeRule(FillRuleConstant.CATEGORY,formData);
		//update-end--Author:baihailong  Date:20191209 for：分類字典編碼規則生成器做成公用配置
		sysCategory.setCode(categoryCode);
		sysCategory.setPid(categoryPid);
		baseMapper.insert(sysCategory);
	}
	
	@Override
	public void updateSysCategory(SysCategory sysCategory) {
		if(oConvertUtils.isEmpty(sysCategory.getPid())){
			sysCategory.setPid(ISysCategoryService.ROOT_PID_VALUE);
		}else{
			//如果當前節點父ID不為空 則設置父節點的hasChild 為1
			SysCategory parent = baseMapper.selectById(sysCategory.getPid());
			if(parent!=null && !"1".equals(parent.getHasChild())){
				parent.setHasChild("1");
				baseMapper.updateById(parent);
			}
		}
		baseMapper.updateById(sysCategory);
	}

	@Override
	public List<TreeSelectModel> queryListByCode(String pcode) throws JeecgBootException {
		String pid = ROOT_PID_VALUE;
		if(oConvertUtils.isNotEmpty(pcode)) {
			List<SysCategory> list = baseMapper.selectList(new LambdaQueryWrapper<SysCategory>().eq(SysCategory::getCode, pcode));
			if(list==null || list.size() ==0) {
				throw new JeecgBootException("該編碼【"+pcode+"】不存在，請核實!");
			}
			if(list.size()>1) {
				throw new JeecgBootException("該編碼【"+pcode+"】存在多個，請核實!");
			}
			pid = list.get(0).getId();
		}
		return baseMapper.queryListByPid(pid,null);
	}

	@Override
	public List<TreeSelectModel> queryListByPid(String pid) {
		if(oConvertUtils.isEmpty(pid)) {
			pid = ROOT_PID_VALUE;
		}
		return baseMapper.queryListByPid(pid,null);
	}

	@Override
	public List<TreeSelectModel> queryListByPid(String pid, Map<String, String> condition) {
		if(oConvertUtils.isEmpty(pid)) {
			pid = ROOT_PID_VALUE;
		}
		return baseMapper.queryListByPid(pid,condition);
	}

	@Override
	public String queryIdByCode(String code) {
		return baseMapper.queryIdByCode(code);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void deleteSysCategory(String ids) {
		String allIds = this.queryTreeChildIds(ids);
		String pids = this.queryTreePids(ids);
		//1.刪除時將節點下所有子節點一并刪除
		this.baseMapper.deleteBatchIds(Arrays.asList(allIds.split(",")));
		//2.將父節點中已經沒有下級的節點，修改為沒有子節點
		if(oConvertUtils.isNotEmpty(pids)){
			LambdaUpdateWrapper<SysCategory> updateWrapper = new UpdateWrapper<SysCategory>()
					.lambda()
					.in(SysCategory::getId,Arrays.asList(pids.split(",")))
					.set(SysCategory::getHasChild,"0");
			this.update(updateWrapper);
		}
	}

	/**
	 * 查詢節點下所有子節點
	 * @param ids
	 * @return
	 */
	private String queryTreeChildIds(String ids) {
		//獲取id數組
		String[] idArr = ids.split(",");
		StringBuffer sb = new StringBuffer();
		for (String pidVal : idArr) {
			if(pidVal != null){
				if(!sb.toString().contains(pidVal)){
					if(sb.toString().length() > 0){
						sb.append(",");
					}
					sb.append(pidVal);
					this.getTreeChildIds(pidVal,sb);
				}
			}
		}
		return sb.toString();
	}

	/**
	 * 查詢需修改標識的父節點ids
	 * @param ids
	 * @return
	 */
	private String queryTreePids(String ids) {
		StringBuffer sb = new StringBuffer();
		//獲取id數組
		String[] idArr = ids.split(",");
		for (String id : idArr) {
			if(id != null){
				SysCategory category = this.baseMapper.selectById(id);
				//根據id查詢pid值
				String metaPid = category.getPid();
				//查詢此節點上一級是否還有其他子節點
				LambdaQueryWrapper<SysCategory> queryWrapper = new LambdaQueryWrapper<>();
				queryWrapper.eq(SysCategory::getPid,metaPid);
				queryWrapper.notIn(SysCategory::getId,Arrays.asList(idArr));
				List<SysCategory> dataList = this.baseMapper.selectList(queryWrapper);
				if((dataList == null || dataList.size()==0) && !Arrays.asList(idArr).contains(metaPid)
						&& !sb.toString().contains(metaPid)){
					//如果當前節點原本有子節點 現在木有了，更新狀態
					sb.append(metaPid).append(",");
				}
			}
		}
		if(sb.toString().endsWith(",")){
			sb = sb.deleteCharAt(sb.length() - 1);
		}
		return sb.toString();
	}

	/**
	 * 遞歸 根據父id獲取子節點id
	 * @param pidVal
	 * @param sb
	 * @return
	 */
	private StringBuffer getTreeChildIds(String pidVal,StringBuffer sb){
		LambdaQueryWrapper<SysCategory> queryWrapper = new LambdaQueryWrapper<>();
		queryWrapper.eq(SysCategory::getPid,pidVal);
		List<SysCategory> dataList = baseMapper.selectList(queryWrapper);
		if(dataList != null && dataList.size()>0){
			for(SysCategory category : dataList) {
				if(!sb.toString().contains(category.getId())){
					sb.append(",").append(category.getId());
				}
				this.getTreeChildIds(category.getId(), sb);
			}
		}
		return sb;
	}

	@Override
	public List<String> loadDictItem(String ids) {
		return this.loadDictItem(ids, true);
	}

	@Override
	public List<String> loadDictItem(String ids, boolean delNotExist) {
		String[] idArray = ids.split(",");
		LambdaQueryWrapper<SysCategory> query = new LambdaQueryWrapper<>();
		query.in(SysCategory::getId, Arrays.asList(idArray));
		// 查詢數據
		List<SysCategory> list = super.list(query);
		// 取出name并返回
		List<String> textList;
		// update-begin--author:sunjianlei--date:20210514--for：新增delNotExist參數，設為false不刪除數據庫里不存在的key ----
		if (delNotExist) {
			textList = list.stream().map(SysCategory::getName).collect(Collectors.toList());
		} else {
			textList = new ArrayList<>();
			for (String id : idArray) {
				List<SysCategory> res = list.stream().filter(i -> id.equals(i.getId())).collect(Collectors.toList());
				textList.add(res.size() > 0 ? res.get(0).getName() : id);
			}
		}
		// update-end--author:sunjianlei--date:20210514--for：新增delNotExist參數，設為false不刪除數據庫里不存在的key ----
		return textList;
	}

}
