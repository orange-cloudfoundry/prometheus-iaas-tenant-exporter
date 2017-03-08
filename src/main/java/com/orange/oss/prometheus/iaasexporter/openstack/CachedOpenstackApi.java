package com.orange.oss.prometheus.iaasexporter.openstack;

import org.jclouds.openstack.nova.v2_0.NovaApi;
import org.jclouds.openstack.nova.v2_0.domain.Flavor;
import org.jclouds.openstack.nova.v2_0.features.FlavorApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;

public class CachedOpenstackApi {

	private static Logger logger=LoggerFactory.getLogger(CachedOpenstackApi.class);
	
	@Autowired
	NovaApi novaApi;

	/**
	 * retrieves flavor details. This method is cached as flavor list is fairly static
	 * @param region
	 * @param flavorId
	 * @return
	 */
	@Cacheable("flavor-api")
	public Flavor findFlavor(String region, String flavorId) {
		logger.info("get detailed description for flavor {}",flavorId);
		FlavorApi flavorApi = this.novaApi.getFlavorApi(region);
		return flavorApi.get(flavorId);
	}

}
