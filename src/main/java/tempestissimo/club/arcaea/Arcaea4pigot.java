package tempestissimo.club.arcaea;

import tempestissimo.club.arcaea.command.BasicCommand;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import tempestissimo.club.arcaea.utils.AffPlayer;
import tempestissimo.club.arcaea.utils.AffReader;
import tempestissimo.club.arcaea.utils.MainRender;
import tempestissimo.club.arcaea.utils.SongScanner;

public final class Arcaea4pigot extends JavaPlugin {
    public static Arcaea4pigot pluginSelf;
    public static FileConfiguration config;
    // 命令处理类
    public static BasicCommand basicCommand;
    // 文件交互
    public static SongScanner songScanner;
    public static AffReader affReader;
    // 渲染逻辑
    public static MainRender mainRender;
    public static AffPlayer affPlayer;


    @Override
    public void onEnable() {
        // Plugin startup logic

        // 配置文件装载
        this.saveDefaultConfig();
        this.config = this.getConfig();
        // 命令组件初始化
        pluginSelf=this;
        this.basicCommand =new BasicCommand(pluginSelf);
        getServer().getPluginCommand("arcaea").setExecutor(new BasicCommand(this));
        // 文件交互读取以及初次运行
        this.songScanner =new SongScanner(config.getString("File.execute_path"));
        this.songScanner.scan_song();
        getLogger().info("歌曲列表读取完毕");
        this.songScanner.scan_pack();
        getLogger().info("曲包列表读取完毕");
        this.affReader =new AffReader();
        // 渲染逻辑
        this.mainRender = new MainRender(this, this.config);
//        this.affPlayer = new AffPlayer()

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
