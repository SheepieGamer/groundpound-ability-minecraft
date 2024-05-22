package me.sheepie.groundpoundability;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;

public final class Groundpound_ability extends JavaPlugin implements CommandExecutor, Listener {

    private final Map<Player, Long> lastGroundPoundTimes = new HashMap<>();
    private final long COOLDOWN = 1500; // Cooldown time in milliseconds (1.5 seconds)

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.getCommand("groundpound").setExecutor(this);
        Bukkit.getPluginManager().registerEvents(this, this);
        getLogger().info("GroundPoundAbility plugin has successfully enabled!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("GroundPoundAbility plugin has successfully disabled!");
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (isPlayerOnGround(player)) {
                if (canUseGroundPound(player)) {
                    executeGroundPound(player);
                    return true;
                } else {
                    player.sendMessage("You must wait before using Groundpound again.");
                    return true;
                }
            } else {
                player.sendMessage("You must be standing on solid ground to use Groundpound.");
                return true;
            }
        }
        return false;
    }

    private boolean isPlayerOnGround(Player player) {
        // Check if the block directly below the player's feet is not air
        return player.getLocation().subtract(0, 1, 0).getBlock().getType() != Material.AIR;
    }

    private boolean canUseGroundPound(Player player) {
        long currentTime = System.currentTimeMillis();
        if (lastGroundPoundTimes.containsKey(player)) {
            long lastTime = lastGroundPoundTimes.get(player);
            if (currentTime - lastTime < COOLDOWN) {
                return false; // Cooldown has not elapsed
            }
        }
        // Update last ground pound time for the player
        lastGroundPoundTimes.put(player, currentTime);
        return true;
    }

    private void executeGroundPound(Player player) {
        // Upwards velocity to reach a height of 2 blocks
        Vector up = new Vector(0, 0.824, 0);
        Vector down = new Vector(0, -1, 0);

        // Launch the player up
        player.setVelocity(up);

        // Schedule the downward motion
        Bukkit.getScheduler().runTaskLater(this, () -> {
            player.setVelocity(down.multiply(2));
        }, 10L); // Shorter delay to start falling sooner
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getInventory().getItemInMainHand().getType() == Material.WARPED_FUNGUS_ON_A_STICK) {
            player.performCommand("groundpound");
        }
    }
}

