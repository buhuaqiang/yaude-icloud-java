<#include "../utils.ftl">
<#if po.isShow == 'Y' && poHasCheck(po)>
    <#if po.fieldName != 'id'>
           ${po.fieldName}: [
        <#assign fieldValidType = po.fieldValidType!''>
    <#-- 非空校驗 -->
        <#if po.nullable == 'N' || fieldValidType == '*'>
              { required: true, message: '請輸入${po.filedComment}!'},
        <#elseif fieldValidType!=''>
              { required: false},
        </#if>
    <#-- 唯一校驗 -->
        <#if fieldValidType == 'only'>
              { validator: (rule, value, callback) => validateDuplicateValue(<#if sub?default("")?trim?length gt 1>'${sub.tableName}'<#else>'${tableName}'</#if>, '${po.fieldDbName}', value, this.model.id, callback)},
        <#-- 6到16位數字 -->
        <#elseif fieldValidType == 'n6-16'>
              { pattern: /^\d{6,16}$/, message: '請輸入6到16位數字!'},
        <#-- 6到16位任意字符 -->
        <#elseif fieldValidType == '*6-16'>
              { pattern: /^.{6,16}$/, message: '請輸入6到16位任意字符!'},
        <#-- 6到18位字符串 -->
        <#elseif fieldValidType == 's6-18'>
              { pattern: /^.{6,18}$/, message: '請輸入6到18位任意字符!'},
        <#-- 網址 -->
        <#elseif fieldValidType == 'url'>
              { pattern: /^((ht|f)tps?):\/\/[\w\-]+(\.[\w\-]+)+([\w\-.,@?^=%&:\/~+#]*[\w\-@?^=%&\/~+#])?$/, message: '請輸入正確的網址!'},
        <#-- 電子郵件 -->
        <#elseif fieldValidType == 'e'>
              { pattern: /^([\w]+\.*)([\w]+)@[\w]+\.\w{3}(\.\w{2}|)$/, message: '請輸入正確的電子郵件!'},
        <#-- 手機號碼 -->
        <#elseif fieldValidType == 'm'>
              { pattern: /^1[3456789]\d{9}$/, message: '請輸入正確的手機號碼!'},
        <#-- 郵政編碼 -->
        <#elseif fieldValidType == 'p'>
              { pattern: /^[1-9]\d{5}$/, message: '請輸入正確的郵政編碼!'},
        <#-- 字母 -->
        <#elseif fieldValidType == 's'>
              { pattern: /^[A-Z|a-z]+$/, message: '請輸入字母!'},
        <#-- 數字 -->
        <#elseif fieldValidType == 'n'>
              { pattern: /^-?\d+\.?\d*$/, message: '請輸入數字!'},
        <#-- 整數 -->
        <#elseif fieldValidType == 'z'>
              { pattern: /^-?\d+$/, message: '請輸入整數!'},
        <#-- 金額 -->
        <#elseif fieldValidType == 'money'>
              { pattern: /^(([1-9][0-9]*)|([0]\.\d{0,2}|[1-9][0-9]*\.\d{0,2}))$/, message: '請輸入正確的金額!'},
        <#-- 正則校驗 -->
        <#elseif fieldValidType != '' && fieldValidType != '*'>
              { pattern: '${fieldValidType}', message: '不符合校驗規則!'},
        <#-- 無校驗 -->
        <#else>
            <#t>
        </#if>
           ],
    </#if>
</#if>