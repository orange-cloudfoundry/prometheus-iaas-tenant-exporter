package com.orange.oss.prometheus.iaasexporter.vcloud;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("exporter.vcloud.url")

public class VCloudConfig {

}
