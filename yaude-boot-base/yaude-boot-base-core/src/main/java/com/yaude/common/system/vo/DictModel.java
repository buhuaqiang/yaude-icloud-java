package com.yaude.common.system.vo;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DictModel implements Serializable{
	private static final long serialVersionUID = 1L;

	public DictModel() {
	}
	
	public DictModel(String value, String text) {
		this.value = value;
		this.text = text;
	}
	public DictModel(String value, String text,String id) {
		this.value = value;
		this.text = text;
		this.dictItemId = id;
	}
	
	/**
	 * 字典value
	 */
	private String value;
	/**
	 * 字典文本
	 */
	private String text;

	/**
	 * 字典id
	 */
	private String dictItemId;

	/**
	 * 特殊用途： JgEditableTable
	 * @return
	 */
	public String getTitle() {
		return this.text;
	}

}
