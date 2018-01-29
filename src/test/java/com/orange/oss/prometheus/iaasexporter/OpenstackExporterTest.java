package com.orange.oss.prometheus.iaasexporter;

import java.util.*;

import com.google.common.collect.FluentIterable;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeApi;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.orange.oss.prometheus.iaasexporter.model.Vm;

import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest

public class OpenstackExporterTest {
	private static Logger logger = LoggerFactory.getLogger(OpenstackExporterTest.class.getName());

	//@Autowired
	@MockBean
	NovaApi novaApi;

	public static final String[] SET_VALUES = new String[]{"regionA", "regionB"};
	public static final Set<String> REGIONS = new HashSet<>(Arrays.asList(SET_VALUES));

	@Mock
	ServerApi serverApiRegionA;

	@Mock
	ServerApi serverApiRegionB;

	/*Optional
	@BeforeClass
	void initMbean(){
		MockitoAnnotations.initMocks(this);
		given(this.novaApi.getConfiguredRegions()).willReturn(REGIONS);

		given(this.novaApi.getServerApi("regionA")).willReturn(serverApiRegionA);
		given(this.novaApi.getServerApi("regionB")).willReturn(serverApiRegionB);

		List<Server> ls = new ArrayList<>();
		ls.add(Server.builder().build());

		VolumeApi volumeApi = Mockito.mock(VolumeApi.class);

		Optional<VolumeApi> volumeApiOptional = Mockito.mock(Optional.class);
		Mockito.when(volumeApiOptional.get()).thenReturn(volumeApi);

		Mockito.when(this.novaApi.getVolumeApi("regionA")).thenReturn(volumeApiOptional);
	}*/

	@Test
	public void testVmMetrics() {

		Set<String> regions = novaApi.getConfiguredRegions();

		for (String region : regions) {
			ServerApi serverApi = novaApi.getServerApi(region);

			logger.info("Servers in " + region);

			for (Server server : serverApi.listInDetail().concat()) {
				logger.info("  " + server);
				String id=server.getId();
				String name=server.getName();
				String address="1.1.1.1";
				Map<String,String> metadata=server.getMetadata();
				//String az=server.getAvailabilityZone()
			
				Vm vm=new Vm(id,name,address,"tenant",region,metadata,2,2000,true);
				
				
			}
		}
	}

}
