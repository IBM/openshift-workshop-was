package org.pwte.example.health;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.health.HealthCheck;
import org.eclipse.microprofile.health.HealthCheckResponse;
import org.eclipse.microprofile.health.Readiness;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
@Readiness
public class ReadinessCheck implements HealthCheck {

	@Inject
	@ConfigProperty(name="SSO_URI")
	private String keycloakURI;
	
	@Override
	public HealthCheckResponse call() {
		HttpURLConnection con = null;
		try {
			
			URL url = new URL(keycloakURI);
			con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("GET");
			int status = con.getResponseCode();
			if (status != 200) {
				System.out.println("Readiness check - status:" + status);
				return HealthCheckResponse.named("Readiness").down().build();
			}

		} catch (IOException e) {
			System.out.println("Readiness check - exception:" + e);
			return HealthCheckResponse.named("Readiness").down().build();

		}
		finally {
			con.disconnect();
		}
		return HealthCheckResponse.named("Readiness").up().build();
	}

}
