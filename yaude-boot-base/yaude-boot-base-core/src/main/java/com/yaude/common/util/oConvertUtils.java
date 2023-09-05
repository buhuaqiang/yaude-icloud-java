package com.yaude.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.BeanUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @Author  張代浩
 *
 */
@Slf4j
public class oConvertUtils {
	public static boolean isEmpty(Object object) {
		if (object == null) {
			return (true);
		}
		if ("".equals(object)) {
			return (true);
		}
		if ("null".equals(object)) {
			return (true);
		}
		return (false);
	}
	
	public static boolean isNotEmpty(Object object) {
		if (object != null && !object.equals("") && !object.equals("null")) {
			return (true);
		}
		return (false);
	}

	public static String decode(String strIn, String sourceCode, String targetCode) {
		String temp = code2code(strIn, sourceCode, targetCode);
		return temp;
	}

	public static String StrToUTF(String strIn, String sourceCode, String targetCode) {
		strIn = "";
		try {
			strIn = new String(strIn.getBytes("ISO-8859-1"), "GBK");
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return strIn;

	}

	private static String code2code(String strIn, String sourceCode, String targetCode) {
		String strOut = null;
		if (strIn == null || (strIn.trim()).equals("")) {
			return strIn;
		}
		try {
			byte[] b = strIn.getBytes(sourceCode);
			for (int i = 0; i < b.length; i++) {
				System.out.print(b[i] + "  ");
			}
			strOut = new String(b, targetCode);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return strOut;
	}

	public static int getInt(String s, int defval) {
		if (s == null || s == "") {
			return (defval);
		}
		try {
			return (Integer.parseInt(s));
		} catch (NumberFormatException e) {
			return (defval);
		}
	}

	public static int getInt(String s) {
		if (s == null || s == "") {
			return 0;
		}
		try {
			return (Integer.parseInt(s));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static int getInt(String s, Integer df) {
		if (s == null || s == "") {
			return df;
		}
		try {
			return (Integer.parseInt(s));
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static Integer[] getInts(String[] s) {
		Integer[] integer = new Integer[s.length];
		if (s == null) {
			return null;
		}
		for (int i = 0; i < s.length; i++) {
			integer[i] = Integer.parseInt(s[i]);
		}
		return integer;

	}

	public static double getDouble(String s, double defval) {
		if (s == null || s == "") {
			return (defval);
		}
		try {
			return (Double.parseDouble(s));
		} catch (NumberFormatException e) {
			return (defval);
		}
	}

	public static double getDou(Double s, double defval) {
		if (s == null) {
			return (defval);
		}
		return s;
	}

	/*public static Short getShort(String s) {
		if (StringUtil.isNotEmpty(s)) {
			return (Short.parseShort(s));
		} else {
			return null;
		}
	}*/

	public static int getInt(Object object, int defval) {
		if (isEmpty(object)) {
			return (defval);
		}
		try {
			return (Integer.parseInt(object.toString()));
		} catch (NumberFormatException e) {
			return (defval);
		}
	}
	
	public static Integer getInt(Object object) {
		if (isEmpty(object)) {
			return null;
		}
		try {
			return (Integer.parseInt(object.toString()));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static int getInt(BigDecimal s, int defval) {
		if (s == null) {
			return (defval);
		}
		return s.intValue();
	}

	public static Integer[] getIntegerArry(String[] object) {
		int len = object.length;
		Integer[] result = new Integer[len];
		try {
			for (int i = 0; i < len; i++) {
				result[i] = new Integer(object[i].trim());
			}
			return result;
		} catch (NumberFormatException e) {
			return null;
		}
	}

	public static String getString(String s) {
		return (getString(s, ""));
	}

	/**
	 * 轉義成Unicode編碼
	 * @param s
	 * @return
	 */
	/*public static String escapeJava(Object s) {
		return StringEscapeUtils.escapeJava(getString(s));
	}*/
	
	public static String getString(Object object) {
		if (isEmpty(object)) {
			return "";
		}
		return (object.toString().trim());
	}

	public static String getString(int i) {
		return (String.valueOf(i));
	}

	public static String getString(float i) {
		return (String.valueOf(i));
	}

	public static String getString(String s, String defval) {
		if (isEmpty(s)) {
			return (defval);
		}
		return (s.trim());
	}

	public static String getString(Object s, String defval) {
		if (isEmpty(s)) {
			return (defval);
		}
		return (s.toString().trim());
	}

	public static long stringToLong(String str) {
		Long test = new Long(0);
		try {
			test = Long.valueOf(str);
		} catch (Exception e) {
		}
		return test.longValue();
	}

	/**
	 * 獲取本機IP
	 */
	public static String getIp() {
		String ip = null;
		try {
			InetAddress address = InetAddress.getLocalHost();
			ip = address.getHostAddress();

		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return ip;
	}

	/**
	 * 判斷一個類是否為基本數據類型。
	 * 
	 * @param clazz
	 *            要判斷的類。
	 * @return true 表示為基本數據類型。
	 */
	private static boolean isBaseDataType(Class clazz) throws Exception {
		return (clazz.equals(String.class) || clazz.equals(Integer.class) || clazz.equals(Byte.class) || clazz.equals(Long.class) || clazz.equals(Double.class) || clazz.equals(Float.class) || clazz.equals(Character.class) || clazz.equals(Short.class) || clazz.equals(BigDecimal.class) || clazz.equals(BigInteger.class) || clazz.equals(Boolean.class) || clazz.equals(Date.class) || clazz.isPrimitive());
	}

	/**
	 * @param request
	 *            IP
	 * @return IP Address
	 */
	public static String getIpAddrByRequest(HttpServletRequest request) {
		String ip = request.getHeader("x-forwarded-for");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	/**
	 * @return 本機IP
	 * @throws SocketException
	 */
	public static String getRealIp() throws SocketException {
		String localip = null;// 本地IP，如果沒有配置外網IP則返回它
		String netip = null;// 外網IP

		Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
		InetAddress ip = null;
		boolean finded = false;// 是否找到外網IP
		while (netInterfaces.hasMoreElements() && !finded) {
			NetworkInterface ni = netInterfaces.nextElement();
			Enumeration<InetAddress> address = ni.getInetAddresses();
			while (address.hasMoreElements()) {
				ip = address.nextElement();
				if (!ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 外網IP
					netip = ip.getHostAddress();
					finded = true;
					break;
				} else if (ip.isSiteLocalAddress() && !ip.isLoopbackAddress() && ip.getHostAddress().indexOf(":") == -1) {// 內網IP
					localip = ip.getHostAddress();
				}
			}
		}

		if (netip != null && !"".equals(netip)) {
			return netip;
		} else {
			return localip;
		}
	}

	/**
	 * java去除字符串中的空格、回車、換行符、制表符
	 * 
	 * @param str
	 * @return
	 */
	public static String replaceBlank(String str) {
		String dest = "";
		if (str != null) {
			Pattern p = Pattern.compile("\\s*|\t|\r|\n");
			Matcher m = p.matcher(str);
			dest = m.replaceAll("");
		}
		return dest;

	}

	/**
	 * 判斷元素是否在數組內
	 * 
	 * @param substring
	 * @param source
	 * @return
	 */
	public static boolean isIn(String substring, String[] source) {
		if (source == null || source.length == 0) {
			return false;
		}
		for (int i = 0; i < source.length; i++) {
			String aSource = source[i];
			if (aSource.equals(substring)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 獲取Map對象
	 */
	public static Map<Object, Object> getHashMap() {
		return new HashMap<Object, Object>();
	}

	/**
	 * SET轉換MAP
	 * 
	 * @param str
	 * @return
	 */
	public static Map<Object, Object> SetToMap(Set<Object> setobj) {
		Map<Object, Object> map = getHashMap();
		for (Iterator iterator = setobj.iterator(); iterator.hasNext();) {
			Map.Entry<Object, Object> entry = (Map.Entry<Object, Object>) iterator.next();
			map.put(entry.getKey().toString(), entry.getValue() == null ? "" : entry.getValue().toString().trim());
		}
		return map;

	}

	public static boolean isInnerIP(String ipAddress) {
		boolean isInnerIp = false;
		long ipNum = getIpNum(ipAddress);
		/**
		 * 私有IP：A類 10.0.0.0-10.255.255.255 B類 172.16.0.0-172.31.255.255 C類 192.168.0.0-192.168.255.255 當然，還有127這個網段是環回地址
		 **/
		long aBegin = getIpNum("10.0.0.0");
		long aEnd = getIpNum("10.255.255.255");
		long bBegin = getIpNum("172.16.0.0");
		long bEnd = getIpNum("172.31.255.255");
		long cBegin = getIpNum("192.168.0.0");
		long cEnd = getIpNum("192.168.255.255");
		isInnerIp = isInner(ipNum, aBegin, aEnd) || isInner(ipNum, bBegin, bEnd) || isInner(ipNum, cBegin, cEnd) || ipAddress.equals("127.0.0.1");
		return isInnerIp;
	}

	private static long getIpNum(String ipAddress) {
		String[] ip = ipAddress.split("\\.");
		long a = Integer.parseInt(ip[0]);
		long b = Integer.parseInt(ip[1]);
		long c = Integer.parseInt(ip[2]);
		long d = Integer.parseInt(ip[3]);

		long ipNum = a * 256 * 256 * 256 + b * 256 * 256 + c * 256 + d;
		return ipNum;
	}

	private static boolean isInner(long userIp, long begin, long end) {
		return (userIp >= begin) && (userIp <= end);
	}
	
	/**
	 * 將下劃線大寫方式命名的字符串轉換為駝峰式。
	 * 如果轉換前的下劃線大寫方式命名的字符串為空，則返回空字符串。</br>
	 * 例如：hello_world->helloWorld
	 * 
	 * @param name
	 *            轉換前的下劃線大寫方式命名的字符串
	 * @return 轉換后的駝峰式命名的字符串
	 */
	public static String camelName(String name) {
		StringBuilder result = new StringBuilder();
		// 快速檢查
		if (name == null || name.isEmpty()) {
			// 沒必要轉換
			return "";
		} else if (!name.contains("_")) {
			// 不含下劃線，僅將首字母小寫
			//update-begin--Author:zhoujf  Date:20180503 for：TASK #2500 【代碼生成器】代碼生成器開發一通用模板生成功能
			//update-begin--Author:zhoujf  Date:20180503 for：TASK #2500 【代碼生成器】代碼生成器開發一通用模板生成功能
			return name.substring(0, 1).toLowerCase() + name.substring(1).toLowerCase();
			//update-end--Author:zhoujf  Date:20180503 for：TASK #2500 【代碼生成器】代碼生成器開發一通用模板生成功能
		}
		// 用下劃線將原始字符串分割
		String camels[] = name.split("_");
		for (String camel : camels) {
			// 跳過原始字符串中開頭、結尾的下換線或雙重下劃線
			if (camel.isEmpty()) {
				continue;
			}
			// 處理真正的駝峰片段
			if (result.length() == 0) {
				// 第一個駝峰片段，全部字母都小寫
				result.append(camel.toLowerCase());
			} else {
				// 其他的駝峰片段，首字母大寫
				result.append(camel.substring(0, 1).toUpperCase());
				result.append(camel.substring(1).toLowerCase());
			}
		}
		return result.toString();
	}
	
	/**
	 * 將下劃線大寫方式命名的字符串轉換為駝峰式。
	 * 如果轉換前的下劃線大寫方式命名的字符串為空，則返回空字符串。</br>
	 * 例如：hello_world,test_id->helloWorld,testId
	 * 
	 * @param name
	 *            轉換前的下劃線大寫方式命名的字符串
	 * @return 轉換后的駝峰式命名的字符串
	 */
	public static String camelNames(String names) {
		if(names==null||names.equals("")){
			return null;
		}
		StringBuffer sf = new StringBuffer();
		String[] fs = names.split(",");
		for (String field : fs) {
			field = camelName(field);
			sf.append(field + ",");
		}
		String result = sf.toString();
		return result.substring(0, result.length() - 1);
	}
	
	//update-begin--Author:zhoujf  Date:20180503 for：TASK #2500 【代碼生成器】代碼生成器開發一通用模板生成功能
	/**
	 * 將下劃線大寫方式命名的字符串轉換為駝峰式。(首字母寫)
	 * 如果轉換前的下劃線大寫方式命名的字符串為空，則返回空字符串。</br>
	 * 例如：hello_world->HelloWorld
	 * 
	 * @param name
	 *            轉換前的下劃線大寫方式命名的字符串
	 * @return 轉換后的駝峰式命名的字符串
	 */
	public static String camelNameCapFirst(String name) {
		StringBuilder result = new StringBuilder();
		// 快速檢查
		if (name == null || name.isEmpty()) {
			// 沒必要轉換
			return "";
		} else if (!name.contains("_")) {
			// 不含下劃線，僅將首字母小寫
			return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
		}
		// 用下劃線將原始字符串分割
		String camels[] = name.split("_");
		for (String camel : camels) {
			// 跳過原始字符串中開頭、結尾的下換線或雙重下劃線
			if (camel.isEmpty()) {
				continue;
			}
			// 其他的駝峰片段，首字母大寫
			result.append(camel.substring(0, 1).toUpperCase());
			result.append(camel.substring(1).toLowerCase());
		}
		return result.toString();
	}
	//update-end--Author:zhoujf  Date:20180503 for：TASK #2500 【代碼生成器】代碼生成器開發一通用模板生成功能
	
	/**
	 * 將駝峰命名轉化成下劃線
	 * @param para
	 * @return
	 */
	public static String camelToUnderline(String para){
        if(para.length()<3){
        	return para.toLowerCase(); 
        }
        StringBuilder sb=new StringBuilder(para);
        int temp=0;//定位
        //從第三個字符開始 避免命名不規范 
        for(int i=2;i<para.length();i++){
            if(Character.isUpperCase(para.charAt(i))){
                sb.insert(i+temp, "_");
                temp+=1;
            }
        }
        return sb.toString().toLowerCase(); 
	}

	/**
	 * 隨機數
	 * @param place 定義隨機數的位數
	 */
	public static String randomGen(int place) {
		String base = "qwertyuioplkjhgfdsazxcvbnmQAZWSXEDCRFVTGBYHNUJMIKLOP0123456789";
		StringBuffer sb = new StringBuffer();
		Random rd = new Random();
		for(int i=0;i<place;i++) {
			sb.append(base.charAt(rd.nextInt(base.length())));
		}
		return sb.toString();
	}
	
	/**
	 * 獲取類的所有屬性，包括父類
	 * 
	 * @param object
	 * @return
	 */
	public static Field[] getAllFields(Object object) {
		Class<?> clazz = object.getClass();
		List<Field> fieldList = new ArrayList<>();
		while (clazz != null) {
			fieldList.addAll(new ArrayList<>(Arrays.asList(clazz.getDeclaredFields())));
			clazz = clazz.getSuperclass();
		}
		Field[] fields = new Field[fieldList.size()];
		fieldList.toArray(fields);
		return fields;
	}
	
	/**
	  * 將map的key全部轉成小寫
	 * @param list
	 * @return
	 */
	public static List<Map<String, Object>> toLowerCasePageList(List<Map<String, Object>> list){
		List<Map<String, Object>> select = new ArrayList<>();
		for (Map<String, Object> row : list) {
			 Map<String, Object> resultMap = new HashMap<>();
			 Set<String> keySet = row.keySet(); 
			 for (String key : keySet) { 
				 String newKey = key.toLowerCase(); 
				 resultMap.put(newKey, row.get(key)); 
			 }
			 select.add(resultMap);
		}
		return select;
	}

	/**
	 * 將entityList轉換成modelList
	 * @param fromList
	 * @param tClass
	 * @param <F>
	 * @param <T>
	 * @return
	 */
	public static<F,T> List<T> entityListToModelList(List<F> fromList, Class<T> tClass){
		if(fromList == null || fromList.isEmpty()){
			return null;
		}
		List<T> tList = new ArrayList<>();
		for(F f : fromList){
			T t = entityToModel(f, tClass);
			tList.add(t);
		}
		return tList;
	}

	public static<F,T> T entityToModel(F entity, Class<T> modelClass) {
		log.debug("entityToModel : Entity屬性的值賦值到Model");
		Object model = null;
		if (entity == null || modelClass ==null) {
			return null;
		}

		try {
			model = modelClass.newInstance();
		} catch (InstantiationException e) {
			log.error("entityToModel : 實例化異常", e);
		} catch (IllegalAccessException e) {
			log.error("entityToModel : 安全權限異常", e);
		}
		BeanUtils.copyProperties(entity, model);
		return (T)model;
	}

	/**
	 * 判斷 list 是否為空
	 *
	 * @param list
	 * @return true or false
	 * list == null		: true
	 * list.size() == 0	: true
	 */
	public static boolean listIsEmpty(Collection list) {
		return (list == null || list.size() == 0);
	}

	/**
	 * 判斷 list 是否不為空
	 *
	 * @param list
	 * @return true or false
	 * list == null		: false
	 * list.size() == 0	: false
	 */
	public static boolean listIsNotEmpty(Collection list) {
		return !listIsEmpty(list);
	}

	/**
	 * 讀取靜態文本內容
	 * @param url
	 * @return
	 */
	public static String readStatic(String url) {
		String json = "";
		try {
			//換個寫法，解決springboot讀取jar包中文件的問題
			InputStream stream = oConvertUtils.class.getClassLoader().getResourceAsStream(url.replace("classpath:", ""));
			json = IOUtils.toString(stream,"UTF-8");
		} catch (IOException e) {
			log.error(e.getMessage(),e);
		}
		return json;
	}
}
