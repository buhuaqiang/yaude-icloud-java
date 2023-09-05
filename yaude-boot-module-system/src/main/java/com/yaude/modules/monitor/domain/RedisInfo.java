package com.yaude.modules.monitor.domain;

import java.util.HashMap;
import java.util.Map;

public class RedisInfo {

	private static Map<String, String> map = new HashMap<>();

	static {
		map.put("redis_version", "Redis 服務器版本");
		map.put("redis_git_sha1", "Git SHA1");
		map.put("redis_git_dirty", "Git dirty flag");
		map.put("os", "Redis 服務器的宿主操作系統");
		map.put("arch_bits", " 架構（32 或 64 位）");
		map.put("multiplexing_api", "Redis 所使用的事件處理機制");
		map.put("gcc_version", "編譯 Redis 時所使用的 GCC 版本");
		map.put("process_id", "服務器進程的 PID");
		map.put("run_id", "Redis 服務器的隨機標識符（用于 Sentinel 和集群）");
		map.put("tcp_port", "TCP/IP 監聽端口");
		map.put("uptime_in_seconds", "自 Redis 服務器啟動以來，經過的秒數");
		map.put("uptime_in_days", "自 Redis 服務器啟動以來，經過的天數");
		map.put("lru_clock", " 以分鐘為單位進行自增的時鐘，用于 LRU 管理");
		map.put("connected_clients", "已連接客戶端的數量（不包括通過從屬服務器連接的客戶端）");
		map.put("client_longest_output_list", "當前連接的客戶端當中，最長的輸出列表");
		map.put("client_longest_input_buf", "當前連接的客戶端當中，最大輸入緩存");
		map.put("blocked_clients", "正在等待阻塞命令（BLPOP、BRPOP、BRPOPLPUSH）的客戶端的數量");
		map.put("used_memory", "由 Redis 分配器分配的內存總量，以字節（byte）為單位");
		map.put("used_memory_human", "以人類可讀的格式返回 Redis 分配的內存總量");
		map.put("used_memory_rss", "從操作系統的角度，返回 Redis 已分配的內存總量（俗稱常駐集大小）。這個值和 top 、 ps 等命令的輸出一致");
		map.put("used_memory_peak", " Redis 的內存消耗峰值(以字節為單位)");
		map.put("used_memory_peak_human", "以人類可讀的格式返回 Redis 的內存消耗峰值");
		map.put("used_memory_lua", "Lua 引擎所使用的內存大小（以字節為單位）");
		map.put("mem_fragmentation_ratio", "sed_memory_rss 和 used_memory 之間的比率");
		map.put("mem_allocator", "在編譯時指定的， Redis 所使用的內存分配器。可以是 libc 、 jemalloc 或者 tcmalloc");

		map.put("redis_build_id", "redis_build_id");
		map.put("redis_mode", "運行模式，單機（standalone）或者集群（cluster）");
		map.put("atomicvar_api", "atomicvar_api");
		map.put("hz", "redis內部調度（進行關閉timeout的客戶端，刪除過期key等等）頻率，程序規定serverCron每秒運行10次。");
		map.put("executable", "server腳本目錄");
		map.put("config_file", "配置文件目錄");
		map.put("client_biggest_input_buf", "當前連接的客戶端當中，最大輸入緩存，用client list命令觀察qbuf和qbuf-free兩個字段最大值");
		map.put("used_memory_rss_human", "以人類可讀的方式返回 Redis 已分配的內存總量");
		map.put("used_memory_peak_perc", "內存使用率峰值");
		map.put("total_system_memory", "系統總內存");
		map.put("total_system_memory_human", "以人類可讀的方式返回系統總內存");
		map.put("used_memory_lua_human", "以人類可讀的方式返回Lua 引擎所使用的內存大小");
		map.put("maxmemory", "最大內存限制，0表示無限制");
		map.put("maxmemory_human", "以人類可讀的方式返回最大限制內存");
		map.put("maxmemory_policy", "超過內存限制后的處理策略");
		map.put("loading", "服務器是否正在載入持久化文件");
		map.put("rdb_changes_since_last_save", "離最近一次成功生成rdb文件，寫入命令的個數，即有多少個寫入命令沒有持久化");
		map.put("rdb_bgsave_in_progress", "服務器是否正在創建rdb文件");
		map.put("rdb_last_save_time", "離最近一次成功創建rdb文件的時間戳。當前時間戳 - rdb_last_save_time=多少秒未成功生成rdb文件");
		map.put("rdb_last_bgsave_status", "最近一次rdb持久化是否成功");
		map.put("rdb_last_bgsave_time_sec", "最近一次成功生成rdb文件耗時秒數");
		map.put("rdb_current_bgsave_time_sec", "如果服務器正在創建rdb文件，那么這個域記錄的就是當前的創建操作已經耗費的秒數");
		map.put("aof_enabled", "是否開啟了aof");
		map.put("aof_rewrite_in_progress", "標識aof的rewrite操作是否在進行中");
		map.put("aof_rewrite_scheduled", "rewrite任務計劃，當客戶端發送bgrewriteaof指令，如果當前rewrite子進程正在執行，那么將客戶端請求的bgrewriteaof變為計劃任務，待aof子進程結束后執行rewrite ");

		map.put("aof_last_rewrite_time_sec", "最近一次aof rewrite耗費的時長");
		map.put("aof_current_rewrite_time_sec", "如果rewrite操作正在進行，則記錄所使用的時間，單位秒");
		map.put("aof_last_bgrewrite_status", "上次bgrewrite aof操作的狀態");
		map.put("aof_last_write_status", "上次aof寫入狀態");

		map.put("total_commands_processed", "redis處理的命令數");
		map.put("total_connections_received", "新創建連接個數,如果新創建連接過多，過度地創建和銷毀連接對性能有影響，說明短連接嚴重或連接池使用有問題，需調研代碼的連接設置");
		map.put("instantaneous_ops_per_sec", "redis當前的qps，redis內部較實時的每秒執行的命令數");
		map.put("total_net_input_bytes", "redis網絡入口流量字節數");
		map.put("total_net_output_bytes", "redis網絡出口流量字節數");

		map.put("instantaneous_input_kbps", "redis網絡入口kps");
		map.put("instantaneous_output_kbps", "redis網絡出口kps");
		map.put("rejected_connections", "拒絕的連接個數，redis連接個數達到maxclients限制，拒絕新連接的個數");
		map.put("sync_full", "主從完全同步成功次數");

		map.put("sync_partial_ok", "主從部分同步成功次數");
		map.put("sync_partial_err", "主從部分同步失敗次數");
		map.put("expired_keys", "運行以來過期的key的數量");
		map.put("evicted_keys", "運行以來剔除(超過了maxmemory后)的key的數量");
		map.put("keyspace_hits", "命中次數");
		map.put("keyspace_misses", "沒命中次數");
		map.put("pubsub_channels", "當前使用中的頻道數量");
		map.put("pubsub_patterns", "當前使用的模式的數量");
		map.put("latest_fork_usec", "最近一次fork操作阻塞redis進程的耗時數，單位微秒");
		map.put("role", "實例的角色，是master or slave");
		map.put("connected_slaves", "連接的slave實例個數");
		map.put("master_repl_offset", "主從同步偏移量,此值如果和上面的offset相同說明主從一致沒延遲");
		map.put("repl_backlog_active", "復制積壓緩沖區是否開啟");
		map.put("repl_backlog_size", "復制積壓緩沖大小");
		map.put("repl_backlog_first_byte_offset", "復制緩沖區里偏移量的大小");
		map.put("repl_backlog_histlen", "此值等于 master_repl_offset - repl_backlog_first_byte_offset,該值不會超過repl_backlog_size的大小");
		map.put("used_cpu_sys", "將所有redis主進程在核心態所占用的CPU時求和累計起來");
		map.put("used_cpu_user", "將所有redis主進程在用戶態所占用的CPU時求和累計起來");
		map.put("used_cpu_sys_children", "將后臺進程在核心態所占用的CPU時求和累計起來");
		map.put("used_cpu_user_children", "將后臺進程在用戶態所占用的CPU時求和累計起來");
		map.put("cluster_enabled", "實例是否啟用集群模式");
		map.put("db0", "db0的key的數量,以及帶有生存期的key的數,平均存活時間");

	}

	private String key;
	private String value;
	private String description;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
		this.description = map.get(this.key);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String toString() {
		return "RedisInfo{" + "key='" + key + '\'' + ", value='" + value + '\'' + ", desctiption='" + description + '\'' + '}';
	}
}
