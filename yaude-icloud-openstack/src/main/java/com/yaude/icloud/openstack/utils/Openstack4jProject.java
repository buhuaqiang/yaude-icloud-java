package com.yaude.icloud.openstack.utils;

import com.yaude.common.util.SpringContextUtils;
import org.openstack4j.api.OSClient;
import org.openstack4j.core.transport.Config;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.compute.FloatingIP;
import org.openstack4j.model.compute.Keypair;
import org.openstack4j.model.compute.Server;
import org.openstack4j.model.identity.v3.Domain;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.model.identity.v3.Region;
import org.openstack4j.model.network.Network;
import org.openstack4j.model.network.Subnet;
import org.openstack4j.openstack.OSFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @author Ad
 * @version 1.0.0
 * @ClassName Openstack4jApi.java
 * @Description TODO
 * @createTime 2021年09月27日 16:51:00
 */
public class Openstack4jProject {
    private static HttpServletResponse response;
    private final OSClient.OSClientV3 os;

    private FileOutputStream private_file_out = null;

    private ObjectOutputStream private_object_out = null;


    public Openstack4jProject(){
        // 开始认证
        System.out.println("开始认证：");
        Identifier domainIdentifier = Identifier.byName("Default");
        Openstack4jEntity bean = SpringContextUtils.getBean(Openstack4jEntity.class);
         os  = OSFactory
                .builderV3()
                .endpoint(bean.getHostUrl())
                 .credentials(bean.getUsername(), bean.getPassword(), domainIdentifier)
                 //.scopeToDomain(Identifier.byName("Default"))
                 .scopeToProject(Identifier.byName("admin"), Identifier.byName("Default"))
                 .withConfig(
                         Config.newConfig()
                                 .withConnectionTimeout(1000 * 60)
                                 .withReadTimeout(1000 * 60 * 5))
                 .authenticate();
    }


    public List<? extends Project> getProjectList(){
       // OSClient.OSClientV3 os1 = os.useRegion("RegionOne");
        List<? extends Project> projectList = os.identity().projects().list();
        return  projectList;
    }

    public void test(){
        List<? extends Region> list = os.identity().regions().list();
        System.out.println(list.toString());

        List<? extends Domain> list1 = os.identity().domains().list();
        System.out.println(list1.toString());

    }

    /**
     * 根据项目id獲取项目
     * @return
     */
    public Project getProjects(String projectId){
        return  os.identity().projects().get(projectId);
    }

    /**
     * 创建秘钥
     * @return
     */
    public Keypair createKeypair(String keyName){
        return os.compute().keypairs().create(keyName, null);
    }

    public static void main(String[] args)  {
        //Openstack4jProject openstack4jApi = new Openstack4jProject();
        //System.out.println(openstack4jApi.getVNCURL("990752a9-d60a-409b-bb80-64af31a08c8a"));
       /* openstack4jApi.getProjectList().stream().forEach(p ->{
            System.out.println(p.toString());
        });

        openstack4jApi.test();*/
        //System.out.println(openstack4jApi.floatingIP());
        System.out.println("开始认证：");
        Identifier domainIdentifier = Identifier.byName("Default");
        //Openstack4jEntity bean = SpringContextUtils.getBean(Openstack4jEntity.class);
        OSClient.OSClientV3  os  = OSFactory
                .builderV3()
                .endpoint("http://192.168.11.23/identity/v3/")
                .credentials("admin", "admin", domainIdentifier)
                //.scopeToDomain(Identifier.byName("Default"))
                .scopeToProject(Identifier.byName("admin"), Identifier.byName("Default"))
                .withConfig(
                        Config.newConfig()
                                .withConnectionTimeout(1000 * 60)
                                .withReadTimeout(1000 * 60 * 5))
                .authenticate();
        System.out.println("1111111");
        System.out.println(os.getToken().getId());
       /* List<? extends Server> list = os.compute().servers().list();
        System.out.println(list.toString());*/


    }

}
