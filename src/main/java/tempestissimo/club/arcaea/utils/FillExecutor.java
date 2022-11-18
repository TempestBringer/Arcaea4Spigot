package tempestissimo.club.arcaea.utils;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import tempestissimo.club.arcaea.utils.entities.infer_related.FillJob;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import static org.bukkit.Bukkit.*;

public class FillExecutor {
    public World dimension;

    public void executeFill(Integer tick, ArrayList<FillJob> fills) {

        for (FillJob fill : fills) {
            //三层循环
            for (int i = fill.x_low; i < fill.x_high; i++) {
                for (int j = fill.y_low; j < fill.y_high; j++) {
                    for (int k = fill.z_low; k < fill.z_high; k++) {
                        Block block=dimension.getBlockAt(i,j,k);
                        block.setType(Material.getMaterial(fill.material));
//                        dimension.setBlockData(i, j, k, createBlockData(Material.getMaterial(fill.material)));
                    }
                }
            }

        }
    }


    public FillExecutor(World dimension) {
        this.dimension = dimension;
    }

}
