package com.orange.oss.prometheus.iaasexporter.cloudstack;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty("exporter.openstack.url")

public class CloudStackConfig {

}
