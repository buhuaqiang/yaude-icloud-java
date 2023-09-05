<#---->
<#-- freemarker 的一些工具方法 -->
<#---->
<#-- 駝峰轉其他字符 -->
<#-- @param str       待轉換的文本 -->
<#-- @param character 要轉換成的字符 -->
<#-- @param case      轉換大小寫（normal 不轉換，lower 小寫，upper 大寫） -->
<#function camelToChar(str, character, case='normal')>
  <#assign text=str?replace("([a-z])([A-Z]+)","$1${character}$2","r")/>
  <#if case=="upper">
    <#return text?upper_case>
  <#elseif case=="lower">
    <#return text?lower_case>
  <#else>
    <#return text>
  </#if>
</#function>
<#--下劃線轉駝峰-->
<#function dashedToCamel(str)>
    <#assign text=""/>
    <#assign strlist = str?split("_")/>
    <#list strlist as v>
        <#assign text=text+v?cap_first/>
    </#list>
    <#return text?uncap_first>
</#function>
<#-- 駝峰轉下劃線 -->
<#function camelToDashed(str, case='normal')>
  <#return camelToChar(str, "_", case)>
</#function>
<#---->
<#-- 駝峰轉橫線 -->
<#function camelToHorizontal(str, case='normal')>
  <#return camelToChar(str, "-", case)>
</#function>
<#---->
<#-- 獲取 v-model 屬性 -->
<#function getVModel po,suffix="">
  <#return "v-model=\"queryParam.${po.fieldName}${suffix}\"">
</#function>
<#-- 獲取 placeholder 屬性 -->
<#function getPlaceholder po,prefix,fillComment=true>
  <#if fillComment>
    <#return "placeholder=\"${prefix}${po.filedComment}\"">
  <#else>
    <#return "placeholder=\"${prefix}\"">
  </#if>
</#function>
<#-- ** 判斷某字段是否配置了校驗 * -->
<#function poHasCheck po>
  <#if (po.fieldValidType!'')?trim?length gt 0 || po.nullable == 'N'>
    <#if po.fieldName != 'id'>
      <#if po.nullable == 'N'
      || po.fieldValidType == '*'
      || po.fieldValidType == 'only'
      || po.fieldValidType == 'n6-16'
      || po.fieldValidType == '*6-16'
      || po.fieldValidType == 's6-18'
      || po.fieldValidType == 'url'
      || po.fieldValidType == 'e'
      || po.fieldValidType == 'm'
      || po.fieldValidType == 'p'
      || po.fieldValidType == 's'
      || po.fieldValidType == 'n'
      || po.fieldValidType == 'z'
      || po.fieldValidType == 'money'
      || po.fieldValidType != ''
      >
        <#return true>
      </#if>
    </#if>
  </#if>
  <#return false>
</#function>
<#-- ** 如果配置了校驗就顯示 validatorRules * -->
<#function autoWriteRules po>
  <#if poHasCheck(po)>
    <#return ", validatorRules.${po.fieldName}">
  <#else>
    <#return "">
  </#if>
</#function>

<#-- ** 如果Blob就顯示 String * -->
<#function autoStringSuffix po>
  <#if  po.fieldDbType=='Blob'>
    <#return "'${po.fieldName}String'">
  <#else>
    <#return "'${po.fieldName}'">
  </#if>
</#function>

<#-- ** 如果Blob就顯示model方式 String * -->
<#function autoStringSuffixForModel po>
    <#if  po.fieldDbType=='Blob'>
        <#return "${po.fieldName}String">
    <#else>
        <#return "${po.fieldName}">
    </#if>
</#function>

<#-- ** 高級查詢生成 * -->
<#function superQueryFieldList po>
    <#assign superQuery_dictTable="">
    <#assign superQuery_dictText="">
    <#if po.dictTable?default("")?trim?length gt 1>
        <#assign superQuery_dictTable="${po.dictTable}">
    </#if>
    <#if po.dictText?default("")?trim?length gt 1>
        <#assign superQuery_dictText="${po.dictText}">
    </#if>
  <#if po.classType=="popup">
      <#return "{type:'${po.classType}',value:'${po.fieldName}',text:'${po.filedComment}', popup:{code:'${po.dictTable}',field:'${po.dictField?split(',')[0]}',orgFields:'${po.dictField?split(',')[0]}',destFields:'${po.dictText?split(',')[0]}'}}">
  <#elseif po.classType=="sel_user" || po.classType=="sel_depart" || po.classType=="datetime" || po.classType=="date" || po.classType=="pca" || po.classType=="switch">
      <#return "{type:'${po.classType}',value:'${po.fieldName}',text:'${po.filedComment}'}">
  <#else>
      <#if po.classType=="sel_search" || po.classType=="list_multi">
          <#return "{type:'${po.classType}',value:'${po.fieldName}',text:'${po.filedComment}',dictTable:'${superQuery_dictTable}', dictText:'${superQuery_dictText}', dictCode:'${po.dictField}'}">
      <#elseif po.dictTable?? && po.dictTable!="" && po.classType!="sel_tree" && po.classType!="cat_tree" && po.classType!="link_down">
          <#return "{type:'${po.fieldDbType}',value:'${po.fieldName}',text:'${po.filedComment}',dictCode:'${po.dictTable},${po.dictText},${po.dictField}'}">
      <#elseif po.dictField?? && po.classType!="sel_tree" && po.classType!="cat_tree" && po.classType!="link_down">
          <#return "{type:'${po.fieldDbType}',value:'${po.fieldName}',text:'${po.filedComment}',dictCode:'${po.dictField}'}">
      <#elseif po.fieldDbType=="Text">
          <#return "{type:'string',value:'${po.fieldName}',text:'${po.filedComment}'}">
      <#elseif po.fieldDbType=="Blob">
          <#return "{type:'byte',value:'${po.fieldName}',text:'${po.filedComment}'}">
      <#elseif po.fieldDbType=="BigDecimal" || po.fieldDbType=="double">
          <#return "{type:'number',value:'${po.fieldName}',text:'${po.filedComment}'}">
      <#else>
          <#return "{type:'${po.fieldDbType}',value:'${po.fieldName}',text:'${po.filedComment}'}">
      </#if>
  </#if>
</#function>