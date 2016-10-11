/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structurecraft.economy;

/**
 *
 * @author Chingo
 */
public class Transaction {
    
    private IEconomy economy;
    private IEconomyAccount source;
    private IEconomyAccount destination;

    public Transaction(IEconomy economy) {
    }

    public void setDestination(IEconomyAccount destination) {
        this.destination = destination;
    }

    public void setSource(IEconomyAccount source) {
        this.source = source;
    }
    
    public void execute() {
        
    }
    
}
