/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.sensor.api;

/**
 *
 * @author tahmi
 */

import java.util.ArrayList;
import java.util.List;

public class Room {

    private String id;
    private String name;
    private List<String> sensors = new ArrayList<>();

    public Room() {}

    public String getId(){
        return id;
    }
    public void setId(String id){
        this.id = id;
    }

    public String getName(){
        return name;
    }
    
    public void setName(String name){
        this.name = name;
    }

    public List<String> getSensors(){
        return sensors;
    }
    
    public void setSensors(List<String> sensors){
        this.sensors = sensors;
    }
    
    public boolean hasSensors(){
        return sensors != null && !sensors.isEmpty();
    }
}
 