package com.orange.oss.prometheus.iaasexporter.model;

import io.prometheus.client.Gauge;

public class Disk {
	private String id;
	private String name;
	private boolean attached;
	private long  sizeMo;	
	public String getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public boolean isAttached() {
		return attached;
	}
	public long getSizeMo() {
		return sizeMo;
	}
	public Disk(String id, String name, boolean attached, long sizeMo) {
		super();
		this.id = id;
		this.name = name;
		this.attached = attached;
		this.sizeMo = sizeMo;
	}
	
	private static final Gauge diskGauge = Gauge
	           .build()
	           .name("iaas_exporter_disk")
	           .labelNames("id", "name", "attached")
	           .help("iaas vm inventory metric")
	           .register();
	
	

	public void publishMetrics(){
		diskGauge.labels(this.id,this.name,Boolean.valueOf(this.attached).toString()).set(this.sizeMo);
	}
	
	
	public static void resetAllTimeSeries(){

		
	}
	

}
