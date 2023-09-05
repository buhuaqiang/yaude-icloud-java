package com.yaude;

import java.util.ArrayList;
import java.util.List;

import org.jeecgframework.codegenerate.generate.impl.CodeGenerateOneToMany;
import org.jeecgframework.codegenerate.generate.pojo.onetomany.MainTableVo;
import org.jeecgframework.codegenerate.generate.pojo.onetomany.SubTableVo;

/**
 * 代碼生成器入口【一對多】
 * 【 GUI模式已棄用，請轉移Online模式進行代碼生成 】
 * @Author 張代浩
 * @site www.jeecg.org
 * 
 */
public class JeecgOneToMainUtil {

	/**
	 * 一對多(父子表)數據模型，生成方法
	 * @param args
	 */
	public static void main(String[] args) {
		//第一步：設置主表配置
		MainTableVo mainTable = new MainTableVo();
		mainTable.setTableName("jeecg_order_main");//表名
		mainTable.setEntityName("GuiTestOrderMain");	 //實體名
		mainTable.setEntityPackage("gui");	 //包名
		mainTable.setFtlDescription("GUI訂單管理");	 //描述
		
		//第二步：設置子表集合配置
		List<SubTableVo> subTables = new ArrayList<SubTableVo>();
		//[1].子表一
		SubTableVo po = new SubTableVo();
		po.setTableName("jeecg_order_customer");//表名
		po.setEntityName("GuiTestOrderCustom");	    //實體名
		po.setEntityPackage("gui");	        //包名
		po.setFtlDescription("客戶明細");       //描述
		//子表外鍵參數配置
		/*說明: 
		 * a) 子表引用主表主鍵ID作為外鍵，外鍵字段必須以_ID結尾;
		 * b) 主表和子表的外鍵字段名字，必須相同（除主鍵ID外）;
		 * c) 多個外鍵字段，采用逗號分隔;
		*/
		po.setForeignKeys(new String[]{"order_id"});
		subTables.add(po);
		//[2].子表二
		SubTableVo po2 = new SubTableVo();
		po2.setTableName("jeecg_order_ticket");		//表名
		po2.setEntityName("GuiTestOrderTicket");			//實體名
		po2.setEntityPackage("gui"); 				//包名
		po2.setFtlDescription("產品明細");			//描述
		//子表外鍵參數配置
		/*說明: 
		 * a) 子表引用主表主鍵ID作為外鍵，外鍵字段必須以_ID結尾;
		 * b) 主表和子表的外鍵字段名字，必須相同（除主鍵ID外）;
		 * c) 多個外鍵字段，采用逗號分隔;
		*/
		po2.setForeignKeys(new String[]{"order_id"});
		subTables.add(po2);
		mainTable.setSubTables(subTables);
		
		//第三步：一對多(父子表)數據模型,代碼生成
		try {
			new CodeGenerateOneToMany(mainTable,subTables).generateCodeFile(null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
