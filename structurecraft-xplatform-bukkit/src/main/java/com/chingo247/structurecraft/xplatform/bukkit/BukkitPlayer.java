
package com.chingo247.structurecraft.xplatform.bukkit;

import com.chingo247.structurecraft.xplatform.core.IPlayer;
import com.chingo247.structurecraft.xplatform.core.IWorld;
import com.chingo247.structurecraft.xplatform.core.item.IInventory;
import com.chingo247.structurecraft.xplatform.core.server.IServer;
import com.google.common.base.Preconditions;
import java.util.Objects;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import com.sk89q.worldedit.Vector;
import org.bukkit.ChatColor;


/**
 *
 * @author Chingo
 */
public class BukkitPlayer  implements IPlayer {
    
    private final Player player;
    private final IServer server;

    public BukkitPlayer(Player player) {
        Preconditions.checkNotNull(player, "Player was null!");
        this.player = player;
        this.server = new BukkitServer(Bukkit.getServer());
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public UUID getUniqueId() {
        return player.getUniqueId();
    }

    @Override
    public IWorld getWorld() {
        return server.getWorld(player.getWorld().getUID());
    }

    public void sendMessage(String... message) {
        player.sendMessage(message);
    }

    @Override
    public float getYaw() {
        return player.getLocation().getYaw();
    }

    @Override
    public int getLevel() {
        return player.getLevel();
    }


    @Override
    public boolean isOP() {
        return player.isOp();
    }

    @Override
    public boolean hasPermission(String permission) {
        return player.hasPermission(permission);
    }

    @Override
    public void updateInventory() {
        player.updateInventory();
    }

   

    @Override
    public boolean isSneaking() {
        return player.isSneaking();
    }

    @Override
    public void closeInventory() {
        player.closeInventory();
    }

    @Override
    public IInventory getInventory() {
        return new BukkitInventory(player.getInventory());
    }

    @Override
    public  void openInventory(IInventory inventory) {
        Preconditions.checkArgument(inventory instanceof BukkitInventory);
        BukkitInventory binv = (BukkitInventory) inventory;
        player.openInventory(binv.getInventory());
    }

    @Override
    public Vector getPosition() {
        Location l = player.getLocation();
        return new Vector(l.getX(), l.getBlockY(), l.getBlockZ());
    }

    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.player.getUniqueId());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BukkitPlayer other = (BukkitPlayer) obj;
        if (!Objects.equals(this.player.getUniqueId(), other.player.getUniqueId())) {
            return false;
        }
        return true;
    }

    @Override
    public void sendMessage(String message) {
        player.sendMessage(message);
    }

    @Override
    public void sendErrorMessage(String message) {
        player.sendMessage(ChatColor.RED + message);
    }

    

   
  

    

    
    
    
    
   
    
    
    
}
