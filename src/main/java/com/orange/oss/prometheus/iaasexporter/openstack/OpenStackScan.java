package com.orange.oss.prometheus.iaasexporter.openstack;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Server;
import org.jclouds.openstack.nova.v2_0.domain.Volume;
import org.jclouds.openstack.nova.v2_0.extensions.VolumeApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.orange.oss.prometheus.iaasexporter.model.Disk;
import com.orange.oss.prometheus.iaasexporter.model.Vm;

public class OpenStackScan {

	
	private static Logger logger=LoggerFactory.getLogger(OpenStackScan.class.getName());
	
	@Autowired
	NovaApi novaApi;

	
	@Scheduled(fixedDelayString = "${exporter.disk.scan.delayms}")
	public void scanIaasDisks() {
		Set<String> regions = novaApi.getConfiguredRegions();
		for (String region : regions) {
			VolumeApi volumeApi = novaApi.getVolumeApi(region).get();

			Iterator<Volume> it = volumeApi.list().iterator();
			while(it.hasNext()){
				Volume v=it.next();
				logger.info("vol: "+v);				

				String id=v.getId();
				String name=v.getName();
				long size=v.getSize()*1024;
				boolean attached=(v.getAttachments().size()>0);				
				Map<String, String> metadata=v.getMetadata();
				
				Disk disk=new Disk(id, name, attached, size);
				disk.publishMetrics();
					
			}
		}

	}
	
	@Scheduled(fixedDelayString="${exporter.vm.scan.delayms}")
	public void scanIaasVms(){
		Set<String> regions = novaApi.getConfiguredRegions();

		for (String region : regions) {
			ServerApi serverApi = novaApi.getServerApi(region);

			logger.info("Servers in " + region);

			for (Server server : serverApi.listInDetail().concat()) {
				logger.info("  " + server);
				String id=server.getId();
				String name=server.getName();
				String address="1.1.1.1";
				//String address=server.getAddresses().values().
				Map<String,String> metadata=server.getMetadata();
				server.getAvailabilityZone().or("none");
				String az=server.getAvailabilityZone().or("none");
			
				Vm vm=new Vm(id,name,address,az,metadata);
				vm.publishMetrics();
				
				
			}
		}

	}
	
}
