package com.orange.oss.prometheus.iaasexporter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PrometheusIaasTenantExporterApplication {

	public static void main(String[] args) {
		SpringApplication.run(PrometheusIaasTenantExporterApplication.class, args);
	}
	
	
	
	
	
	
}
