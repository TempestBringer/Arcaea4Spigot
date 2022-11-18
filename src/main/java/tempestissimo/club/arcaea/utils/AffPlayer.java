package tempestissimo.club.arcaea.utils;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import tempestissimo.club.arcaea.Arcaea4pigot;
import tempestissimo.club.arcaea.utils.entities.Song;
import tempestissimo.club.arcaea.utils.entities.infer_related.FillJob;

import java.util.ArrayList;
import java.util.HashMap;

import static org.bukkit.Bukkit.getServer;

public class AffPlayer {
    //参数准备
    public Arcaea4pigot plugin;
    public FileConfiguration config;
    public World dimension;
    public Integer wait_before_playing;
    //运行时参数
    public Boolean isPlaying = false;
    public Long startPlayingTime;
    public Song curSong;
    public ArrayList<FillJob> rawWorkLoad;

    public HashMap<Integer,ArrayList<FillJob>> shuffledWorkLoad;
    public Integer maxTick=0;//终止条件
    //渲染线程
    public BukkitRunnable affRenderThread;

    /**
     * 执行编译
     * @param songIndex
     * @param ratingClass
     * @return
     */
    public Boolean onReceiveCompileCommand(Integer songIndex,Integer ratingClass){
        Boolean compilable=plugin.mainRender.compileEntry(songIndex,ratingClass);
        return compilable;
    }

    /**
     * 检测是否可以播放
     * @return
     */
    public Boolean onReceivePlayCommand(){
        Boolean play=plugin.mainRender.playEntry();
        return play;
    }

    /**
     * 执行播放
     * @return
     */
    public Boolean onReceivePlayConfirmCommand(){
        startPlayingTime=dimension.getTime()+wait_before_playing;
        isPlaying=true;
        rawWorkLoad=plugin.mainRender.compileResults;
        shuffledWorkLoad = shuffleWorkLoad(rawWorkLoad);
        affRenderThread=new BukkitRunnable(){
            @Override
            public void run() {
                Long curGameTime=dimension.getTime();
                if (curGameTime>=startPlayingTime){
                    Long tick=curGameTime-startPlayingTime;

                    if (tick>=maxTick){
                        //超界不执行
                        isPlaying=false;
                        this.cancel();
                    }else{
                        //执行操作
//                        HashMap<String, ArrayList<FillJob>> tickJob = workLoad.get(tick.intValue());
                        executeFill(tick.intValue(),shuffledWorkLoad.get(tick.intValue()));

                        System.out.println("Current Tick Work load"+shuffledWorkLoad.get(tick.intValue()).size());
                        //debug：展示每tick操作
                        for (FillJob fill:shuffledWorkLoad.get(tick.intValue())){
                            System.out.println(fill.toString());
                        }

                    }

                }
            }
        };
        affRenderThread.runTaskTimer(plugin,1,0);
        return true;
    }

    public HashMap<Integer,ArrayList<FillJob>> shuffleWorkLoad(ArrayList<FillJob> input){
        HashMap<Integer,ArrayList<FillJob>> results = new HashMap<>();
        for (FillJob fillJob:input){
            ArrayList<FillJob> fillJobsInTick;
            if (results.containsKey(fillJob.frame)){
                fillJobsInTick = results.get(fillJob.frame);
            }else{
                fillJobsInTick = new ArrayList<>();
            }
            fillJobsInTick.add(fillJob);
            results.put(fillJob.frame,fillJobsInTick);

            if (fillJob.frame>maxTick)
                maxTick=fillJob.frame;
        }
        Integer i=0;
        for (;i<maxTick;i++){
            if (!results.containsKey(i)){
                results.put(i,new ArrayList<>());
            }
        }
        return results;
    }

    public void executeFill(Integer tick, ArrayList<FillJob> fills) {
        for (FillJob fill : fills) {
            //三层循环
            for (int i = fill.x_low; i < fill.x_high; i++) {
                for (int j = fill.y_low; j < fill.y_high; j++) {
                    for (int k = fill.z_low; k < fill.z_high; k++) {
                        dimension.getBlockAt(i,j,k).setType(Material.matchMaterial(fill.material));
                    }
                }
            }

        }
    }

    public AffPlayer(Arcaea4pigot plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        this.dimension = getServer().getWorld(config.getString("Render.Position.dimension"));
        this.wait_before_playing = config.getInt("Render.Time.wait_before_playing");
    }
}
