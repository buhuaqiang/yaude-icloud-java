package com.yaude.common.util;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.*;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

/**
 * redis 工具類
 * @Author Scott
 *
 */
@Component
public class RedisUtil {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private StringRedisTemplate stringRedisTemplate;

	/**
	 * 指定緩存失效時間
	 * 
	 * @param key  鍵
	 * @param time 時間(秒)
	 * @return
	 */
	public boolean expire(String key, long time) {
		try {
			if (time > 0) {
				redisTemplate.expire(key, time, TimeUnit.SECONDS);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根據key 獲取過期時間
	 * 
	 * @param key 鍵 不能為null
	 * @return 時間(秒) 返回0代表為永久有效
	 */
	public long getExpire(String key) {
		return redisTemplate.getExpire(key, TimeUnit.SECONDS);
	}

	/**
	 * 判斷key是否存在
	 * 
	 * @param key 鍵
	 * @return true 存在 false不存在
	 */
	public boolean hasKey(String key) {
		try {
			return redisTemplate.hasKey(key);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 刪除緩存
	 * 
	 * @param key 可以傳一個值 或多個
	 */
	@SuppressWarnings("unchecked")
	public void del(String... key) {
		if (key != null && key.length > 0) {
			if (key.length == 1) {
				redisTemplate.delete(key[0]);
			} else {
				redisTemplate.delete(CollectionUtils.arrayToList(key));
			}
		}
	}

	// ============================String=============================
	/**
	 * 普通緩存獲取
	 * 
	 * @param key 鍵
	 * @return 值
	 */
	public Object get(String key) {
		return key == null ? null : redisTemplate.opsForValue().get(key);
	}

	/**
	 * 普通緩存放入
	 * 
	 * @param key   鍵
	 * @param value 值
	 * @return true成功 false失敗
	 */
	public boolean set(String key, Object value) {
		try {
			redisTemplate.opsForValue().set(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 普通緩存放入并設置時間
	 * 
	 * @param key   鍵
	 * @param value 值
	 * @param time  時間(秒) time要大于0 如果time小于等于0 將設置無限期
	 * @return true成功 false 失敗
	 */
	public boolean set(String key, Object value, long time) {
		try {
			if (time > 0) {
				redisTemplate.opsForValue().set(key, value, time, TimeUnit.SECONDS);
			} else {
				set(key, value);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 遞增
	 * 
	 * @param key 鍵
	 * @param by  要增加幾(大于0)
	 * @return
	 */
	public long incr(String key, long delta) {
		if (delta < 0) {
			throw new RuntimeException("遞增因子必須大于0");
		}
		return redisTemplate.opsForValue().increment(key, delta);
	}

	/**
	 * 遞減
	 * 
	 * @param key 鍵
	 * @param by  要減少幾(小于0)
	 * @return
	 */
	public long decr(String key, long delta) {
		if (delta < 0) {
			throw new RuntimeException("遞減因子必須大于0");
		}
		return redisTemplate.opsForValue().increment(key, -delta);
	}

	// ================================Map=================================
	/**
	 * HashGet
	 * 
	 * @param key  鍵 不能為null
	 * @param item 項 不能為null
	 * @return 值
	 */
	public Object hget(String key, String item) {
		return redisTemplate.opsForHash().get(key, item);
	}

	/**
	 * 獲取hashKey對應的所有鍵值
	 * 
	 * @param key 鍵
	 * @return 對應的多個鍵值
	 */
	public Map<Object, Object> hmget(String key) {
		return redisTemplate.opsForHash().entries(key);
	}

	/**
	 * HashSet
	 * 
	 * @param key 鍵
	 * @param map 對應多個鍵值
	 * @return true 成功 false 失敗
	 */
	public boolean hmset(String key, Map<String, Object> map) {
		try {
			redisTemplate.opsForHash().putAll(key, map);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * HashSet 并設置時間
	 * 
	 * @param key  鍵
	 * @param map  對應多個鍵值
	 * @param time 時間(秒)
	 * @return true成功 false失敗
	 */
	public boolean hmset(String key, Map<String, Object> map, long time) {
		try {
			redisTemplate.opsForHash().putAll(key, map);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 向一張hash表中放入數據,如果不存在將創建
	 * 
	 * @param key   鍵
	 * @param item  項
	 * @param value 值
	 * @return true 成功 false失敗
	 */
	public boolean hset(String key, String item, Object value) {
		try {
			redisTemplate.opsForHash().put(key, item, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 向一張hash表中放入數據,如果不存在將創建
	 * 
	 * @param key   鍵
	 * @param item  項
	 * @param value 值
	 * @param time  時間(秒) 注意:如果已存在的hash表有時間,這里將會替換原有的時間
	 * @return true 成功 false失敗
	 */
	public boolean hset(String key, String item, Object value, long time) {
		try {
			redisTemplate.opsForHash().put(key, item, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 刪除hash表中的值
	 * 
	 * @param key  鍵 不能為null
	 * @param item 項 可以使多個 不能為null
	 */
	public void hdel(String key, Object... item) {
		redisTemplate.opsForHash().delete(key, item);
	}

	/**
	 * 判斷hash表中是否有該項的值
	 * 
	 * @param key  鍵 不能為null
	 * @param item 項 不能為null
	 * @return true 存在 false不存在
	 */
	public boolean hHasKey(String key, String item) {
		return redisTemplate.opsForHash().hasKey(key, item);
	}

	/**
	 * hash遞增 如果不存在,就會創建一個 并把新增后的值返回
	 * 
	 * @param key  鍵
	 * @param item 項
	 * @param by   要增加幾(大于0)
	 * @return
	 */
	public double hincr(String key, String item, double by) {
		return redisTemplate.opsForHash().increment(key, item, by);
	}

	/**
	 * hash遞減
	 * 
	 * @param key  鍵
	 * @param item 項
	 * @param by   要減少記(小于0)
	 * @return
	 */
	public double hdecr(String key, String item, double by) {
		return redisTemplate.opsForHash().increment(key, item, -by);
	}

	// ============================set=============================
	/**
	 * 根據key獲取Set中的所有值
	 * 
	 * @param key 鍵
	 * @return
	 */
	public Set<Object> sGet(String key) {
		try {
			return redisTemplate.opsForSet().members(key);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根據value從一個set中查詢,是否存在
	 * 
	 * @param key   鍵
	 * @param value 值
	 * @return true 存在 false不存在
	 */
	public boolean sHasKey(String key, Object value) {
		try {
			return redisTemplate.opsForSet().isMember(key, value);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 將數據放入set緩存
	 * 
	 * @param key    鍵
	 * @param values 值 可以是多個
	 * @return 成功個數
	 */
	public long sSet(String key, Object... values) {
		try {
			return redisTemplate.opsForSet().add(key, values);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 將set數據放入緩存
	 * 
	 * @param key    鍵
	 * @param time   時間(秒)
	 * @param values 值 可以是多個
	 * @return 成功個數
	 */
	public long sSetAndTime(String key, long time, Object... values) {
		try {
			Long count = redisTemplate.opsForSet().add(key, values);
			if (time > 0) {
				expire(key, time);
			}
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 獲取set緩存的長度
	 * 
	 * @param key 鍵
	 * @return
	 */
	public long sGetSetSize(String key) {
		try {
			return redisTemplate.opsForSet().size(key);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 移除值為value的
	 * 
	 * @param key    鍵
	 * @param values 值 可以是多個
	 * @return 移除的個數
	 */
	public long setRemove(String key, Object... values) {
		try {
			Long count = redisTemplate.opsForSet().remove(key, values);
			return count;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	// ===============================list=================================

	/**
	 * 獲取list緩存的內容
	 * 
	 * @param key   鍵
	 * @param start 開始
	 * @param end   結束 0 到 -1代表所有值
	 * @return
	 */
	public List<Object> lGet(String key, long start, long end) {
		try {
			return redisTemplate.opsForList().range(key, start, end);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 獲取list緩存的長度
	 * 
	 * @param key 鍵
	 * @return
	 */
	public long lGetListSize(String key) {
		try {
			return redisTemplate.opsForList().size(key);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 通過索引 獲取list中的值
	 * 
	 * @param key   鍵
	 * @param index 索引 index>=0時， 0 表頭，1 第二個元素，依次類推；index<0時，-1，表尾，-2倒數第二個元素，依次類推
	 * @return
	 */
	public Object lGetIndex(String key, long index) {
		try {
			return redisTemplate.opsForList().index(key, index);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 將list放入緩存
	 * 
	 * @param key   鍵
	 * @param value 值
	 * @param time  時間(秒)
	 * @return
	 */
	public boolean lSet(String key, Object value) {
		try {
			redisTemplate.opsForList().rightPush(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 將list放入緩存
	 * 
	 * @param key   鍵
	 * @param value 值
	 * @param time  時間(秒)
	 * @return
	 */
	public boolean lSet(String key, Object value, long time) {
		try {
			redisTemplate.opsForList().rightPush(key, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 將list放入緩存
	 * 
	 * @param key   鍵
	 * @param value 值
	 * @param time  時間(秒)
	 * @return
	 */
	public boolean lSet(String key, List<Object> value) {
		try {
			redisTemplate.opsForList().rightPushAll(key, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 將list放入緩存
	 * 
	 * @param key   鍵
	 * @param value 值
	 * @param time  時間(秒)
	 * @return
	 */
	public boolean lSet(String key, List<Object> value, long time) {
		try {
			redisTemplate.opsForList().rightPushAll(key, value);
			if (time > 0) {
				expire(key, time);
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 根據索引修改list中的某條數據
	 * 
	 * @param key   鍵
	 * @param index 索引
	 * @param value 值
	 * @return
	 */
	public boolean lUpdateIndex(String key, long index, Object value) {
		try {
			redisTemplate.opsForList().set(key, index, value);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 移除N個值為value
	 * 
	 * @param key   鍵
	 * @param count 移除多少個
	 * @param value 值
	 * @return 移除的個數
	 */
	public long lRemove(String key, long count, Object value) {
		try {
			Long remove = redisTemplate.opsForList().remove(key, count, value);
			return remove;
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 *  獲取指定前綴的一系列key
	 *  使用scan命令代替keys, Redis是單線程處理，keys命令在KEY數量較多時，
	 *  操作效率極低【時間復雜度為O(N)】，該命令一旦執行會嚴重阻塞線上其它命令的正常請求
	 * @param keyPrefix
	 * @return
	 */
	private Set<String> keys(String keyPrefix) {
		String realKey = keyPrefix + "*";

		try {
			return redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
				Set<String> binaryKeys = new HashSet<>();
				Cursor<byte[]> cursor = connection.scan(new ScanOptions.ScanOptionsBuilder().match(realKey).count(Integer.MAX_VALUE).build());
				while (cursor.hasNext()) {
					binaryKeys.add(new String(cursor.next()));
				}

				return binaryKeys;
			});
		} catch (Throwable e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 *  刪除指定前綴的一系列key
	 * @param keyPrefix
	 */
	public void removeAll(String keyPrefix) {
		try {
			Set<String> keys = keys(keyPrefix);
			redisTemplate.delete(keys);
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}
