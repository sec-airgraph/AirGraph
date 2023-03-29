package com.sec.version.entity.version;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WasanbonVersion {
	
	@JsonProperty("wasanbon version")
	private WasanbonAndOsVersion wasanbonAndOsVersion;
	
	@JsonProperty("wasanbon-webframework version")
	private String wasanbonWebframeworkVersion;
	
	public String getWasanbonVersion() {
		return wasanbonAndOsVersion.getWasanbonVersion();
	}
}


