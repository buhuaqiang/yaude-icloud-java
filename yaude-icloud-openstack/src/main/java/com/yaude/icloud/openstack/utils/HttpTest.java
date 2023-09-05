package com.yaude.icloud.openstack.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.*;

/**
 * @author Ad
 * @version 1.0.0
 * @ClassName HttpTest.java
 * @Description TODO
 * @createTime 2021年10月25日 16:47:00
 */
public class HttpTest {


    void test(){
        String url = "https://192.168.2.8:9696/v2.0/floatingips";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("X-Auth-Token","gAAAAABhdndAmgjcenFHDrN3bzY7XjR9KNxPlp7wMyl9VfXz_2dM3SPFhL7zyQHl3Md2YCe7aq5PmFyIwSmyBA1lx9AMCcinn5oyvJipTXNp338GsUvBwQHgID-9Db_B7z1UBM_AXC6mn-HY7iMlBuvRKgBdvX7sJTdVY0hySO2MZpjkwDQvr0k");
        RestTemplate template = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<String>(null, requestHeaders);
        ResponseEntity<String> response = template.exchange(url, HttpMethod.GET, requestEntity, String.class);
        String sttr = response.getBody();
        System.out.println(sttr);
    }
    //根据网络和子网生成浮动ip
    public  String test2(String tokenId,String networkId,String subnetId){
        String url = "http://192.168.2.8:9696/v2.0/floatingips";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("X-Auth-Token",tokenId);
        Map<String,Object> dataValue = new HashMap<>();
        Map<String,Object> floatingIp = new HashMap<>();
        floatingIp.put("floating_network_id",networkId);
        floatingIp.put("subnet_id",subnetId);
        dataValue.put("floatingip",floatingIp);


        RestTemplate template = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<String>(JSON.toJSONString(dataValue), requestHeaders);
        ResponseEntity<String> response = template.exchange(url, HttpMethod.POST, requestEntity, String.class);
        String sttr = response.getBody();
        HashMap<String, Object> json = JSONObject.parseObject(sttr, HashMap.class);
        Map<String,Object> floatIp = JSONObject.parseObject(json.get("floatingip").toString(), HashMap.class);
        String ip = (String) floatIp.get("floating_ip_address");
        return ip;
    }


    void test2(){
        String url = "http://192.168.2.8:9696/v2.0/floatingips";
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("X-Auth-Token","gAAAAABhd8adNEzLvU70unhl68lh5-XDA11xz-_ZlRgNlW-ihFc-qUj-0bdslOAeM2i9aAXlm7bkMsJDv8ae2rRTrHJkeMNxSG7Ae2-VxEepyVJN_CXQs3btmpwcewLPDDWlEJIkFkjuFGvJa6aYWfleH9YadyWHLDAKNGXa8q2yR9bOfm2jBCY");
        Map<String,Object> dataValue = new HashMap<>();
        Map<String,Object> floatingIp = new HashMap<>();
        floatingIp.put("floating_network_id","c8820dd2-e56a-492e-8247-90126254671e");
        floatingIp.put("subnet_id","da4a328a-4950-4c88-93b1-ad455bf753de");
        dataValue.put("floatingip",floatingIp);


        RestTemplate template = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<String>(JSON.toJSONString(dataValue), requestHeaders);
        ResponseEntity<String> response = template.exchange(url, HttpMethod.POST, requestEntity, String.class);
        String sttr = response.getBody();

        HashMap<String, Object> json = JSONObject.parseObject(sttr, HashMap.class);
        Map<String,Object> floatIp = JSONObject.parseObject(json.get("floatingip").toString(), HashMap.class);
        String ip = (String) floatIp.get("floating_ip_address");
        System.out.println(ip);
    }

    //创建vm时同时创建卷，并将vm挂载到卷上
    public String server(String tokenId,String networkId,String securityName,String imgId,String size,String isDelete,String instanceName,String flavorId){
        String url = "http://192.168.2.8:8774/v2.1/servers";
        Boolean delete_on_termination = false;
        if(isDelete.equals("1")){
             delete_on_termination = true;
        }
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.add("X-Auth-Token",tokenId);
        Map<String,Object> dataValue = new HashMap<>();
        Map<String,Object> server = new HashMap<>();
        Map<String,Object> networks = new HashMap<>();
        ArrayList<Map<String, Object>> network = new ArrayList<>();
        Map<String,Object> security_groups = new HashMap<>();
        ArrayList<Map<String, Object>> security_group = new ArrayList<>();
        Map<String,Object> block_device_mapping_v2 = new HashMap<>();
        ArrayList<Map<String, Object>> block_device_mapping = new ArrayList<>();
        networks.put("uuid",networkId);
        security_groups.put("name",securityName);
        block_device_mapping_v2.put("uuid",imgId);
        block_device_mapping_v2.put("source_type","image");
        block_device_mapping_v2.put("destination_type","volume");
        block_device_mapping_v2.put("boot_index",0);
        block_device_mapping_v2.put("volume_size",size);
        block_device_mapping_v2.put("delete_on_termination",delete_on_termination);
        network.add(networks);
        security_group.add(security_groups);
        block_device_mapping.add(block_device_mapping_v2);
        server.put("name",instanceName);
        server.put("flavorRef",flavorId);
        server.put("networks",network);
        server.put("security_groups",security_group);
        server.put("block_device_mapping_v2",block_device_mapping);
        dataValue.put("server",server);


        RestTemplate template = new RestTemplate();
        HttpEntity<String> requestEntity = new HttpEntity<String>(JSON.toJSONString(dataValue), requestHeaders);
        ResponseEntity<String> response = template.exchange(url, HttpMethod.POST, requestEntity, String.class);
        String sttr = response.getBody();

        HashMap<String, Object> json = JSONObject.parseObject(sttr, HashMap.class);
        Map<String,Object> servers = JSONObject.parseObject(json.get("server").toString(), HashMap.class);
        String id = (String) servers.get("id");
        return  id;
       /* Map<String,Object> floatIp = JSONObject.parseObject(json.get("floatingip").toString(), HashMap.class);
        String ip = (String) floatIp.get("floating_ip_address");
        System.out.println(ip);*/
    }



    public static void main(String[] args) throws IOException {
        HttpTest httpTest = new HttpTest();
        httpTest.test();
    }
}
