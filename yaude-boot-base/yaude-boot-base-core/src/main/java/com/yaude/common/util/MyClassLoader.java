package com.yaude.common.util;

/**
 * @Author  張代浩
 */
public class MyClassLoader extends ClassLoader {
	public static Class getClassByScn(String className) {
		Class myclass = null;
		try {
			myclass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException(className+" not found!");
		}
		return myclass;
	}

	// 獲得類的全名，包括包名
	public static String getPackPath(Object object) {
		// 檢查用戶傳入的參數是否為空
		if (object == null) {
			throw new java.lang.IllegalArgumentException("參數不能為空！");
		}
		// 獲得類的全名，包括包名
		String clsName = object.getClass().getName();
		return clsName;
	}

	public static String getAppPath(Class cls) {
		// 檢查用戶傳入的參數是否為空
		if (cls == null) {
			throw new java.lang.IllegalArgumentException("參數不能為空！");
		}
		ClassLoader loader = cls.getClassLoader();
		// 獲得類的全名，包括包名
		String clsName = cls.getName() + ".class";
		// 獲得傳入參數所在的包
		Package pack = cls.getPackage();
		String path = "";
		// 如果不是匿名包，將包名轉化為路徑
		if (pack != null) {
			String packName = pack.getName();
			// 此處簡單判定是否是Java基礎類庫，防止用戶傳入JDK內置的類庫
			if (packName.startsWith("java.") || packName.startsWith("javax.")) {
				throw new java.lang.IllegalArgumentException("不要傳送系統類！");
			}
			// 在類的名稱中，去掉包名的部分，獲得類的文件名
			clsName = clsName.substring(packName.length() + 1);
			// 判定包名是否是簡單包名，如果是，則直接將包名轉換為路徑，
			if (packName.indexOf(".") < 0) {
				path = packName + "/";
			} else {// 否則按照包名的組成部分，將包名轉換為路徑
				int start = 0, end = 0;
				end = packName.indexOf(".");
				while (end != -1) {
					path = path + packName.substring(start, end) + "/";
					start = end + 1;
					end = packName.indexOf(".", start);
				}
				path = path + packName.substring(start) + "/";
			}
		}
		// 調用ClassLoader的getResource方法，傳入包含路徑信息的類文件名
		java.net.URL url = loader.getResource(path + clsName);
		// 從URL對象中獲取路徑信息
		String realPath = url.getPath();
		// 去掉路徑信息中的協議名"file:"
		int pos = realPath.indexOf("file:");
		if (pos > -1) {
			realPath = realPath.substring(pos + 5);
		}
		// 去掉路徑信息最后包含類文件信息的部分，得到類所在的路徑
		pos = realPath.indexOf(path + clsName);
		realPath = realPath.substring(0, pos - 1);
		// 如果類文件被打包到JAR等文件中時，去掉對應的JAR等打包文件名
		if (realPath.endsWith("!")) {
			realPath = realPath.substring(0, realPath.lastIndexOf("/"));
		}
		/*------------------------------------------------------------  
		 ClassLoader的getResource方法使用了utf-8對路徑信息進行了編碼，當路徑  
		  中存在中文和空格時，他會對這些字符進行轉換，這樣，得到的往往不是我們想要  
		  的真實路徑，在此，調用了URLDecoder的decode方法進行解碼，以便得到原始的  
		  中文及空格路徑  
		-------------------------------------------------------------*/
		try {
			realPath = java.net.URLDecoder.decode(realPath, "utf-8");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return realPath;
	}// getAppPath定義結束
}
