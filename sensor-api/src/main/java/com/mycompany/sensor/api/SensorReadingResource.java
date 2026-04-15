/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sensor.api;

/**
 *
 * @author tahmi
 */
import javax.ws.rs.*;
import javax.ws.rs.core.*;

@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorReadingResource {
    
    private final Sensor sensor;

    public SensorReadingResource(Sensor sensor) {
        this.sensor = sensor;
    }

    @GET
    public Response getReadings() {
        return Response.ok(sensor.getReadings()).build();
    }

    @POST
    public Response addReading(Reading reading) {
        sensor.addReading(reading);
        
        sensor.setCurrentValue(reading.getValue());
        
        return Response.status(Response.Status.CREATED)
                .entity(reading)
                .build();
    }
}