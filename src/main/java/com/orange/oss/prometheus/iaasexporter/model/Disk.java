package com.orange.oss.prometheus.iaasexporter.model;

import io.prometheus.client.Gauge;

public class Disk {
	private String id;
	private String name;
	private boolean attached;
	private int size;	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public boolean isAttached() {
		return attached;
	}
	public int getSize() {
		return size;
	}
	public Disk(String id, String name, boolean attached, int size) {
		super();
		this.id = id;
		this.name = name;
		this.attached = attached;
		this.size = size;
	}
	
	private static final Gauge diskGauge = Gauge
	           .build()
	           .name("iaas_exporter_disk")
	           .labelNames("id", "name", "attached")
	           .help("iaas vm inventory metric")
	           .register();
	
	

	public void publishMetrics(){
		diskGauge.labels(this.id,this.name,Boolean.valueOf(this.attached).toString()).set(this.size);
	}
	

}
