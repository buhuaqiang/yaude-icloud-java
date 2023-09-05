package com.yaude.common.api;

import com.yaude.common.api.vo.OaWpsModel;

/**
 * @Description: WPS通用接口
 * @Author: wangshuai
 * @Date:20200709
 * @Version:V1.0
 */
public interface IWpsBaseAPI {

  /*根據模板id獲取模板信息*/
  OaWpsModel getById(String id);

  /*根據文件路徑下載文件*/
 void downloadOosFiles(String objectName, String basePath,String fileName);

 /*WPS 設置數據存儲，用于邏輯判斷*/
 void context(String type,String text);

 /*刪除WPS模板相關信息*/
 void deleteById(String id);
}
