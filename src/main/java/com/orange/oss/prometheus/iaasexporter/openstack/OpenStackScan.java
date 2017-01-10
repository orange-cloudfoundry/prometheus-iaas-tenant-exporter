package com.orange.oss.prometheus.iaasexporter.openstack;

import java.util.Map;
import java.util.Set;

import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.orange.oss.prometheus.iaasexporter.model.Vm;

@Service

public class OpenStackScan {

	
	private static Logger logger=LoggerFactory.getLogger(OpenStackScan.class.getName());
	
	@Autowired
	NovaApi novaApi;
	
	
	@Scheduled(fixedDelayString="${exporter.vm.scan.delayms}")
	public void scanIaas(){
		Set<String> regions = novaApi.getConfiguredRegions();

		for (String region : regions) {
			ServerApi serverApi = novaApi.getServerApi(region);

			logger.info("Servers in " + region);

			for (Server server : serverApi.listInDetail().concat()) {
				logger.info("  " + server);
				String id=server.getId();
				String name=server.getName();
				String address=server.getAccessIPv4();
				Map<String,String> metadata=server.getMetadata();
				//String az=server.getAvailabilityZone()
			
				Vm vm=new Vm(id,name,address,metadata);
				
				
			}
		}

	}
	
}
