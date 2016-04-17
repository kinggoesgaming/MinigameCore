package io.github.flibio.minigamecore.arena;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.Serializable;
import java.util.Optional;

public class SerializableLocation implements Serializable {

    private static final long serialVersionUID = 1L;
    private String worldName;
    private double x, y, z;

    protected SerializableLocation(String worldName, double x, double y, double z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static SerializableLocation of(Location<World> loc) {
        return new SerializableLocation(loc.getExtent().getName(), loc.getX(), loc.getY(), loc.getZ());
    }

    public Location<World> createLocation() {
        Optional<World> wOpt = Sponge.getServer().getWorld(worldName);
        if (wOpt.isPresent()) {
            return new Location<World>(wOpt.get(), x, y, z);
        } else {
            World world = Sponge.getServer().getWorld(Sponge.getServer().getDefaultWorldName()).get();
            return new Location<World>(world, x, y, z);
        }
    }
}
