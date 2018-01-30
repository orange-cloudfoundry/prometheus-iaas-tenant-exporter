package com.orange.oss.prometheus.iaasexporter;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;
import com.orange.oss.prometheus.iaasexporter.openstack.CachedOpenstackApi;
import com.orange.oss.prometheus.iaasexporter.openstack.OpenStackScan;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.cinder.v1.CinderApi;
import org.jclouds.openstack.cinder.v1.domain.Volume;
import org.jclouds.openstack.cinder.v1.features.VolumeApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
public class OpenstackExporterTest {
    public static final String[] SET_VALUES = new String[]{"regionA", "regionB"};
    public static final Set<String> REGIONS = new HashSet<>(Arrays.asList(SET_VALUES));
    private static Logger logger = LoggerFactory.getLogger(OpenstackExporterTest.class.getName());
    @LocalServerPort
    int port;

    @MockBean
    CinderApi cinderApi;

    @MockBean
    NovaApi novaApi;

    @MockBean
    CachedOpenstackApi cachedOpenstackApi;

    @Before
    public void setup() {
        RestAssured.port = port;
        initCinderApi();
        initNovaApi();

        logger.warn("PORT :{}", port);
    }



    private void initCinderApi(){
        FluentIterable<Volume> volumes = FluentIterable.from(Arrays.asList(
                Volume.builder()
                        .id("id-1")
                        .size(1024)
                        .status(Volume.Status.CREATING)
                        .created(new Date())
                        .name("volume1")
                        .build(),
                Volume.builder()
                        .id("id-2")
                        .size(1024)
                        .status(Volume.Status.AVAILABLE)
                        .created(new Date())
                        .name("volume2")
                        .build()));

        VolumeApi volumeApi = Mockito.mock(VolumeApi.class);
        doReturn(volumes).when(volumeApi).list();
        doReturn(volumeApi).when(this.cinderApi).getVolumeApi("regionA");
        doReturn(volumeApi).when(this.cinderApi).getVolumeApi("regionB");
        doReturn(REGIONS).when(this.cinderApi).getConfiguredRegions();

    }


    private void initNovaApi(){


        Flavor flavor1 = Flavor.builder().id("flavor-id1").name("res1").vcpus(1).ram(1024).swap("none").links().build();
        Flavor flavor2 = Flavor.builder().id("flavor-id2").name("res2").vcpus(2).ram(2048).swap("none").links().build();

        FluentIterable<Server> servers = FluentIterable.from(Arrays.asList(
                Server.builder()
                        .id("server-id-1")
                        .tenantId("tenantA")
                        .userId("testUser")
                        .status(Server.Status.BUILD)
                        .flavor(flavor1)
                        .created(new Date())
                        .name("server1")
                        .build(),
                Server.builder()
                        .id("server-id-2")
                        .tenantId("tenantA")
                        .userId("testUser")
                        .status(Server.Status.BUILD)
                        .flavor(flavor2)
                        .created(new Date())
                        .name("server2")
                        .build()));
        ServerApi serverApi = Mockito.mock(ServerApi.class);
        FlavorApi flavorApi = Mockito.mock(FlavorApi.class);
        PagedIterable< Server> serversP = Mockito.mock(PagedIterable.class);

        Mockito.when(serversP.concat()).thenReturn(servers);

        doReturn(serversP).when(serverApi).listInDetail();
        doReturn(serverApi).when(this.novaApi).getServerApi("regionA");
        doReturn(serverApi).when(this.novaApi).getServerApi("regionB");
        doReturn(REGIONS).when(this.novaApi).getConfiguredRegions();


        Mockito.when(flavorApi.get("regionA")).thenReturn(flavor1);
        Mockito.when(flavorApi.get("regionB")).thenReturn(flavor2);
        doReturn(flavorApi).when(this.novaApi).getFlavorApi("regionA");


        doReturn(flavor1).when(this.cachedOpenstackApi).findFlavor("regionA","flavor-id1");
        doReturn(flavor2).when(this.cachedOpenstackApi).findFlavor("regionA","flavor-id2");

        doReturn(flavor1).when(this.cachedOpenstackApi).findFlavor("regionB","flavor-id1");
        doReturn(flavor2).when(this.cachedOpenstackApi).findFlavor("regionB","flavor-id2");

    }




    @Test
    public void should_export_disk_metrics() throws InterruptedException{

        Thread.sleep(1500);

        String reponseBody = RestAssured.
                when()
                .get("/prometheus").
                        then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .body().asString();

        logger.info("response from GET /prometheus : " + reponseBody);
        Assertions.assertThat(reponseBody.contains("iaas_exporter_disk{id=\"id-1\",name=\"volume1\",attached=\"false\",} 1048576.0"))
                .isTrue();
        Assertions.assertThat(reponseBody.contains("iaas_exporter_disk{id=\"id-2\",name=\"volume2\",attached=\"false\",} 1048576.0"))
                .isTrue();

    }

}
