package com.orange.oss.prometheus.iaasexporter.model;

import java.util.Map;

import org.springframework.util.Assert;

import io.prometheus.client.Gauge;

public class Vm {
	private String id;
	private String name;
	private String address;
	private String tenant;


	private String az;
	public String getAz() {
		return az;
	}

	private Map<String,String> metadata;
	
	public Vm(String id, String name, String address, String tenant,String az,Map<String, String> metadata) {
		super();
		Assert.notNull(id);
		Assert.notNull(name);
		Assert.notNull(address);
		Assert.notNull(az);
		this.id = id;
		this.name = name;
		this.address = address;
		this.metadata = metadata;
		this.tenant=tenant;
		this.az=az;
	}
	
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}
	public String getTenant() {
		return tenant;
	}
	

	public Map<String, String> getMetadata() {
		return metadata;
	}

	private static final Gauge vmGauge = Gauge
	           .build()
	           .name("iaas_exporter_vm")
	           .labelNames("id", "name", "address","tenant","az")
	           .help("iaas vm inventory metric")
	           .register();
	
	

	public void publishMetrics(){
		vmGauge.labels(this.id,this.name,this.address,this.tenant,this.az).set(1);
	}



	
}
