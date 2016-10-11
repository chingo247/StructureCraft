/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structurecraft.economy;

import java.util.UUID;

/**
 *
 * @author Chingo
 */
public interface IEconomyActor {
    
    UUID getUUID();
    
    String getPlayerName();
    
    IEconomyAccount getAccount();
    
}
