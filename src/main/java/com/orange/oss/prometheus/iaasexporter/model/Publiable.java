package com.orange.oss.prometheus.iaasexporter.model;

public interface Publiable {
    public void publishMetrics();

    public void unpublishMetrics();
}
