package com.elite.example.firebase.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

import lombok.Data;

@ConfigurationProperties(prefix = "com.hardik.sunspot")
@Data
public class FireBaseConfigurationProperties {

	private Configuration firebase = new Configuration();

	@Data
	public class Configuration {

		private String firebasePrivateKey;

	}

}