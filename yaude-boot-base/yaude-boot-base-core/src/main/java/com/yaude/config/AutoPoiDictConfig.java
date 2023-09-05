package com.yaude.config;

import lombok.extern.slf4j.Slf4j;
import com.yaude.common.api.CommonAPI;
import com.yaude.common.system.vo.DictModel;
import com.yaude.common.util.oConvertUtils;
import org.jeecgframework.dict.service.AutoPoiDictServiceI;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * 描述：AutoPoi Excel注解支持字典參數設置
 *  舉例： @Excel(name = "性別", width = 15, dicCode = "sex")
 * 1、導出的時候會根據字典配置，把值1,2翻譯成：男、女;
 * 2、導入的時候，會把男、女翻譯成1,2存進數據庫;
 * 
 * @Author:scott 
 * @since：2019-04-09 
 * @Version:1.0
 */
@Slf4j
@Service
public class AutoPoiDictConfig implements AutoPoiDictServiceI {
	@Lazy
	@Resource
	private CommonAPI commonAPI;

	/**
	 * 通過字典查詢easypoi，所需字典文本
	 * 
	 * @Author:scott 
	 * @since：2019-04-09
	 * @return
	 */
	@Override
	public String[] queryDict(String dicTable, String dicCode, String dicText) {
		List<String> dictReplaces = new ArrayList<String>();
		List<DictModel> dictList = null;
		// step.1 如果沒有字典表則使用系統字典表
		if (oConvertUtils.isEmpty(dicTable)) {
			dictList = commonAPI.queryDictItemsByCode(dicCode);
		} else {
			try {
				dicText = oConvertUtils.getString(dicText, dicCode);
				dictList = commonAPI.queryTableDictItemsByCode(dicTable, dicText, dicCode);
			} catch (Exception e) {
				log.error(e.getMessage(),e);
			}
		}
		for (DictModel t : dictList) {
			if(t!=null){
				dictReplaces.add(t.getText() + "_" + t.getValue());
			}
		}
		if (dictReplaces != null && dictReplaces.size() != 0) {
			log.info("---AutoPoi--Get_DB_Dict------"+ dictReplaces.toString());
			return dictReplaces.toArray(new String[dictReplaces.size()]);
		}
		return null;
	}
}
