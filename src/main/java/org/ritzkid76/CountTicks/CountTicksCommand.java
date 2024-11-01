package org.ritzkid76.CountTicks;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.ritzkid76.CountTicks.RedstoneTracer.RedstoneTracer;
import org.ritzkid76.CountTicks.RedstoneTracer.RedstoneTracerResult;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraph;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraphPath;

import com.sk89q.worldedit.math.BlockVector3;

import io.github.pieter12345.javaloader.bukkit.BukkitCommand;
import io.github.pieter12345.javaloader.bukkit.JavaLoaderBukkitProject;

public class CountTicksCommand extends JavaLoaderBukkitProject {

    private static final String commandPrefix =
            ChatColor.BLUE + "[" +
            ChatColor.AQUA + "CountTicks" +
            ChatColor.BLUE + "] " +
            ChatColor.RESET;

    public World world;

    @Override
    public void onLoad() {
        // Register an event listener.
    //    this.getPlugin().getServer().getPluginManager().registerEvents(new Listener() {

    //    }, this.getPlugin());

        // Print feedback.
        Bukkit.getConsoleSender().sendMessage(commandPrefix + ChatColor.GREEN + "Loaded.");
    }

    @Override
    public void onUnload() {
        // Unregister all listeners from this project.
        // HandlerList.unregisterAll(this.getPlugin());

        // Print feedback.
        Bukkit.getConsoleSender().sendMessage(commandPrefix + ChatColor.RED + "Unloaded.");
    }

    @Override
    public String getVersion() {
        return "0.0.1-SNAPSHOT";
    }

	  public static void sendChatMessage(CommandSender sender, String message) {
		sender.sendMessage(commandPrefix + message);
	}

    @Override
    public boolean onCommand(final CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sendChatMessage(sender, "This command can only be used by players!");
            return true;
        }

        world = player.getWorld();
        BlockVector3 pos1;
        BlockVector3 pos2;

        WorldEditSelection worldEditSelection = new WorldEditSelection(world, player);

        try {
            BlockVector3[] selection = worldEditSelection.getSelection();
            pos1 = selection[0];
            pos2 = selection[1];
        } catch (Exception e) {
            sendChatMessage(player, "Please select a start point (pos1) and end point (pos2).");
            return true;
        }

        RedstoneTracerResult tracerReturn = new RedstoneTracer(world, pos1).getPath(pos2);


        switch(tracerReturn.type()) {
            case PATH_FOUND -> {
                sendChatMessage(player, "Path found.");
                RedstoneTracerGraph graph = tracerReturn.graph();
                RedstoneTracerGraphPath path = graph.fastestPath(pos2);
                Debug.log(player, path.gameTickDelay() + "", "delay");
            }
            case NO_PATH -> sendChatMessage(player, "No path found.");
            case INVALID_SELECTION -> sendChatMessage(player, "Be sure that the start and end positions are valid redstone components.");
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return null;
    }

    @Override
    public BukkitCommand[] getCommands() {
        return new BukkitCommand[]{
                new BukkitCommand("countticks")
                        .setUsageMessage("TODO")
                        .setPermission("javaloader.countticks.counttickscommand")
                        .setPermissionMessage("You do not have permission to use this command.")
                        .setAliases("count", "ticks", "ct")
                        .setExecutor(this)
                        .setTabCompleter(this)
        };
    }
}
