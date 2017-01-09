#purpose
The aim of this component is to provide a prometheus exporter, for tenant level iaas info.
Although iaas admin level prometheus components exists, we needed a prometheus exporter helping a iaas tenant to manage its components

This exporter scans the iaas tenant api and retrieves
1 disk inventory
2 vms / instances inventory
3 floating / vip ips. 

The component will target the following iaas
1 vcloud director
2 openstack
3 cloudstack


#design
the component is based on spring boot / spring boot actuator.
Java Spring will help leveraging overall good iaas apis support on the java platform


#references

http://blog.monkey.codes/actuator-and-prometheus/
https://github.com/prometheus/client_java/pull/114



