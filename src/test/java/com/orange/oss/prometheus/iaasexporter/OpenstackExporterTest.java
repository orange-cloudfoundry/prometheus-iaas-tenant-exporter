package com.orange.oss.prometheus.iaasexporter;

import com.google.common.collect.FluentIterable;
import io.restassured.RestAssured;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import org.apache.http.HttpStatus;
import org.assertj.core.api.Assertions;
import org.jclouds.openstack.cinder.v1.CinderApi;
import org.jclouds.openstack.cinder.v1.domain.Volume;
import org.jclouds.openstack.cinder.v1.features.VolumeApi;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
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

    @Before
    public void setup() {
        RestAssured.port = port;
        //RestAssured.authentication = basic("user", "secret");
    }

    @Test
    public void should_export_disk_metrics() {
        //Given
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
        doReturn(REGIONS).when(this.cinderApi).getConfiguredRegions();

        String reponseBody = RestAssured.
                when()
                .get("/prometheus").
                        then()
                .statusCode(HttpStatus.SC_OK)
                .extract()
                .body().asString();

        logger.debug("response from GET /prometheus : " + reponseBody);
        Assertions.assertThat(reponseBody.contains("iaas_exporter_disk{id=\"id-1\",name=\"volume1\",attached=\"false\",} 1048576.0"))
                .isTrue();
        Assertions.assertThat(reponseBody.contains("iaas_exporter_disk{id=\"id-2\",name=\"volume2\",attached=\"false\",} 1048576.0"))
                .isTrue();

    }

}
