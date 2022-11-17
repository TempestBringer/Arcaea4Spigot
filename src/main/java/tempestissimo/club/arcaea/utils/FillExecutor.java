package tempestissimo.club.arcaea.utils;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.data.BlockData;
import tempestissimo.club.arcaea.utils.entities.infer_related.FillJob;

import java.util.ArrayList;
import java.util.HashMap;

import static org.bukkit.Bukkit.*;

public class FillExecutor {
    public World dimension;

    public void executeFill(Integer tick, ArrayList<HashMap<String, ArrayList<FillJob>>> fills){
        HashMap<String, ArrayList<FillJob>> curTickFills = fills.get(tick);
        for (String key: curTickFills.keySet()){
            ArrayList<FillJob> curNote = curTickFills.get(key);
            for (FillJob fill:curNote){
                //三层循环
                for (int i = fill.x_low; i < fill.x_high; i++) {
                    for (int j = fill.y_low; j < fill.y_high; j++) {
                        for (int k = fill.z_low; k < fill.z_high; k++) {
                            dimension.setBlockData(i,j,k,createBlockData(Material.getMaterial(fill.material)));
                        }
                    }
                }
            }
        }
    }

    public FillExecutor(World dimension) {
        this.dimension = dimension;
    }

}
