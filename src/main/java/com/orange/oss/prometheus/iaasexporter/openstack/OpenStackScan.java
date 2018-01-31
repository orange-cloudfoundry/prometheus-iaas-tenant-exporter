package com.orange.oss.prometheus.iaasexporter.openstack;

import java.util.*;

import javax.net.ssl.SSLEngineResult.Status;

import com.orange.oss.prometheus.iaasexporter.Utility;
import com.orange.oss.prometheus.iaasexporter.model.Publiable;
import lombok.extern.java.Log;
import org.jclouds.cloudstack.domain.VirtualMachine.State;
import org.jclouds.collect.PagedIterable;
import org.jclouds.openstack.cinder.v1.CinderApi;
import org.jclouds.openstack.cinder.v1.domain.Volume;
import org.jclouds.openstack.cinder.v1.features.VolumeApi;
import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Address;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.domain.Server;


import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.jclouds.openstack.nova.v2_0.features.ServerApi;
import org.jclouds.openstack.v2_0.domain.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.orange.oss.prometheus.iaasexporter.model.Disk;
import com.orange.oss.prometheus.iaasexporter.model.Vm;

@Log
public class OpenStackScan {


	private static Logger logger=LoggerFactory.getLogger(OpenStackScan.class.getName());
	private String tenant;


	@Autowired
	NovaApi novaApi;


	@Autowired
	CinderApi cinderApi;

	@Autowired
	CachedOpenstackApi cachedOpenstackApi;

	public OpenStackScan(String tenant){
		logger.info("create openstack tenant {}",tenant);
		this.tenant=tenant;
	}


	Set<Publiable> oldSetDisk = new HashSet<>();

	@Scheduled(fixedDelayString = "${exporter.disk.scan.delayms}")
	public void scanIaasDisks() {
		Set<Publiable> setDisk = new HashSet<>();
		Set<String> regions = cinderApi.getConfiguredRegions();
		for (String region : regions) {
			VolumeApi volumeApi = cinderApi.getVolumeApi(region);
			Set<? extends Volume> it = volumeApi.list().toSet();
			for (Volume v : it) {
				logger.info("vol: " + v);
				String id=v.getId();
				String name=v.getName();
				long size=v.getSize()*1024;
				boolean attached=(v.getAttachments().size()>0);
				Map<String, String> metadata=v.getMetadata();
				Disk disk=new Disk(id, name, attached, size);
				disk.publishMetrics();
				setDisk.add(disk);
			}
		}
		Utility.purgeOldData(oldSetDisk, setDisk);
	}


	Set<Publiable> oldSetVm = new HashSet<>();

	@Scheduled(fixedDelayString="${exporter.vm.scan.delayms}")
	public void scanIaasVms(){
		Set<Publiable> setVm = new HashSet<>();
		Set<String> regions = novaApi.getConfiguredRegions();

		for (String region : regions) {
			ServerApi serverApi = novaApi.getServerApi(region);

			logger.info("Servers in " + region);

			for (Server server : serverApi.listInDetail().concat()) {
				logger.info("  " + server);
				String id=server.getId();
				String name=server.getName();


				//FIXME parse network structure to get IP
				String address="";

				Collection<Address> adresses=server.getAddresses().values();
				if (adresses.size()>0){
					address=adresses.iterator().next().getAddr(); //only first IP
				}
				Flavor flavorDetails=this.cachedOpenstackApi.findFlavor(region, server.getFlavor().getId());

				Integer numberOfCpu=flavorDetails.getVcpus();
				Integer memoryMb=flavorDetails.getRam();
				boolean running=(server.getStatus()==org.jclouds.openstack.nova.v2_0.domain.Server.Status.ACTIVE);

				Map<String,String> metadata=server.getMetadata();
				server.getAvailabilityZone().or("none");
				String az=server.getAvailabilityZone().or("");

				Vm vm=new Vm(id,name,address,this.tenant,az,metadata,numberOfCpu,memoryMb,running);
				vm.publishMetrics();
				setVm.add(vm);
			}
		}
		Utility.purgeOldData(oldSetVm, setVm);
	}


}
