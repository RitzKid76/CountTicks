package org.ritzkid76.CountTicks;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.*;
import org.bukkit.command.CommandSender;
import org.ritzkid76.CountTicks.RedstoneTracer.RedstoneTracer;

import java.util.Objects;
import java.util.Random;

public class Debug {
    private static final String debugPrefix =
        ChatColor.GOLD + "[" +
        ChatColor.YELLOW + "DEBUG" +
        ChatColor.GOLD + "] " +
        ChatColor.RESET;
    public static void log(String message) { log(message, ""); }
    public static void log(String message, String name) { log(Objects.requireNonNull(Bukkit.getPlayer("RitzKid76")), message, name); }
    public static void log(CommandSender sender, String message, String name) { sender.sendMessage(debugPrefix + name + ": " + message); }

    public static void spawnDebugParticleRandom(BlockVector3 pos) {
        Random r = new Random();
        spawnDebugParticle(pos, Color.fromRGB(r.nextInt(256), r.nextInt(256), r.nextInt(256)));
    }
    public static void spawnDebugParticle(BlockVector3 pos) { spawnDebugParticle(pos, Color.RED); }
    public static void spawnDebugParticle(BlockVector3 pos, Color color) {
        Particle.DustOptions options = new Particle.DustOptions(color, 10);
        RedstoneTracer.getTracerWorld().spawnParticle(Particle.DUST, new Location(RedstoneTracer.getTracerWorld(), pos.x() + .5, pos.y() + .5, pos.z() + .5), 1, options);
    }
}
