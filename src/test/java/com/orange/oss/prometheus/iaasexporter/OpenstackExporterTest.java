package com.orange.oss.prometheus.iaasexporter;

import java.util.Map;
import java.util.Set;

import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.orange.oss.prometheus.iaasexporter.model.Vm;

@RunWith(SpringRunner.class)
@SpringBootTest

public class OpenstackExporterTest {
	private static Logger logger = LoggerFactory.getLogger(OpenstackExporterTest.class.getName());

	@Autowired
	NovaApi novaApi;

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
			
				Vm vm=new Vm(id,name,address,region,metadata);
				
				
			}
		}
	}

}
