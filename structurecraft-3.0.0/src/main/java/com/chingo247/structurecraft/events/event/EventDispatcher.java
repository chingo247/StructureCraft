/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structurecraft.events.event;

import com.google.common.eventbus.EventBus;
import java.util.ArrayList;
import java.util.List;

/**
 * Implementation of {@link IEventDispatcher}
 * @author Chingo
 */
public class EventDispatcher {
    
    public static final EventDispatcher IMP = new EventDispatcher();
    
    private List<EventBus> eventBusses;

    private EventDispatcher() {
        this.eventBusses = new ArrayList<>();
    }
    

    public void dispatchEvent(Object event) {
        for(EventBus e : eventBusses) {
            e.post(event);
        }
    }

    public void register(EventBus eventBus) {
        this.eventBusses.add(eventBus);
    }
    
}
