package org.pwte.example.resources;

import java.util.HashMap;
import java.util.Map;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.config.inject.ConfigProperty;


@Path("/keycloak")
@RequestScoped
public class JWTConfigResource {
	
	@Inject
	@ConfigProperty(name = "SSO_URI")
	private String keycloakURI;
	@Inject
	@ConfigProperty(name = "SSO_REALM")
	private String keycloakRealm;
	@Inject
	@ConfigProperty(name = "SSO_CLIENT_ID")
	private String keycloakClientID;

	@GET
	@Path("config.json")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getConfiguration()
	{
		Map<String,String> config = new HashMap<String, String>();
		config.put("realm", keycloakRealm);
		config.put("auth-server-url", keycloakURI);
		config.put("resource", keycloakClientID);

		return Response.ok().entity(config).build();
	}
}
