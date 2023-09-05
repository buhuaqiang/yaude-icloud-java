package com.yaude.modules.system.vo;

import com.yaude.modules.system.entity.SysTranslate;
import lombok.Data;


@Data
public class SysTranslateVO extends SysTranslate {
	private String text;

	private String keyword;

}
