package com.orange.oss.prometheus.iaasexporter.vcloud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import com.orange.oss.prometheus.iaasexporter.model.Disk;
import com.orange.oss.prometheus.iaasexporter.model.Vm;
import com.vmware.vcloud.api.rest.schema.QueryResultDiskRecordType;
import com.vmware.vcloud.api.rest.schema.QueryResultVMRecordType;
import com.vmware.vcloud.sdk.RecordResult;
import com.vmware.vcloud.sdk.VCloudException;
import com.vmware.vcloud.sdk.VcloudClient;

//@Component
//@ConditionalOnProperty("exporter.vcloud.endpoint")

public class VCloudScan {

	private static Logger logger=LoggerFactory.getLogger(VCloudScan.class.getName());
	
	
	@Autowired
	VcloudClient vcc;
	
	private String org;
	
	public VCloudScan(String org){
		this.org=org;
	}
	
	@Scheduled(fixedDelayString = "${exporter.disk.scan.delayms}")
	public void scanIaasDisks() throws VCloudException {
		RecordResult<QueryResultDiskRecordType> paginatedResult = vcc.getQueryService().queryDiskIdRecords();
		while (paginatedResult!=null) {
			List<QueryResultDiskRecordType> disks = paginatedResult.getRecords();

			for (QueryResultDiskRecordType v : disks) {
				logger.info("vol: " + v);
				String id = v.getId();
				String name = v.getName();
				long size = v.getSizeB() / 1024 / 1024; //bytes to Mo
				boolean attached = v.isIsAttached();
				Disk disk = new Disk(id, name, attached, size);
				disk.publishMetrics();
			}
			paginatedResult = (paginatedResult.hasNextPage() ? paginatedResult.getNextPage() : null);
		}
	}
	
	@Scheduled(fixedDelayString = "${exporter.vm.scan.delayms}")
	public void scanIaasVms() throws VCloudException {
		RecordResult<QueryResultVMRecordType> paginatedResult = vcc.getQueryService().queryVmIdRecords();
		while (paginatedResult!=null) {
			List<QueryResultVMRecordType> vms = paginatedResult.getRecords();
			for (QueryResultVMRecordType server : vms) {
				logger.info("  " + server);
				String id = server.getId();
				String name = server.getName();
				
				// FIXME parse network structure to get IP
				String address = "1.1.1.1";

				Map<String, String> metadata = new HashMap<String, String>(); // .getMetadata().getOtherAttributes()
				String az = server.getVdc();

				Vm vm = new Vm(id, name, address, this.org, az, metadata);
				vm.publishMetrics();
			}
			paginatedResult = (paginatedResult.hasNextPage() ? paginatedResult.getNextPage() : null);
		}
	}
	/* VDC quota management
            // ReST QueryService
            QueryService queryService = vc.getQueryService();
            // vdc org
            RecordResult<QueryResultOrgVdcRecordType> orgVcResults = queryService.queryRecords(QueryRecordType.ORGVDC);
            for (QueryResultOrgVdcRecordType orgVdcRecord : orgVcResults.getRecords()) {
                IaaSCapacitySetter curOrgInfos = new IaaSCapacitySetter(orgVdcRecord.getOrgName());
                curOrgInfos.setVdcName(orgVdcRecord.getName());
                curOrgInfos.setMemoryAllocationMB(orgVdcRecord.getMemoryAllocationMB());
                curOrgInfos.setMemoryUsedMB(orgVdcRecord.getMemoryUsedMB());
                curOrgInfos.setMemoryLimitMB(orgVdcRecord.getMemoryLimitMB());
                curOrgInfos.setCpuAllocationMhz(orgVdcRecord.getCpuAllocationMhz());
                curOrgInfos.setCpuUsedMhz(orgVdcRecord.getCpuUsedMhz());
                curOrgInfos.setCpuLimitMhz(orgVdcRecord.getCpuLimitMhz());
                curOrgInfos.setStorageAllocationMB(orgVdcRecord.getStorageAllocationMB());
                curOrgInfos.setStorageUsedMB(orgVdcRecord.getStorageUsedMB());
                curOrgInfos.setStorageLimitMB(orgVdcRecord.getStorageLimitMB());
                orgsInfos.add(curOrgInfos);
	 * 
	 */
}

