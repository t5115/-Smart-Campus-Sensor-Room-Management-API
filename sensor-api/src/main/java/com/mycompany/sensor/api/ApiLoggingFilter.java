/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sensor.api;

/**
 *
 * @author tahmi
 */
import java.util.logging.Logger;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.util.logging.Level;

@Provider
public class ApiLoggingFilter implements ContainerRequestFilter, ContainerResponseFilter {

    private static final Logger logger = Logger.getLogger(ApiLoggingFilter.class.getName());

    // Incoming request logging
    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String method = requestContext.getMethod();
        String uri = requestContext.getUriInfo().getRequestUri().toString();

        logger.log(Level.INFO, "Incoming Request -> Method: {0} | URI: {1}", new Object[]{method, uri});
    }

    // Outgoing response logging
    @Override
    public void filter(ContainerRequestContext requestContext,
                       ContainerResponseContext responseContext) throws IOException {

        int status = responseContext.getStatus();

        logger.log(Level.INFO, "Outgoing Response -> Status: {0}", status);
    }
}