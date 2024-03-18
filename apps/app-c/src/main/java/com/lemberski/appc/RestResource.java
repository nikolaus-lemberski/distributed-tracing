package com.lemberski.appc;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/")
public class RestResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String appC() {
        return "App C";
    }
}
