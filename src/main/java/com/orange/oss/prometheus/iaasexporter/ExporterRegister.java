package com.orange.oss.prometheus.iaasexporter;

import java.util.List;

import io.prometheus.client.Collector;

/**
 * Metric exporter register bean to register a list of exporters to the default
 * registry
 */
public class ExporterRegister {

     private List<Collector> collectors; 

     public ExporterRegister(List<Collector> collectors) {
          for (Collector collector : collectors) {
              collector.register();
          }
          this.collectors = collectors;
     }

     public List<Collector> getCollectors() {
          return collectors;
     }

}