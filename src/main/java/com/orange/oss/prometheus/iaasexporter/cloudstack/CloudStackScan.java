package com.orange.oss.prometheus.iaasexporter.cloudstack;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.jclouds.cloudstack.CloudStackApi;
import org.jclouds.cloudstack.domain.VirtualMachine;
import org.jclouds.cloudstack.domain.Volume;
import org.jclouds.cloudstack.domain.Zone;
import org.jclouds.cloudstack.options.ListVirtualMachinesOptions;
import org.jclouds.cloudstack.options.ListVolumesOptions;
import org.jclouds.cloudstack.options.ListZonesOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.orange.oss.prometheus.iaasexporter.model.Disk;
import com.orange.oss.prometheus.iaasexporter.model.Vm;

@Component
@ConditionalOnProperty("exporter.openstack.url")
public class CloudStackScan {

	private static Logger logger=LoggerFactory.getLogger(CloudStackScan.class.getName());
	private String zone;
	
	@Autowired
	private CloudStackApi api;

	
	public CloudStackScan(String zone){
		this.zone=zone;
	}
	
	
	@Scheduled(fixedDelayString = "${exporter.disk.scan.delayms}")
	public void scanIaasDisks() {
	
	logger.info("start scanning cloustack disks");
	
	Set<Volume> listVolumes = api.getVolumeApi().listVolumes(ListVolumesOptions.Builder.zoneId(this.findZoneId()));
	for (Volume v: listVolumes){
		logger.info("vol:"+v.toString());
		String id= v.getId();
		String name= v.getName();
		long size=v.getSize();
		boolean attached=(v.getVirtualMachineId()!=null);
		Disk d=new Disk(id, name, attached, size);
		d.publishMetrics();
	}
	}
	
	@Scheduled(fixedDelayString = "${exporter.vm.scan.delayms}")
	public void scanIaasVms() {
		logger.info("start scanning cloustack vms");
		Set<VirtualMachine> vms = api.getVirtualMachineApi().listVirtualMachines(ListVirtualMachinesOptions.Builder.zoneId(this.findZoneId()));
		for (VirtualMachine vm : vms) {
			logger.info("vm:" + vm.toString());
			String id=vm.getId();
			String name=vm.getName();
			String address=vm.getIPAddress();
			if (address==null){
				address=""; //case of booting vm..
			}
			
			Map<String, String> metadatas=new HashMap<String,String>();
			Vm v=new Vm(id,name, address, this.zone,metadatas);
			v.publishMetrics();
		}
	}	

	
	/**
	 * utility to retrieve cloudstack zoneId
	 * 
	 * @return
	 */
	@Cacheable("zone")
	private String findZoneId() {
		// TODO: select the exact zone if multiple available
		ListZonesOptions zoneOptions = ListZonesOptions.Builder.available(true);
		Set<Zone> zones = api.getZoneApi().listZones(zoneOptions);
		Assert.notEmpty(zones, "No Zone available");
		String id="";
		for (Zone z:zones){
			if (z.getName().equalsIgnoreCase(this.zone)){
				id=z.getId();
			}
		}

		Assert.isTrue((id.length()>0),"Zone not found " + zone);
		return id;
	}
	

}
