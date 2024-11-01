package org.ritzkid76.CountTicks;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.session.SessionManager;

import io.github.pieter12345.javaloader.bukkit.JavaLoaderBukkitProject;
import io.github.pieter12345.javaloader.bukkit.BukkitCommand;

import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.ritzkid76.CountTicks.RedstoneTracer.*;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraph;
import org.ritzkid76.CountTicks.RedstoneTracer.Graph.RedstoneTracerGraphPath;

import java.util.List;
import java.util.Objects;
import java.util.Random;

public class CountTicksCommand extends JavaLoaderBukkitProject {

    private static final String commandPrefix =
            ChatColor.BLUE + "[" +
            ChatColor.AQUA + "CountTicks" +
            ChatColor.BLUE + "] " +
            ChatColor.RESET;
    private static final String debugPrefix =
            commandPrefix +
            ChatColor.GOLD + "[" +
            ChatColor.YELLOW + "DEBUG" +
            ChatColor.GOLD + "] " +
            ChatColor.RESET;

    public World world;

    @Override
    public void onLoad() {
        // Register an event listener.
//        this.getPlugin().getServer().getPluginManager().registerEvents(new Listener() {
//
//        }, this.getPlugin());

        // Print feedback.
        Bukkit.getConsoleSender().sendMessage(commandPrefix + ChatColor.GREEN + "Loaded.");
    }

    @Override
    public void onUnload() {
        // Unregister all listeners from this project.
        HandlerList.unregisterAll(this.getPlugin());

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

    // DEBUG METHODS
    public static void sendDebugMessage(String message) { sendDebugMessage(message, ""); }
    public static void sendDebugMessage(String message, String name) { sendDebugMessage(Objects.requireNonNull(Bukkit.getPlayer("RitzKid76")), message, name); }
    public static void sendDebugMessage(CommandSender sender, String message, String name) { sender.sendMessage(debugPrefix + name + ": " + message); }

    public static void spawnDebugParticleRandom(BlockVector3 pos) {
        Random r = new Random();
        spawnDebugParticle(pos, Color.fromRGB(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
    }
    public static void spawnDebugParticle(BlockVector3 pos) { spawnDebugParticle(pos, Color.RED); }
    public static void spawnDebugParticle(BlockVector3 pos, Color color) {
        Particle.DustOptions options = new Particle.DustOptions(color, 10);
        RedstoneTracer.getTracerWorld().spawnParticle(Particle.DUST, new Location(RedstoneTracer.getTracerWorld(), pos.x() + .5, pos.y() + .5, pos.z() + .5), 1, options);
    }

    private int getOppositeCoordinate(int min, int max, int pos1Coord) { return (min == pos1Coord)? max : min; }
    public BlockVector3[] getSelection(World world, Player player) throws IncompleteRegionException {
        com.sk89q.worldedit.entity.Player wePlayer = BukkitAdapter.adapt(player);
        com.sk89q.worldedit.world.World weWorld = BukkitAdapter.adapt(world);

        SessionManager sessionManager = WorldEdit.getInstance().getSessionManager();
        LocalSession localSession = sessionManager.get(wePlayer);

        BlockVector3 pos1 = localSession.getRegionSelector(weWorld).getPrimaryPosition();

        // have to extract the second position since i am either mega beans retard, or sk89q is mega sadge ape
        Region selection = localSession.getSelection(weWorld);
        BlockVector3 min = selection.getMinimumPoint();
        BlockVector3 max = selection.getMaximumPoint();

        BlockVector3 pos2 = BlockVector3.at(
                getOppositeCoordinate(min.x(), max.x(), pos1.x()),
                getOppositeCoordinate(min.y(), max.y(), pos1.y()),
                getOppositeCoordinate(min.z(), max.z(), pos1.z())
        );

        return new BlockVector3[] {pos1, pos2};
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

        try {
            BlockVector3[] selection = getSelection(world, player);
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
//                sendDebugMessage(graph.toString(), "graph");
                RedstoneTracerGraphPath path = graph.fastestPath(pos2);
                sendDebugMessage(player, path.gameTickDelay() + "", "delay");
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
