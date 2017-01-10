package com.orange.oss.prometheus.iaasexporter.model;

import java.util.Map;

import io.prometheus.client.Gauge;

public class Vm {
	private String id;
	private String name;
	private String address;
	private Map<String,String> metadata;
	
	public Vm(String id, String name, String address, Map<String, String> metadata) {
		super();
		this.id = id;
		this.name = name;
		this.address = address;
		this.metadata = metadata;
	}
	
	private static final Gauge vmGauge = Gauge
	           .build()
	           .name("iaas_exporter_vm")
	           .labelNames("id", "name", "address")
	           .help("iaas vm inventory metric")
	           .register();
	
	
	
	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getAddress() {
		return address;
	}

	public Map<String, String> getMetadata() {
		return metadata;
	}


	
	
}
