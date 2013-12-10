package com.xtencilLauncher.model;

import java.util.ArrayList;

public class FIProperties {
	
	private String sender;
	private String receiver;
	private String loggingEntries;
	private String dataEntries;
	private String xtencilEntries;
	private String swiftQueueEntries;
	private String queueEntries;
	private String queueParamsEntries;
	private String resolverEntries;
	private String xmlEntries;
	private String ediEntries;
	private String recordDaosEntries;
	private String oracleDBEntries;
	private String corecsDBEntries;
	private String itemDB_Preprod_Entries;
	private String itemDB_Prod_Entries;
	private String itemDB_Blade_Entries;
	private String locationDB_Preprod_Entries;
	private String locationDB_Prod_Entries;
	private String locationDB_Blade_Entries;
	private String nrfDBEntries;
	private String dc4DBEntries;
	private String regressionTestingEntries;
	private String versionControlEntries;
	private String validateContentAcrossDocsEntries;
	private String bladeXTS_Entries;
	private String preprodXTS_Entries;
	private String prodXTS_Entries;
	private String bladeDRE_Entries;
	private String testSystem;
	private Boolean loadFromFile;
	private ArrayList<String> propertiesList;

	

	public ArrayList<String> getPropertiesList() {
		return propertiesList;
	}



	public FIProperties(Boolean loadFromFile) {
		super();
		this.loadFromFile = loadFromFile;
		this.propertiesList = new ArrayList<String>();
		if (!this.loadFromFile) {
			
//			this.setDebugEntry("sps.dbug.opts=true\r\n");
//			this.setLoggingEntries("sps.dbug.opts=true\r\nsps.queue.service.audit=true\r\nsps.queue.service.auditdir=audit/${XtencilNet}\r\nsps.queue.service.audit.usedate=false");
			this.setLoggingEntries("sps.queue.service.audit=true\r\nsps.queue.service.auditdir=audit/${XtencilNet}\r\nsps.queue.service.audit.usedate=false");
			this.setDataEntries("xts.read.maxErrors=100\r\nsps.fi.checkTarget=true\r\ntruncateSource=true\r\nsps.fi.useTargetAppKeys=false\r\nsps.util.jarClassLoader.maxMapCache=4");
			this.setXtencilEntries("sps.fi.xtencilDir=../FIMaps\r\nsps.deployer.xtencil.xtlSrcDir=/jv/implementation/maps\r\nxtencil.dtd.default=conf/form/Xtencil.dtd\r\nsps.deployer.xtencil.updateMaps=false\r\nsps.deployer.xtencil.unable.faiFl=false");
			this.setQueueEntries("sps.fi.jms.dcType=dc4\r\nsps.queue.node.context=com.evermind.server.rmi.RMIInitialContextFactory\r\nsps.queue.node.jmsurl=ormi\\://hoover.corp.spscommerce.net\\:12410\r\nsps.queue.node.usr=oc4jadmin\r\nsps.queue.node.pwd=password1\r\nsps.queue.node.input=jms/XG2\r\nsps.queue.node.notify=jms/DCNOTIFICATIONS\r\nsps.queue.node.output=jms/DCMESSAGE\r\nsps.queue.discardOutput=false");
			this.setQueueParamsEntries("sps.queue.service.formats.params.AUDIT=sps.queue.service.audit\r\nsps.queue.service.formats.params.DEBUG=sps.dbug.opts\r\nsps.queue.service.formats.params.DISCARD_OUTPUT=sps.queue.discardOutput\r\nsps.queue.service.formats.params.CHECK_TARGET=sps.fi.checkTarget\r\nsps.queue.service.formats.params.XTENCIL_NET=XtencilNet\r\nsps.queue.service.formats.params.XML_SPEC=xmlDTD\r\nsps.queue.service.formats.params.XML_DOC_TYPE=xmlDocType\r\nsps.queue.service.formats.params.XML_ATTRIBUTE_ROLE=xmlAttributeRole\r\nsps.queue.service.formats.params.XMLValidation=xmlresolver.parser.validate\r\nsps.queue.service.formats.params.XMLValidation.2=xmlresolver.parser.validate.schema\r\nsps.queue.service.formats.params.val.XMLValidation.NONE=false\r\nsps.queue.service.formats.params.val.XMLValidation.2.NONE=false\r\nsps.queue.service.formats.params.val.XMLValidation.ALL=true\r\nsps.queue.service.formats.params.val.XMLValidation.2.ALL=true\r\nsps.queue.service.formats.params.val.XMLValidation.DTD=true\r\nsps.queue.service.formats.params.val.XMLValidation.2.DTD=false\r\nsps.queue.service.formats.params.val.XMLValidation.SCHEMA=false\r\nsps.queue.service.formats.params.val.XMLValidation.2.SCHEMA=true");
			this.setResolverEntries("sps.fi.autoinsert.src=false\r\nsps.fi.autoinsert.tgt=false\r\nsps.fi.autofetch.src=false\r\nsps.fi.autofetch.tgt=false");
			this.setXmlEntries("org.xml.sax.xmlreader=org.apache.xerces.parsers.SAXParser\r\nxmlresolver.parser.type=SAX\r\nxmlresolver.parser.validate=false\r\nxmlresolver.parser.continue_after_error=true\r\nxmlresolver.parser.continue_after_error_feature=http\\://apache.org/xml/features/continue-after-fatal-error\r\nxmlresolver.parser.validate.schema=false\r\nxmlChunk=true");
			this.setEdiEntries("sps.fi.jaim.feds.legacy=true\r\nsps.fi.jaim.feds.validate.fetch=true\r\nsps.jaim.feds.strictChecking=false\r\nsps.jaim.feds.strictChecking.inbound=false\r\nsps.jaim.feds.strictChecking.outbound=false");
			this.setRecordDaosEntries("sps.app.record.finder.daoFactory=sps.integration.resource.dao.RecordFinderFactory\r\nsps.integration.resource.dao.finderFactory.custom=\\\r\nsps.item.*=sps.item.master.dao.ItemInfoDAO,\\\r\nsps.entity.override.*=sps.entity.override.dao.EntityOverrideDAO,\\\r\nsps.location.*=sps.location.dao.LocationDAO,\\\r\nsps.nrf.*=sps.nrf.dao.NRFDAO");
			this.setCorecsDBEntries("sps.corecs.dao.jndiLocation=jdbc/Corecs\r\nsps.corecs.dao.driver=oracle.jdbc.driver.OracleDriver\r\nsps.corecs.dao.url=jdbc:oracle:thin:@//impodb01.corp.spscommerce.net:1521/fitest\r\nsps.corecs.dao.user=iwb\r\nsps.corecs.dao.password.{encrypted}=nEANQYCAJeopvWBRBslmBA==\r\nsps.corecs.dao.maxRecords=25000");
			this.setNrfDBEntries("sps.nrf.dao.driver=oracle.jdbc.driver.OracleDriver\r\nsps.nrf.dao.url=jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=on)(ADDRESS=(PROTOCOL=tcp)(HOST=rdb01-vip.mps.spscommerce.net)(PORT=1521))(ADDRESS=(PROTOCOL=tcp)(HOST=rdb02-vip.mps.spscommerce.net)(PORT=1521))(ADDRESS=(PROTOCOL=tcp)(HOST=rdb03-vip.mps.spscommerce.net)(PORT=1521))(ADDRESS=(PROTOCOL=tcp)(HOST=rdb7-vip.mps.spscommerce.net)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=nrf)))\r\nsps.nrf.dao.user=\r\nsps.nrf.dao.password=\r\nsps.nrf.dao.maxRecords=25000");
			this.setDc4DBEntries("sps.dc4.dao.driver=oracle.jdbc.driver.OracleDriver\r\nsps.dc4.dao.url=jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=on)(ADDRESS=(PROTOCOL=tcp)(HOST=rdbdc01-vip.mps.spscommerce.net)(PORT=1521))(ADDRESS=(PROTOCOL=tcp)(HOST=rdbdc02-vip.mps.spscommerce.net)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=drtrck)))\r\nsps.dc4.dao.user=spscdc4\r\nsps.dc4.dao.password.{encrypted}=1qpMhNx06yA=\r\nsps.dc4.dao.maxRecords=30000");
			this.setRegressionTestingEntries("sps.fi.junit.testdir=../FITest/test");
			this.setVersionControlEntries("sps.deployer.xtencil.get.versioncontrol=svn\r\nsps.deployer.xtencil.get.svn=svn\r\nsps.deployer.xtencil.get.svn.update=up\r\nsps.deployer.xtencil.get.svn.update.options=");
			this.setBladeXTS_Entries("sps.xg.db.driver=oracle.jdbc.driver.OracleDriver\r\nsps.xg.db.url=jdbc:oracle:thin:@impodb01:1521:fitest\r\nsps.xg.db.user=xts\r\nsps.xg.db.password=geksonck9");
			this.setBladeXTS_Entries("sps.xg.db.url=jdbc:postgresql://prexrefprime01:5432/xts\r\nsps.xg.db.driver=org.postgresql.Driver\r\nsps.xg.db.user=xts\r\nsps.xg.db.password=Camwed\r\n#sps.xg.db.password.{encrypted}=RHdnWMJ3qOA=\r\nsps.xg.db.critical=true\r\nsps.xg.db.maxQueryRetries=1\r\nsps.xg.db.validationQuery=SELECT NOW() ");
			this.setBladeXTS_Entries("sps.xg.db.driver=oracle.jdbc.driver.OracleDriver\r\nsps.xg.db.url=jdbc:oracle:thin:@impodb01:1521:fitest\r\nsps.xg.db.user=xts\r\nsps.xg.db.password.{encrypted}=y3uldoIYmcbD9HHpr9t/7g==\r\nsps.xg.db.maxQueryRetries=1\r\n");
			this.setBladeXTS_Entries("sps.xg.db.driver=oracle.jdbc.driver.OracleDriver\r\nsps.xg.db.url=jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=bldxtsprime01)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=fitest)))\r\nsps.xg.db.user=xts\r\nsps.xg.db.password.{encrypted}=y3uldoIYmcbD9HHpr9t/7g==\r\nsps.xg.db.maxQueryRetries=2\r\n\r\nsps.location.dao.url=jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS=(PROTOCOL=tcp)(HOST=bldxtsprime01)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=fitest)))\r\nsps.location.dao.user=xts\r\nsps.location.dao.password=geksonck9\r\n\r\nsps.item.dao.driver=oracle.jdbc.driver.OracleDriver\r\nsps.item.dao.url=jdbc:oracle:thin:@(DESCRIPTION=(LOAD_BALANCE=on)(ADDRESS=(PROTOCOL=TCP)(HOST=blddcprime01)(PORT=1521))(CONNECT_DATA=(SERVICE_NAME=fitest)))\r\nsps.item.dao.user=SPSITEM\r\nsps.item.dao.password.{encrypted}=opFLMt3+7W1aY30Co/oALA==\r\nsps.item.dao.dateformat=MM-dd-yyyy\r\nsps.item.dao.toDateFunction=to_date");
			this.setBladeDRE_Entries("sps.dre.jdbcDriver=oracle.jdbc.driver.OracleDriver\r\nsps.dre.jdbcURL=jdbc:oracle:thin:@impodb01:1521:fitest\r\nsps.dre.databaseUsername=dre\r\nsps.dre.databasePassword=geksonck9\r\n");
			this.setPreprodXTS_Entries("sps.xg.db.url=jdbc:postgresql://prexrefprime01:5432/xts\r\nsps.xg.db.driver=org.postgresql.Driver\r\nsps.xg.db.user=xts\r\nsps.xg.db.password.{encrypted}=RHdnWMJ3qOA=\r\nsps.xg.db.critical=true\r\nsps.xg.db.maxQueryRetries=1\r\nsps.xg.db.validationQuery=SELECT NOW()\r\n\r\n");
			this.setProdXTS_Entries("sps.xg.db.url=jdbc:postgresql://xrefpdbprime01.mps.spscommerce.net:5432/xts\r\nsps.xg.db.driver=org.postgresql.Driver\r\nsps.xg.db.user=xts\r\nsps.xg.db.password.{encrypted}=RHdnWMJ3qOA=\r\nsps.xg.db.critical=true\r\nsps.xg.db.maxQueryRetries=1\r\nsps.xg.db.validationQuery=SELECT NOW()\r\n\r\n");
		}
	}
	
	public void addStandardProperties(){
		propertiesList.add(this.loggingEntries);
		propertiesList.add(this.dataEntries);
		propertiesList.add(this.xtencilEntries);
		propertiesList.add(this.queueEntries);
		propertiesList.add(this.queueParamsEntries);
		propertiesList.add(this.resolverEntries);
		propertiesList.add(this.xmlEntries);
		propertiesList.add(this.ediEntries);
		propertiesList.add(this.recordDaosEntries);
		propertiesList.add(this.corecsDBEntries);
		propertiesList.add(this.nrfDBEntries);
		propertiesList.add(this.dc4DBEntries);
		propertiesList.add(this.regressionTestingEntries);
		propertiesList.add(this.versionControlEntries);
		propertiesList.add(this.bladeDRE_Entries);
	}
	
	public void clearStandardProperties(){
		propertiesList.clear();
	}

	public void addProperty(String property){
		propertiesList.add(property);	
	}

	public String getSender() {
		return sender;
	}

	public void setSender(String sender) {
		this.sender = sender;
	}

	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getLoggingEntries() {
		return loggingEntries;
	}

	public void setLoggingEntries(String loggingEntries) {
		this.loggingEntries = loggingEntries;
	}

	public String getDataEntries() {
		return dataEntries;
	}

	public void setDataEntries(String dataEntries) {
		this.dataEntries = dataEntries;
	}

	public String getXtencilEntries() {
		return xtencilEntries;
	}

	public void setXtencilEntries(String xtencilEntries) {
		this.xtencilEntries = xtencilEntries;
	}

	public String getSwiftQueueEntries() {
		return swiftQueueEntries;
	}

	public void setSwiftQueueEntries(String swiftQueueEntries) {
		this.swiftQueueEntries = swiftQueueEntries;
	}

	public String getQueueEntries() {
		return queueEntries;
	}

	public void setQueueEntries(String queueEntries) {
		this.queueEntries = queueEntries;
	}

	public String getQueueParamsEntries() {
		return queueParamsEntries;
	}

	public void setQueueParamsEntries(String queueParamsEntries) {
		this.queueParamsEntries = queueParamsEntries;
	}

	public String getResolverEntries() {
		return resolverEntries;
	}

	public void setResolverEntries(String resolverEntries) {
		this.resolverEntries = resolverEntries;
	}

	public String getXmlEntries() {
		return xmlEntries;
	}

	public void setXmlEntries(String xmlEntries) {
		this.xmlEntries = xmlEntries;
	}

	public String getEdiEntries() {
		return ediEntries;
	}

	public void setEdiEntries(String ediEntries) {
		this.ediEntries = ediEntries;
	}

	public String getRecordDaosEntries() {
		return recordDaosEntries;
	}

	public void setRecordDaosEntries(String recordDaosEntries) {
		this.recordDaosEntries = recordDaosEntries;
	}

	public String getOracleDBEntries() {
		return oracleDBEntries;
	}

	public void setOracleDBEntries(String oracleDBEntries) {
		this.oracleDBEntries = oracleDBEntries;
	}

	public String getCorecsDBEntries() {
		return corecsDBEntries;
	}

	public void setCorecsDBEntries(String corecsDBEntries) {
		this.corecsDBEntries = corecsDBEntries;
	}

	public String getItemDB_Preprod_Entries() {
		return itemDB_Preprod_Entries;
	}

	public void setItemDB_Preprod_Entries(String itemDB_Preprod_Entries) {
		this.itemDB_Preprod_Entries = itemDB_Preprod_Entries;
	}

	public String getItemDB_Prod_Entries() {
		return itemDB_Prod_Entries;
	}

	public void setItemDB_Prod_Entries(String itemDB_Prod_Entries) {
		this.itemDB_Prod_Entries = itemDB_Prod_Entries;
	}

	public String getItemDB_Blade_Entries() {
		return itemDB_Blade_Entries;
	}

	public void setItemDB_Blade_Entries(String itemDB_Blade_Entries) {
		this.itemDB_Blade_Entries = itemDB_Blade_Entries;
	}

	public String getLocationDB_Preprod_Entries() {
		return locationDB_Preprod_Entries;
	}

	public void setLocationDB_Preprod_Entries(String locationDB_Preprod_Entries) {
		this.locationDB_Preprod_Entries = locationDB_Preprod_Entries;
	}

	public String getLocationDB_Prod_Entries() {
		return locationDB_Prod_Entries;
	}

	public void setLocationDB_Prod_Entries(String locationDB_Prod_Entries) {
		this.locationDB_Prod_Entries = locationDB_Prod_Entries;
	}

	public String getLocationDB_Blade_Entries() {
		return locationDB_Blade_Entries;
	}

	public void setLocationDB_Blade_Entries(String locationDB_Blade_Entries) {
		this.locationDB_Blade_Entries = locationDB_Blade_Entries;
	}

	public String getNrfDBEntries() {
		return nrfDBEntries;
	}

	public void setNrfDBEntries(String nrfDBEntries) {
		this.nrfDBEntries = nrfDBEntries;
	}

	public String getDc4DBEntries() {
		return dc4DBEntries;
	}

	public void setDc4DBEntries(String dc4dbEntries) {
		dc4DBEntries = dc4dbEntries;
	}

	public String getRegressionTestingEntries() {
		return regressionTestingEntries;
	}

	public void setRegressionTestingEntries(String regressionTestingEntries) {
		this.regressionTestingEntries = regressionTestingEntries;
	}

	public String getVersionControlEntries() {
		return versionControlEntries;
	}

	public void setVersionControlEntries(String versionControlEntries) {
		this.versionControlEntries = versionControlEntries;
	}

	public String getValidateContentAcrossDocsEntries() {
		return validateContentAcrossDocsEntries;
	}

	public void setValidateContentAcrossDocsEntries(
			String validateContentAcrossDocsEntries) {
		this.validateContentAcrossDocsEntries = validateContentAcrossDocsEntries;
	}

	public String getBladeXTS_Entries() {
		return bladeXTS_Entries;
	}

	public void setBladeXTS_Entries(String bladeXTS_Entries) {
		this.bladeXTS_Entries = bladeXTS_Entries;
	}

	public String getPreprodXTS_Entries() {
		return preprodXTS_Entries;
	}

	public void setPreprodXTS_Entries(String preprodXTS_Entries) {
		this.preprodXTS_Entries = preprodXTS_Entries;
	}

	public String getProdXTS_Entries() {
		return prodXTS_Entries;
	}

	public void setProdXTS_Entries(String prodXTS_Entries) {
		this.prodXTS_Entries = prodXTS_Entries;
	}

	public String getBladeDRE_Entries() {
		return bladeDRE_Entries;
	}

	public void setBladeDRE_Entries(String bladeDRE_Entries) {
		this.bladeDRE_Entries = bladeDRE_Entries;
	}

	public String getTestSystem() {
		return testSystem;
	}

	public void setTestSystem(String testSystem) {
		this.testSystem = testSystem;
	}
	
}
