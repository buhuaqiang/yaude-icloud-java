package com.yaude.common.util;

import io.netty.util.internal.StringUtil;

/**
 * 流水號生成規則(按默認規則遞增，數字從1-99開始遞增，數字到99，遞增字母;位數不夠增加位數)
 * A001
 * A001A002
 * @Author zhangdaihao
 *
 */
public class YouBianCodeUtil {

	// 數字位數(默認生成3位的數字)

	private static final int numLength = 2;//代表數字位數

	public static final int zhanweiLength = 1+numLength;

	/**
	 * 根據前一個code，獲取同級下一個code
	 * 例如:當前最大code為D01A04，下一個code為：D01A05
	 * 
	 * @param code
	 * @return
	 */
	public static synchronized String getNextYouBianCode(String code) {
		String newcode = "";
		if (oConvertUtils.isEmpty(code)) {
			String zimu = "A";
			String num = getStrNum(1);
			newcode = zimu + num;
		} else {
			String before_code = code.substring(0, code.length() - 1- numLength);
			String after_code = code.substring(code.length() - 1 - numLength,code.length());
			char after_code_zimu = after_code.substring(0, 1).charAt(0);
			Integer after_code_num = Integer.parseInt(after_code.substring(1));
//			org.jeecgframework.core.util.LogUtil.info(after_code);
//			org.jeecgframework.core.util.LogUtil.info(after_code_zimu);
//			org.jeecgframework.core.util.LogUtil.info(after_code_num);

			String nextNum = "";
			char nextZimu = 'A';
			// 先判斷數字等于999*，則計數從1重新開始，遞增
			if (after_code_num == getMaxNumByLength(numLength)) {
				nextNum = getNextStrNum(0);
			} else {
				nextNum = getNextStrNum(after_code_num);
			}
			// 先判斷數字等于999*，則字母從A重新開始,遞增
			if(after_code_num == getMaxNumByLength(numLength)) {
				nextZimu = getNextZiMu(after_code_zimu);
			}else{
				nextZimu = after_code_zimu;
			}

			// 例如Z99，下一個code就是Z99A01
			if ('Z' == after_code_zimu && getMaxNumByLength(numLength) == after_code_num) {
				newcode = code + (nextZimu + nextNum);
			} else {
				newcode = before_code + (nextZimu + nextNum);
			}
		}
		return newcode;

	}

	/**
	 * 根據父親code,獲取下級的下一個code
	 * 
	 * 例如：父親CODE:A01
	 *       當前CODE:A01B03
	 *       獲取的code:A01B04
	 *       
	 * @param parentCode   上級code
	 * @param localCode    同級code
	 * @return
	 */
	public static synchronized String getSubYouBianCode(String parentCode,String localCode) {
		if(localCode!=null && localCode!=""){

//			return parentCode + getNextYouBianCode(localCode);
			return getNextYouBianCode(localCode);

		}else{
			parentCode = parentCode + "A"+ getNextStrNum(0);
		}
		return parentCode;
	}

	

	/**
	 * 將數字前面位數補零
	 * 
	 * @param num
	 * @return
	 */
	private static String getNextStrNum(int num) {
		return getStrNum(getNextNum(num));
	}

	/**
	 * 將數字前面位數補零
	 * 
	 * @param num
	 * @return
	 */
	private static String getStrNum(int num) {
		String s = String.format("%0" + numLength + "d", num);
		return s;
	}

	/**
	 * 遞增獲取下個數字
	 * 
	 * @param num
	 * @return
	 */
	private static int getNextNum(int num) {
		num++;
		return num;
	}

	/**
	 * 遞增獲取下個字母
	 * 
	 * @param num
	 * @return
	 */
	private static char getNextZiMu(char zimu) {
		if (zimu == 'Z') {
			return 'A';
		}
		zimu++;
		return zimu;
	}
	
	/**
	 * 根據數字位數獲取最大值
	 * @param length
	 * @return
	 */
	private static int getMaxNumByLength(int length){
		if(length==0){
			return 0;
		}
		String max_num = "";
		for (int i=0;i<length;i++){
			max_num = max_num + "9";
		}
		return Integer.parseInt(max_num);
	}
	public static String[] cutYouBianCode(String code){
		if(code==null || StringUtil.isNullOrEmpty(code)){
			return null;
		}else{
			//獲取標準長度為numLength+1,截取的數量為code.length/numLength+1
			int c = code.length()/(numLength+1);
			String[] cutcode = new String[c];
			for(int i =0 ; i <c;i++){
				cutcode[i] = code.substring(0,(i+1)*(numLength+1));
			}
			return cutcode;
		}
		
	}
//	public static void main(String[] args) {
//		// org.jeecgframework.core.util.LogUtil.info(getNextZiMu('C'));
//		// org.jeecgframework.core.util.LogUtil.info(getNextNum(8));
//	    // org.jeecgframework.core.util.LogUtil.info(cutYouBianCode("C99A01B01")[2]);
//	}
}
