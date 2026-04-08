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

@Path("/rooms")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class SensorRoom {
    
    // In-memory storage (shared across requests)
    private static final Map<String, Room> rooms = new ConcurrentHashMap<>();

    // GET /api/v1/rooms
    @GET
    public Collection<Room> getAllRooms() {
        return rooms.values();
    }

    // POST /api/v1/rooms
    @POST
    public Response createRoom(Room room) {

        String id = UUID.randomUUID().toString();
        room.setId(id);

        rooms.put(id, room);

        return Response.status(Response.Status.CREATED)
                .entity(room)
                .build();
    }

    // GET /api/v1/rooms/{roomId}
    @GET
    @Path("/{roomId}")
    public Response getRoom(@PathParam("roomId") String roomId) {

        Room room = rooms.get(roomId);

        if (room == null) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("{\"error\":\"Room not found\"}")
                    .build();
        }

        return Response.ok(room).build();
    }
    
// DELETE /api/v1/rooms/{roomId}
@DELETE
@Path("/{roomId}")
public Response deleteRoom(@PathParam("roomId") String roomId) {

    Room room = rooms.get(roomId);

    if (room == null) {
        return Response.status(Response.Status.NOT_FOUND)
                .entity("{\"error\":\"Room not found\"}")
                .build();
    }

    // Business rule: cannot delete if sensors exist
    if (room.hasSensors()) {
        return Response.status(Response.Status.CONFLICT)
                .entity("{\"error\":\"Room cannot be deleted because it has active sensors\"}")
                .build();
    }

    rooms.remove(roomId);

    return Response.noContent().build(); // 204
}
}

