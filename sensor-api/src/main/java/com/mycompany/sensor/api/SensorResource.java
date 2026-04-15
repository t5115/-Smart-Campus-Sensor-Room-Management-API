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
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Path("/sensors")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorResource {
  // In-memory sensor storage
    private static final Map<String, Sensor> sensors = new ConcurrentHashMap<>();

@POST
public Response createSensor(Sensor sensor) {

    if (sensor.getRoomId() == null || sensor.getRoomId().isEmpty()) {
        return Response.status(Response.Status.BAD_REQUEST)
                .entity("{\"error\":\"roomId is required\"}")
                .build();
    }

    // Directly access rooms here
    Room room = SensorRoom.getRooms().get(sensor.getRoomId());

    if (room == null) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"Room does not exist\"}")
                .build();
    }

    String id = UUID.randomUUID().toString();
    sensor.setId(id);

    sensors.put(id, sensor);
    
    room.getSensors().add(id);
    
    return Response.status(Response.Status.CREATED)
            .entity(sensor)
            .build();
}

@GET
public Collection<Sensor> getSensors(@QueryParam("type") String type) {

    if (type == null || type.isEmpty()) {
        return sensors.values(); // return all sensors
    }

    return sensors.values().stream()
            .filter(sensor -> type.equalsIgnoreCase(sensor.getType()))
            .toList();
}
// Task 4:
@Path("{sensorId}/reading")
public SensorReadingResource getSensorReadingResource(@PathParam("sensorId") String sensorId) {
    
    Sensor sensor = sensors.get(sensorId);

    if (sensor == null) {
        throw new NotFoundException("Sensor not found");
    }

    return new SensorReadingResource(sensor);
}
}