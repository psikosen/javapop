/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.novusradix.JavaPop.Client.Tools;

import com.novusradix.JavaPop.Messaging.UpDown;

/**
 *
 * @author mom
 */
public class RaiseLowerTool extends BaseTool {

    private static RaiseLowerTool t;
    

    public void PrimaryAction(int x, int y) {
        client.sendMessage(new UpDown(x, y, true));
    }


    public void SecondaryAction(int x, int y) {
        client.sendMessage(new UpDown(x, y, false));
    }

}