package tempestissimo.club.arcaea.utils;

import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import tempestissimo.club.arcaea.Arcaea4pigot;
import tempestissimo.club.arcaea.utils.entities.Song;
import tempestissimo.club.arcaea.utils.entities.infer_related.FillJob;

import java.util.ArrayList;
import java.util.Comparator;
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
    public HashMap<Integer,ArrayList<FillJob>> rawWorkLoad;

    public HashMap<Integer,ArrayList<FillJob>> shuffledWorkLoad;
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
     * 接收到轨道绘制指令
     */
    public void onReceiveInitTrackCommand(){
        ArrayList<FillJob> fillJobs = plugin.mainRender.prepareTrack();
        executeFill(0, fillJobs);
    }

    /**
     * 执行播放
     * @return
     */
    public Boolean onReceivePlayConfirmCommand(){
        startPlayingTime=dimension.getGameTime()+wait_before_playing+plugin.mainRender.minTick;
        isPlaying=true;
        rawWorkLoad=plugin.mainRender.compileResults;
//        shuffledWorkLoad = shuffleWorkLoad(rawWorkLoad);
        shuffledWorkLoad = rawWorkLoad;
        affRenderThread=new BukkitRunnable(){
            @Override
            public void run() {
                try {
                    Integer tick= (int)(dimension.getGameTime()-startPlayingTime);
                    if (tick>plugin.mainRender.maxTick){
                        //超界不执行
                        isPlaying=false;
                        this.cancel();
                        plugin.info.broadcastSuccess("play_finished", new ArrayList<>());
                    }else{
                        //执行操作
//                        HashMap<String, ArrayList<FillJob>> tickJob = workLoad.get(tick.intValue());
                        if (shuffledWorkLoad.containsKey(tick))
                            executeFill(tick.intValue(),shuffledWorkLoad.get(tick));
                        //debug：展示每tick操作
//                        System.out.println("Current Tick Work load "+shuffledWorkLoad.get(tick.intValue()).size());
//                        for (FillJob fill:shuffledWorkLoad.get(tick.intValue())){
//                            if (fill.type.equalsIgnoreCase("arc")) {
//                                System.out.println(fill.toString());
//                            }
//                        }

                    }
                }catch (Exception e){
                    isPlaying=false;
                    this.cancel();
                    plugin.info.broadcastSuccess("error_occurred_while_playing", new ArrayList<>());
                }


            }
        };
        affRenderThread.runTaskTimer(plugin,1,0);
        return true;
    }

    public void executeFill(Integer tick, ArrayList<FillJob> fills) {
        for (FillJob fill : fills) {
            if (fill.type.equals("blackline")){
                dimension.spawnParticle(Particle.END_ROD,fill.x_low,fill.y_low,fill.z_low,1,0,0,0,10000,null,true);
            }else{
                //三层循环
//                System.out.println(fill.toString());
                for (int i = (int)fill.x_low; i <= fill.x_high; i++) {
                    for (int j = (int)fill.y_low; j <= fill.y_high; j++) {
                        for (int k = (int)fill.z_low; k <= fill.z_high; k++) {
                            dimension.getBlockAt(i,j,k).setType(Material.matchMaterial(fill.material));
                        }
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
