package com.yaude.icloud.openstack.utils ;

import org.apache.http.ssl.SSLContexts;
import org.openstack4j.api.OSClient;
import org.openstack4j.api.exceptions.OS4JException;
import org.openstack4j.core.transport.Config;
import org.openstack4j.core.transport.HttpRequest;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.compute.*;
import org.openstack4j.model.compute.ext.Hypervisor;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.model.identity.v3.Region;
import org.openstack4j.model.identity.v3.Tenant;
import org.openstack4j.model.storage.object.SwiftAccount;
import org.openstack4j.model.storage.object.SwiftContainer;
import org.openstack4j.model.telemetry.Meter;
import org.openstack4j.openstack.OSFactory;
import org.openstack4j.openstack.internal.BaseOpenStackService;
import org.openstack4j.openstack.internal.OSClientSession;

import javax.net.ssl.SSLContext;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.*;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static cn.hutool.core.io.FileUtil.newFile;

public class Openstack {
    public static void main( String[] args ) throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException, KeyManagementException, IOException, CertificateException {


        // 开始认证
        System.out.println("开始认证：");
        OSClient.OSClientV3 os  = OSFactory
                .builderV3()
                .endpoint("http://192.168.11.23/identity/v3/")

                .credentials("admin", "admin", Identifier.byName("Default"))

                .scopeToProject(Identifier.byName("admin"), Identifier.byName("Default"))

                .withConfig(
                        Config.newConfig()
                                .withConnectionTimeout(1000 * 60)
                                .withReadTimeout(1000 * 60 * 5))
                .authenticate();

        OSClientSession ses = OSClientSession.getCurrent();
        System.out.println("getTokenId==============="+ses.getTokenId());
        System.out.println("完成认证："+os.toString());

        System.out.println("获取Swift账户：");
        SwiftAccount swiftAccount = os.objectStorage().account().get();
        System.out.println(swiftAccount);

        System.out.println("获取对象存储元数据信息：");
        Map<String, String> metadata = new HashMap<String, String>();
        boolean result = os.objectStorage().account().updateMetadata(metadata);
        System.out.println(result);

        System.out.println("获取容器信息：");
        List<? extends SwiftContainer> containers = os.objectStorage().containers().list();
        int containerCount = (int) swiftAccount.getContainerCount();

        for(int i=0; i<containerCount; i++){
            System.out.println(containers.get(i).getName()+containers.get(i).getTotalSize()+"\n");
        }

      /*  System.out.println("新建容器：");
        os.objectStorage().containers().create("xhhuangContainer", CreateUpdateContainerOptions.create()
                .accessAnybodyRead()
        );*/
        List images = os.compute().images().list();
        System.out.println("image size="+images.size());
     /* Image image = (Image) images.get(0);
      System.out.println("image info="+image.getName());*/



        // List all Servers
        // List<? extends Server> servers = os.compute().servers().list();

// List all servers (light) ID, Name and Links populated
        //  List<? extends Server> servers = os.compute().servers().list(false);

// Get a specific Server by ID
        System.out.println("获取指定vm ：");
        Server server = os.compute().servers().get("990752a9-d60a-409b-bb80-64af31a08c8a");

//        Map<String, ? extends Number> diagnostics = os.compute().servers().diagnostics("990752a9-d60a-409b-bb80-64af31a08c8a");
//        System.out.println("获取指定vm diagnostics 診斷 ："+diagnostics.toString());

        //String consoleOutput = os.compute().servers().getConsoleOutput("990752a9-d60a-409b-bb80-64af31a08c8a", 50);
        //System.out.println("获取指定vm console log ："+consoleOutput);

//        VNCConsole console = os.compute().servers().getVNCConsole("990752a9-d60a-409b-bb80-64af31a08c8a", VNCConsole.Type.NOVNC);
//        System.out.println("获取指定vm VNC console url ："+console.getURL());

      /*  Project project =os.identity().projects().get("");
        os.octavia().healthMonitorV2().list();//健康監控*/


        os.compute().quotaSets().limits().getAbsolute();//絕對可用資源

        Hypervisor hypervisor = os.compute().hypervisors().list().get(0);
        System.out.println("openstack4j  hypervisor = "+hypervisor);

      /* Map  vmMetadata  =  os.compute().servers().get("1c3840f9-43c3-4036-b25f-13241653f311").getMetadata();
        System.out.println("openstack4j  vmMetadata = "+vmMetadata);*/

        List<? extends Meter> meters = os.telemetry().meters().list();
        System.out.println("openstack4j meters==="+meters);

        //項目的整體資源使用情況
        List<? extends SimpleTenantUsage> tenantUsages = os.compute().quotaSets().listTenantUsages();
        System.out.println("openstack4j 項目整體資源用量 =="+tenantUsages);

        //項目下每個實例的狀態
        SimpleTenantUsage usage = os.compute().quotaSets().getTenantUsage("04987b0c4ad54494a79f0c41a7fb6c02");
        System.out.println("openstack4j project 04987b0c4ad54494a79f0c41a7fb6c02  項目用量 =="+usage);

        //項目下每個實例的狀態   某一時間段
        SimpleTenantUsage usage2 = os.compute().quotaSets().getTenantUsage("04987b0c4ad54494a79f0c41a7fb6c02","2021-09-27T09:49:58","2021-10-10T09:49:58");
        System.out.println("openstack4j project 04987b0c4ad54494a79f0c41a7fb6c02  項目用量2021-08-31 =="+usage2);

        //  項目概況信息
        Limits limits  = os.compute().quotaSets().limits();
        System.out.println("limits ============="+limits);



        List<? extends Region> regions = os.identity().regions().list();
        System.out.println("regions ============="+regions);




    }

}
