/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.chingo247.structurecraft.util;

/**
 *
 * @author Chingo
 */
public class VersionUtil {
    
    private VersionUtil() {}
    
    /**
     * Compares two versions of the same format. Expected format is: #.#.#
     * This method accepts any number of #. (e.g. 2.3.3.3.3.3.3 or 2.4.5.6).
     * 
     * This method will return:
     * -1 if versionA &lt versionB, 1 if versionA &gt versionB, 0 if versions are equal
     * @param versionA
     * @param versionB
     * @return comp result
     */
    public static int compare(String versionA, String versionB) {
        String[] versionsA = versionA.split("\\.");
        String[] versionsB = versionB.split("\\.");
        
        if(versionsA.length > versionsB.length) {
            String[] vb = new String[versionA.length()];
            int i;
            for(i = 0; i < versionA.length(); i++) {
                vb[i] = i < versionsB.length ? versionsB[i] : "0";
            }
            versionsB = vb;
        } else if (versionsA.length < versionsB.length) {
            String[] va = new String[versionB.length()];
            int i;
            for(i = 0; i < versionB.length(); i++) {
                va[i] = i < versionsA.length ? versionsA[i] : "0";
            }
            versionsA = va;
        }
        
        for(int i = 0; i < versionsA.length; i++) {
            int a = Integer.parseInt(versionsA[i]);
            int b = Integer.parseInt(versionsB[i]);
            
            if(a == b) {
                continue;
            }
            
            if(a > b) {
                return 1;
            } else {
                return -1;
            }
            
        }
        return 0; 
    }
    
    
}
