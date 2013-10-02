package org.openrosa.client.postprocess;

public class PostProcessProperties {
	
	private String DIRNAME_CSV = ".";
	private String DIRNAME_XML_STORAGE = ".";
	private String DIRNAME_XML_DEV = ".";
	private String LOGGING_LEVEL = "OFF";
	
	
	public PostProcessProperties(String dIRNAME_CSV,
			String dIRNAME_XML_STORAGE, String dIRNAME_XML_DEV,
			String lOGGING_LEVEL) {
		super();
		DIRNAME_CSV = dIRNAME_CSV;
		DIRNAME_XML_STORAGE = dIRNAME_XML_STORAGE;
		DIRNAME_XML_DEV = dIRNAME_XML_DEV;
		LOGGING_LEVEL = lOGGING_LEVEL;
	}
	
	
	public String getDIRNAME_CSV() {
		return DIRNAME_CSV;
	}
	public void setDIRNAME_CSV(String dIRNAME_CSV) {
		DIRNAME_CSV = dIRNAME_CSV;
	}
	public String getDIRNAME_XML_STORAGE() {
		return DIRNAME_XML_STORAGE;
	}
	public void setDIRNAME_XML_STORAGE(String dIRNAME_XML_STORAGE) {
		DIRNAME_XML_STORAGE = dIRNAME_XML_STORAGE;
	}
	public String getDIRNAME_XML_DEV() {
		return DIRNAME_XML_DEV;
	}
	public void setDIRNAME_XML_DEV(String dIRNAME_XML_DEV) {
		DIRNAME_XML_DEV = dIRNAME_XML_DEV;
	}
	public String getLOGGING_LEVEL() {
		return LOGGING_LEVEL;
	}
	public void setLOGGING_LEVEL(String lOGGING_LEVEL) {
		LOGGING_LEVEL = lOGGING_LEVEL;
	}
	
	

}
