# Purpose
The aim of this component is to provide a prometheus exporter, for tenant level iaas info.
Although iaas admin level prometheus components exists, we needed a prometheus exporter helping a iaas tenant to manage its components

This exporter scans the iaas tenant api and retrieves
1 disk inventory
2 vms / instances inventory
3 floating / vip ips.
4 templates 

The component will target the following iaas :
1 vcloud director
2 openstack
3 cloudstack


The target runtime for the exporter:
1 standalone spring boot jar (ie: cloudfoundry compatible)
2 docker image
3 bosh release


# Design
the component is based on spring boot / spring boot actuator.
Java Spring will help leveraging overall good iaas apis support on the java platform
Spring boot web, and prometheus java API will help a productive exposition of prometheus metrics

No persistence involved


# References

http://blog.monkey.codes/actuator-and-prometheus/
https://github.com/prometheus/client_java/pull/114



