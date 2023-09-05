package com.yaude.icloud.openstack.utils;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.openstack4j.api.OSClient;
import org.openstack4j.core.transport.Config;
import org.openstack4j.model.common.Identifier;
import org.openstack4j.model.identity.v3.Project;
import org.openstack4j.openstack.OSFactory;

import java.util.List;

/*
@Author ERIC
@Description TODO
@Date 2021/9/29 11:42
*/
public class OpenstackProject {
    @JsonIgnoreProperties(value = "options" , ignoreUnknown = true)

    public static  void main(String args[]) {

    Identifier domainIdentifier = Identifier.byId("default");

    // unscoped authentication
// as the username is not unique across domains you need to provide the domainIdentifier
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


   int  i= os.identity().projects().list().size();
    System.out.println(i);
    //System.out.println("projects ===="+projects);
}

}
