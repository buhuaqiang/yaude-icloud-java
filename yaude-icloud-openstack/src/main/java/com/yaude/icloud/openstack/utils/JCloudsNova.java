package com.yaude.icloud.openstack.utils ;

import com.google.common.base.Optional;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.io.Closeables;
import com.google.inject.Module;
import com.yaude.common.util.SpringContextUtils;
import org.jclouds.ContextBuilder;
import org.jclouds.collect.PagedIterable;
import org.jclouds.logging.slf4j.config.SLF4JLoggingModule;
import org.jclouds.openstack.keystone.auth.config.CredentialTypes;
import org.jclouds.openstack.keystone.config.KeystoneProperties;
import org.jclouds.openstack.keystone.v3.KeystoneApi;
import org.jclouds.openstack.keystone.v3.domain.Project;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.*;
import org.jclouds.openstack.nova.v2_0.extensions.QuotaApi;
import org.jclouds.openstack.nova.v2_0.extensions.SecurityGroupApi;
import org.jclouds.openstack.nova.v2_0.extensions.SimpleTenantUsageApi;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ImageApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.nova.v2_0.options.CreateServerOptions;

import javax.annotation.concurrent.Immutable;
import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * openstack Nova 組件方法，VM 創建或刪除，image等
 *
 */
public class JCloudsNova implements Closeable {
    private final NovaApi novaApi;
    private final Set<String> regions;


    public static void main(String[] args) throws IOException {
        Openstack4jEntity openstack4jEntity = new Openstack4jEntity();
        openstack4jEntity.setProjectId("673c2c0f0d714602b5767ddbb399f6db");

        JCloudsNova jcloudsNova = new JCloudsNova(openstack4jEntity);

        try {
           // Server server = jcloudsNova.getServerById("603ea682-e3a5-4e75-93ed-f1ef24d055d2");//603ea682-e3a5-4e75-93ed-f1ef24d055d2  990752a9-d60a-409b-bb80-64af31a08c8a

           // System.out.println(server.getAddresses());

           // CreateServerOptions options = new CreateServerOptions();
            //options.securityGroupNames("default");//安全組名字
           // options.networks("88b09b02-54e5-4f2d-84c4-e69116a15fb9");//網絡UUID
           // options.configDrive()
            FluentIterable<Server> aa = jcloudsNova.getListServers();
            aa.stream().forEach(f->{
                System.out.println(f.getName());
            });


            //System.out.println(options.getNetworks());

            //ServerCreated sc = jcloudsNova.createServer("mycirros01","b909cede-ab75-4eb7-8330-5a50aa3ac729","c1",options);
            //System.out.println(sc.getId());

          //  ServerCreated sc = jcloudsNova.createServer("mycirros01","b909cede-ab75-4eb7-8330-5a50aa3ac729","c1",options);
          //  System.out.println(sc.getId());

            //ServerCreated sc = jcloudsNova.createServer("mycirros01","b909cede-ab75-4eb7-8330-5a50aa3ac729","c1",options);
            //System.out.println(sc.getId());
            /*System.out.println("---------------------");
            System.out.println(jcloudsNova.getListServers().toList());
            System.out.println("---------------------");

            jcloudsNova.getListFlavor();

            jcloudsNova.getQuotaInfo();


            jcloudsNova.getListSecurityGroup();
            jcloudsNova.getListImage();
            jcloudsNova.close();

            jcloudsNova.getDiagnosticsById("603ea682-e3a5-4e75-93ed-f1ef24d055d2");*/


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            jcloudsNova.close();
        }
    }

    public JCloudsNova() {
        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

        // Please refer to 'Keystone v2-v3 authentication' section for complete authentication use case
        Openstack4jEntity bean = SpringContextUtils.getBean(Openstack4jEntity.class);
        String provider = "openstack-nova";
        String identity = "Default:"+bean.getUsername(); // tenantName:userName
        String credential = bean.getPassword();
        final Properties overrides = new Properties();
        overrides.put(KeystoneProperties.KEYSTONE_VERSION, "3");
        overrides.put(KeystoneProperties.SCOPE, "project:admin");
        novaApi   = ContextBuilder.newBuilder(provider)
                .endpoint(bean.getHostUrl())
                .credentials(identity, credential)
                .overrides(overrides)
                .modules(modules)
                .buildApi(NovaApi.class);
        regions = novaApi.getConfiguredRegions();

    }
    public JCloudsNova(Openstack4jEntity openstack4jEntity) {
        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

        // Please refer to 'Keystone v2-v3 authentication' section for complete authentication use case
        Openstack4jEntity bean = SpringContextUtils.getBean(Openstack4jEntity.class);
        String provider = "openstack-nova";
        String identity = "Default:"+bean.getUsername(); // tenantName:userName
        String credential = bean.getPassword();
        final Properties overrides = new Properties();
        overrides.put(KeystoneProperties.KEYSTONE_VERSION, "3");
        //overrides.put(KeystoneProperties.SCOPE, "project:admin");

        //overrides.put(KeystoneProperties.SCOPE,"projectId:673c2c0f0d714602b5767ddbb399f6db");
        overrides.put(KeystoneProperties.SCOPE,"projectId:"+openstack4jEntity.getProjectId());
        novaApi   = ContextBuilder.newBuilder(provider)
                .endpoint(bean.getHostUrl())
                .credentials(identity, credential)
                .overrides(overrides)
                .modules(modules)
                .buildApi(NovaApi.class);
        regions = novaApi.getConfiguredRegions();

    }

    //初始化設定項目名稱
    public JCloudsNova(String projectName) {
        Iterable<Module> modules = ImmutableSet.<Module>of(new SLF4JLoggingModule());

        // Please refer to 'Keystone v2-v3 authentication' section for complete authentication use case
        Openstack4jEntity bean = SpringContextUtils.getBean(Openstack4jEntity.class);
        String provider = "openstack-nova";
        String identity = "Default:"+bean.getUsername(); // tenantName:userName
        String credential = bean.getPassword();
        final Properties overrides = new Properties();
        overrides.put(KeystoneProperties.KEYSTONE_VERSION, "3");
        overrides.put(KeystoneProperties.SCOPE, "project:"+projectName);
        novaApi = ContextBuilder.newBuilder(provider)
                .endpoint(bean.getHostUrl())
                .credentials(identity, credential)
                .overrides(overrides)
                .modules(modules)
                .buildApi(NovaApi.class);

        regions = novaApi.getConfiguredRegions();
    }

    /**
     * 查詢項目下全部server
     * @return
     */
    private FluentIterable<Server> getListServers() {
        for (String region : regions) {
            ServerApi serverApi = novaApi.getServerApi(region);
            return serverApi.listInDetail().concat();

        }
        return null;
    }

    /**
     * 根據VM id查詢server
     * @param id
     * @return
     */
    private Server getServerById(String id){
        for (String region : regions) {
            ServerApi serverApi = novaApi.getServerApi(region);
           return  serverApi.get(id);

        }
        return null;
    }



    /**
     * 創建VM
     * @param name
     * @param imageRef
     * @param flavorRef
     * @param options
     * @return
     */
   // @PayloadParam("name") String var1, @PayloadParam("imageRef") String var2, @PayloadParam("flavorRef") String var3, CreateServerOptions... var4
    public ServerCreated   createServer(String name, String imageRef, String flavorRef, CreateServerOptions options){
        for (String region : regions) {
            ServerApi serverApi = novaApi.getServerApi(region);
           return  serverApi.create(name,imageRef,flavorRef,options);

        }
        return null;
    }

    private void console(String vmid,String option) {
       for (String region : regions) {
            ServerApi serverApi = novaApi.getServerApi(region);
          // serverApi.stop("990752a9-d60a-409b-bb80-64af31a08c8a");
           serverApi.start("990752a9-d60a-409b-bb80-64af31a08c8a");

        }

    }




    /**
     * 查詢全部 image
     * @return
     */
    public ImmutableList<Image> getListImage() {
        PagedIterable<Image>  images = null;
        for (String region : regions) {
            ImageApi imageApi = novaApi.getImageApi(region);
            images= imageApi.listInDetail();
            System.out.println("Image = "+images);
        }
        return images.concat().toList();
    }


    /**
     * 返回安全組
     */
    public FluentIterable<SecurityGroup>  getListSecurityGroup() {
        FluentIterable<SecurityGroup>  securityGroup = null;
        for (String region : regions) {
            Optional<SecurityGroupApi>   securityGroupApi = novaApi.getSecurityGroupApi(region);
             securityGroup =   securityGroupApi.get().list();
            System.out.println("securityGroup="+securityGroup);

        }
        return securityGroup;
    }

    /**
     * 查詢全部實例類型
     * @return
     */
    public ImmutableList<Flavor> getListFlavor() {
        PagedIterable<Flavor> flavors = null;
        for (String region : regions) {
            FlavorApi flavorApi = novaApi.getFlavorApi(region);
            flavors= flavorApi.listInDetail();
        }
        return flavors.concat().toList();
    }
    //根据id获取實例類型
    public Flavor getFlavor(String id){
        for (String region : regions) {
            FlavorApi flavorApi = novaApi.getFlavorApi(region);
            return  flavorApi.get(id);

        }
        return null;
    }

    /**
     * 獲取虛擬機資源使用情況
     */
    private  void getListHypervisorUsageInfo(){
        for (String region : regions) {
            Optional<SimpleTenantUsageApi> simpleTenantUsageApi =novaApi.getSimpleTenantUsageApi(region);
            FluentIterable<SimpleTenantUsage> tenantUsage=  simpleTenantUsageApi.get().list();
            System.out.println(tenantUsage);

        }
    }

    private  void getQuotaInfo(){
        for (String region : regions) {
            Optional<QuotaApi>  quotaApi =novaApi.getQuotaApi(region);
            Quota quota=  quotaApi.get().getByTenant("04987b0c4ad54494a79f0c41a7fb6c02");
            System.out.println(quota);

        }
    }

    private void getDiagnosticsById(String uuid){
        for (String region : regions) {
            Optional<Map<String, String>> diagnostics =novaApi.getServerApi(region).getDiagnostics(uuid);

            System.out.println("diagnostics ==="+diagnostics);

        }
    }


    public void close() throws IOException {
        Closeables.close(novaApi, true);
    }


    /*
        獲取實例列表
     */
    public ImmutableList<Server> getServersToList(){
        return this.getListServers().toList();
    }

    /*
     * 獲取全部實例類型
     * @return
             */
    public ImmutableList<Flavor> getFlavorsToList() {

        return this.getListFlavor();
    }


    /*
        獲取鏡像列表
     */
    public ImmutableList<Image> getImagesToList(){
        return this.getListImage();
    }

    /**
     * 開啓實例
     */
    public void startInstance(String instanceID){
        for (String region : regions) {
            ServerApi serverApi = novaApi.getServerApi(region);
            serverApi.start(instanceID);
        }
    }

    /**
     * 关闭實例
     */
    public void stopInstance(String instanceID){
        for (String region : regions) {
            ServerApi serverApi = novaApi.getServerApi(region);
            serverApi.stop(instanceID);
        }
    }

    /**
     * 重启實例
     * instanceID 實例ID
     * rebootType ： hard 强制重啓
     *              soft 正常關機后重啓
     */
    public void rebootInstance(String instanceID,RebootType rebootType){
        for (String region : regions) {
            ServerApi serverApi = novaApi.getServerApi(region);
            serverApi.reboot(instanceID,rebootType);
        }
    }


    /*
     根據ID獲取虛擬機運行狀態
     */
    public Server.Status getServerStatusById(String id){
        Server.Status status = null;
        Server server = this.getServerById(id);
        if(!(server == null)){
            status = server.getStatus();
        }
        return status;
    }


}