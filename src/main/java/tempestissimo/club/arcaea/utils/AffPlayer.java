package tempestissimo.club.arcaea.utils;

import org.bukkit.World;
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
    public ArrayList<HashMap<String, ArrayList<FillJob>>> workLoad;
    //渲染线程
    public BukkitRunnable affRenderThread;
    //渲染执行器
    public FillExecutor fillExecutor;

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
        affRenderThread=new BukkitRunnable(){
            @Override
            public void run() {
                Long curGameTime=dimension.getTime();
                if (curGameTime>=startPlayingTime){
                    Long tick=curGameTime-startPlayingTime;
                    if (tick>=workLoad.size()){
                        //超界不执行
                        isPlaying=false;
                        this.cancel();
                    }else{
                        //执行操作
//                        HashMap<String, ArrayList<FillJob>> tickJob = workLoad.get(tick.intValue());
                        fillExecutor.executeFill(tick.intValue(),workLoad);

                    }
                }
            }
        };
        affRenderThread.runTaskTimer(plugin,1,0);
        return true;
    }

    public AffPlayer(Arcaea4pigot plugin, FileConfiguration config) {
        this.plugin = plugin;
        this.config = config;
        System.out.println("Available Worlds");
        for (World world:getServer().getWorlds()){
            System.out.println(world.getName());
        }
        this.dimension = getServer().getWorld(config.getString("Render.Position.dimension"));
        this.wait_before_playing = config.getInt("Render.Time.wait_before_playing");
        this.fillExecutor = new FillExecutor(dimension);
    }
}
