package com.sec.version.entity.version;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WasanbonAndOsVersion {
	
	@JsonProperty("platform version")
	private String platformVersion;
	
	@JsonProperty("wasanbon version")
	private String wasanbonVersion;
	
	public String getWasanbonVersion() {
		return wasanbonVersion;
	}
}