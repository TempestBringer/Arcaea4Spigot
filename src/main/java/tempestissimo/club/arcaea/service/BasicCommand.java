package tempestissimo.club.arcaea.service;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.apache.commons.lang.StringUtils;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import tempestissimo.club.arcaea.Arcaea4pigot;
import tempestissimo.club.arcaea.utils.entities.Difficulty;
import tempestissimo.club.arcaea.utils.entities.Song;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BasicCommand implements CommandExecutor, TabCompleter {

    public Arcaea4pigot plugin;
    //&4=大红 、&c=浅红、 &6=土黄、 &e=金黄、 &2=绿、 &a=浅绿 、&b=蓝绿、&3=天蓝
    //&1=深蓝、 &9=蓝紫、 &d=粉红、 &5=品红、 &f=白、 &7=灰、 &8=深灰、 &0=黑
    public String function_prefix= ChatColor.AQUA+"[Arc"+ChatColor.DARK_PURPLE+"aea]";

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase("arcaea")){
            if (!(sender instanceof Player)){
                sender.sendMessage(function_prefix+ChatColor.RED+" §c必须以玩家身份使用该命令");
                return true;
            }
            Player player = ((Player)sender);
            if (args.length==0){
                player.sendMessage(function_prefix+ChatColor.GOLD+" Arcaea in Minecraft");
                player.sendMessage(function_prefix+ChatColor.GOLD+" Auther : TempestZYTux");
                player.sendMessage(function_prefix+ChatColor.GOLD+" 使用/arcaea help获得更多信息");
                return true;
            }else {
                if (args[0].equalsIgnoreCase("help")){
                    player.sendMessage(function_prefix+ChatColor.GOLD+" 帮助菜单");
                    player.sendMessage(function_prefix+ChatColor.WHITE+" songlist             "+ChatColor.GOLD+"   歌曲列表");
                    player.sendMessage(function_prefix+ChatColor.WHITE+" select [歌曲id] [难度]"+ChatColor.GOLD+"   查看歌曲信息");
                    player.sendMessage(function_prefix+ChatColor.WHITE+" compile [歌曲id] [难度]"+ChatColor.GOLD+"   编译歌曲");
                    return true;
                }
                //歌曲菜单
                if (args[0].equalsIgnoreCase("songlist")){
                    // 缺省了页号
                    if (args.length==1){
                        Integer start=0;
                        Integer number=plugin.config.getInt("Display.song_number_per_page");
                        ArrayList<Song> result=plugin.songScanner.select_from_song_list(start,number);
                        player.sendMessage(function_prefix+"§b 歌曲菜单 (0/"+plugin.songScanner.song_list.size()/number+")");
                        song_list_to_text(player,result);
                        TextComponent midPage=new TextComponent("        ");
                        TextComponent nexPage=new TextComponent("下一页>>>");
                        nexPage.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
                        nexPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/arcaea songlist 1"));
                        player.spigot().sendMessage(midPage,midPage,nexPage);
                        return true;
                    }

                    // 补全了页号
                    if (args.length>=2){
                        if(!StringUtils.isNumeric(args[1])){
                            plugin.info.sendWarn("argument_type_error", new ArrayList<>(),player);
                            return true;
                        }
                        Integer page=Integer.parseInt(args[1]);
                        if (page<0)
                            page=0;
                        Integer start=page*plugin.config.getInt("Display.song_number_per_page");
                        Integer number=plugin.config.getInt("Display.song_number_per_page");
                        ArrayList<Song> result=plugin.songScanner.select_from_song_list(start,number);
                        player.sendMessage(function_prefix+"§b 歌曲菜单 ("+String.valueOf(page)+"/"+(plugin.songScanner.song_list.size()/number)+")");
                        song_list_to_text(player,result);
                    }
                    // 快捷翻页
                    Integer page=0;
                    if (args.length>=2)
                        page=Integer.parseInt(args[1]);
                    Integer upLimit=(plugin.songScanner.song_list.size()/plugin.config.getInt("Display.song_number_per_page"));
                    TextComponent[] diff_comps=new TextComponent[3];
                    TextComponent prePage=new TextComponent("<<<上一页");
                    TextComponent midPage=new TextComponent("        ");
                    TextComponent nexPage=new TextComponent("下一页>>>");
                    prePage.setColor(net.md_5.bungee.api.ChatColor.AQUA);
                    nexPage.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
                    prePage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/arcaea songlist "+(page-1)));
                    nexPage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/arcaea songlist "+(page+1)));
                    if (page<0){
                        diff_comps[0]=midPage;
                        diff_comps[1]=midPage;
                        diff_comps[2]=midPage;
                    }else if (page==0){
                        diff_comps[0]=midPage;
                        diff_comps[1]=midPage;
                        diff_comps[2]=nexPage;
                    }else if (page==upLimit){
                        diff_comps[0]=prePage;
                        diff_comps[1]=midPage;
                        diff_comps[2]=midPage;
                    }else if (page>upLimit){
                        diff_comps[0]=midPage;
                        diff_comps[1]=midPage;
                        diff_comps[2]=midPage;
                    }else{
                        diff_comps[0]=prePage;
                        diff_comps[1]=midPage;
                        diff_comps[2]=nexPage;
                    }
                    player.spigot().sendMessage(diff_comps);
                    return true;
                }else if (args[0].equalsIgnoreCase("select")){//选择某个歌曲的某个难度 /arcaea select [歌曲id] [难度]
                    //过滤参数不足以及参数错误
                    if (args.length==1 || args.length==2){
                        plugin.info.sendWarn("argument_num_error", new ArrayList<>(),player);
                        return true;
                    }else if(!StringUtils.isNumeric(args[1])||!StringUtils.isNumeric(args[2])){
                        plugin.info.sendWarn("argument_type_error", new ArrayList<>(),player);
                        return true;
                    }
                    //开始解析
                    Integer songIndex= Integer.valueOf(args[1]);
                    Integer ratingClass= Integer.valueOf(args[2]);
                    //检查文件完整性
                    Boolean exist= plugin.songScanner.check_file_if_exist(songIndex,ratingClass);
                    if (!exist){
                        plugin.info.sendWarn("aff_file_not_exist", new ArrayList<>(),player);
                        return true;
                    }
                    song_info_to_text(player,songIndex,ratingClass);
                    return true;
                }else if (args[0].equalsIgnoreCase("compile")){//选择某个歌曲的某个难度进行编译 /arcaea compile [歌曲id] [难度]
                    //过滤参数不足以及参数错误
                    if (args.length==1 || args.length==2 || args.length>3){
                        plugin.info.sendWarn("argument_num_error", new ArrayList<>(),player);
                        return true;
                    }else if(!StringUtils.isNumeric(args[1])||!StringUtils.isNumeric(args[2])){
                        plugin.info.sendWarn("argument_type_error", new ArrayList<>(),player);
                        return true;
                    }
                    //开始解析
                    Integer songIndex= Integer.valueOf(args[1]);
                    Integer ratingClass= Integer.valueOf(args[2]);
                    //检查文件完整性
                    Boolean exist= plugin.songScanner.check_file_if_exist(songIndex,ratingClass);
                    if (!exist){
                        plugin.info.sendWarn("aff_file_not_exist", new ArrayList<>(),player);
                        return true;
                    }
                    Boolean compilable = plugin.affPlayer.onReceiveCompileCommand(songIndex, ratingClass);
                    if (compilable){
                        // 正在编译
                        plugin.info.broadcastSuccess("compiling_aff", new ArrayList<>());
                        return true;
                    }else{
                        plugin.info.sendWarn("unable_to_compile", new ArrayList<>(),player);
                        return true;
                    }
                }else if (args[0].equalsIgnoreCase("play")){
                    if (args.length==1){ //展示编译结果以及打歌信息/arcaea play
                        if (plugin.affPlayer.onReceivePlayCommand()){
                            plugin.info.sendSuccess("before_play_warning", new ArrayList<>(),player);
                        }else{
                            plugin.info.sendWarn("no_compile_or_not_finished", new ArrayList<>(),player);
                        }
                        return true;
                    }else if(args.length==2){ // arcaea play confirm
                        if (args[1].equalsIgnoreCase("confirm")){
                            //确认播放前的可用性检查
                            if (plugin.affPlayer.onReceivePlayCommand()){
                                //执行播放
                                Boolean playConfirm = plugin.affPlayer.onReceivePlayConfirmCommand();
                                //反馈启动结果
                                if (playConfirm){
                                    plugin.info.sendSuccess("start_play", new ArrayList<>(),player);
                                }else{
                                    plugin.info.sendWarn("error_occurred_while_starting_play", new ArrayList<>(),player);
                                }

                            }else{
                                plugin.info.sendWarn("no_compile_or_not_finished", new ArrayList<>(),player);
                            }
                        }else{
                            //其他错误输入
                            plugin.info.sendWarn("argument_value_error", new ArrayList<>(),player);
                        }
                        return true;
                    }
                }else if (args[0].equalsIgnoreCase("InitTrack")){
                    if (args.length!=1){ //展示编译结果以及打歌信息/arcaea play
                        plugin.info.sendWarn("argument_num_error", new ArrayList<>(),player);
                        return true;
                    }else { // /arcaea InitTrack
                        plugin.affPlayer.onReceiveInitTrackCommand();
                        return true;
                    }
                }
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        List<String> list = new ArrayList<>();
        list.add("songlist");
        list.add("help");
        list.add("select");
        list.add("play");
        list.add("InitTrack");
        return list;
    }

    /**
     * 将Song和难度描述转化为前台文本
     * @param player 玩家
     * @param songIndex 歌曲id
     * @param ratingClass 难度id
     */
    public void song_info_to_text(Player player,Integer songIndex,Integer ratingClass){
        Song song=plugin.songScanner.song_list.get(songIndex);
        String localized_text=localize_text(song.title_localized);
        String artist=song.artist;
        String bpm=song.bpm;
        String song_pack=song.set;
        String update_version=song.version;
        Difficulty diff=song.difficulties.get(ratingClass);
        String ratingString= String.valueOf(diff.rating);
        if (diff.ratingPlus)
            ratingString=ratingString+"+";
        //标题
        TextComponent title = new TextComponent("  "+localized_text+" "+ratingString);
        //播放按钮
        TextComponent space = new TextComponent("      ");
        TextComponent playComp = new TextComponent(">>PLAY<<");
        playComp.setBold(true);
//        playComp.setColor(net.md_5.bungee.api.ChatColor.BOLD);
        playComp.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/arcaea compile "+songIndex+" "+ratingClass));

        //标题和播放按钮换色
        if (diff.ratingClass==0){
            title.setColor(net.md_5.bungee.api.ChatColor.AQUA);
            playComp.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        }
        else if (diff.ratingClass==1){
            title.setColor(net.md_5.bungee.api.ChatColor.GREEN);
            playComp.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        }
        else if (diff.ratingClass==2) {
            title.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
            playComp.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
        }
        else if (diff.ratingClass==3) {
            title.setColor(net.md_5.bungee.api.ChatColor.DARK_RED);
            playComp.setColor(net.md_5.bungee.api.ChatColor.DARK_RED);
        }
        else {
            title.setColor(net.md_5.bungee.api.ChatColor.AQUA);
            playComp.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        }

        TextComponent head_0=new TextComponent("[Arc");
        head_0.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        TextComponent head_1=new TextComponent("aea]");
        head_1.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);

        TextComponent artistComp = new TextComponent(artist);
        TextComponent artistTitle = new TextComponent("作曲：");
        artistTitle.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
        TextComponent bpmComp = new TextComponent(bpm);
        TextComponent bpmTitle = new TextComponent("bpm：");
        bpmTitle.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
        TextComponent packComp = new TextComponent(song_pack);
        TextComponent packTitle = new TextComponent("曲包：");
        packTitle.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
        TextComponent versionComp = new TextComponent(update_version);
        TextComponent versionTitle = new TextComponent("更新版本：");
        versionTitle.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
        TextComponent chartDesignerComp = new TextComponent(diff.chartDesigner);
        TextComponent chartDesignerTitle = new TextComponent("谱面设计：");
        chartDesignerTitle.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
        TextComponent jacketDesignerComp = new TextComponent(diff.jacketDesigner);
        TextComponent jacketDesignerTitle = new TextComponent("插画设计：");
        jacketDesignerTitle.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);

        TextComponent aff_availableComp;
        if (diff.aff_available){
            aff_availableComp=new TextComponent("可用");
            aff_availableComp.setColor(net.md_5.bungee.api.ChatColor.GREEN);
        }
        else{
            aff_availableComp=new TextComponent("不可用");
            aff_availableComp.setColor(net.md_5.bungee.api.ChatColor.DARK_RED);
        }
        TextComponent aff_availableTitle = new TextComponent("谱面文件可用性：");
        aff_availableTitle.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);

        player.spigot().sendMessage(head_0,head_1,title);
        player.spigot().sendMessage(artistTitle,artistComp);
        player.spigot().sendMessage(bpmTitle,bpmComp);
        player.spigot().sendMessage(packTitle,packComp);
        player.spigot().sendMessage(versionTitle,versionComp);
        player.spigot().sendMessage(chartDesignerTitle,chartDesignerComp);
        player.spigot().sendMessage(jacketDesignerTitle,jacketDesignerComp);
        player.spigot().sendMessage(aff_availableTitle,aff_availableComp);
        player.spigot().sendMessage(space,playComp,space);

    }


    /**
     * 将含有Song的ArrayList转化为前台文本
     * @param songs
     */
    public void song_list_to_text(Player player, ArrayList<Song> songs){
        //对每首歌
        for (int i = 0; i < songs.size(); i++) {
            Song song=songs.get(i);
            TextComponent[] diff_comps=new TextComponent[song.difficulties.size()+1];
            Integer diff_comps_pointer=0;

            //对每个难度
            for (int j=0;j<song.difficulties.size();j++){
                Difficulty diff = song.difficulties.get(j);
                String diff_rate= String.valueOf(diff.rating);
                if (diff.ratingPlus)
                    diff_rate=diff_rate+"+";
                TextComponent diffCom = new TextComponent("["+diff_rate+"]");
                if (diff.ratingClass==0)
                    diffCom.setColor(net.md_5.bungee.api.ChatColor.AQUA);
                else if(diff.ratingClass==1)
                    diffCom.setColor(net.md_5.bungee.api.ChatColor.GREEN);
                else if (diff.ratingClass==2)
                    diffCom.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
                else if (diff.ratingClass==3)
                    diffCom.setColor(net.md_5.bungee.api.ChatColor.DARK_RED);
                if (!diff.aff_available) {
                    diffCom.setStrikethrough(true);
                    diffCom.setColor(net.md_5.bungee.api.ChatColor.DARK_GRAY);
                }else{
                    diffCom.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,"/arcaea select "+ song.idx +" "+ j ));
                }
                diff_comps[diff_comps_pointer]=diffCom;
                diff_comps_pointer++;
            }
            TextComponent song_comp = new TextComponent(localize_text(song.getTitle_localized()));
            diff_comps[diff_comps_pointer]=song_comp;
            player.spigot().sendMessage(diff_comps);
        }
    }

    /**
     * 从本地化译名中提取出可用的、首选的译名
     * @param song_name 包含本地化译名的HashMap
     * @return 解析的字符串
     */
    public String localize_text(HashMap<String,String> song_name){
        String default_key = String.valueOf(plugin.config.get("Display.language").toString());
        //读取配置
        if (song_name.keySet().contains(default_key)){
            return song_name.get(default_key);
        }
        //按照顺序
        else if (song_name.keySet().contains("en")){
            return song_name.get("en");
        }else if (song_name.keySet().contains("zh-Hans")){
            return song_name.get("zh-Hans");
        }else if (song_name.keySet().contains("zh-Hant")){
            return song_name.get("zh-Hant");
        }else if (song_name.keySet().contains("ja")){
            return song_name.get("ja");
        }else if (song_name.keySet().contains("ko")){
            return song_name.get("ko");
        }
        return "null";
    }

    public BasicCommand(Arcaea4pigot plugin) {
        this.plugin=plugin;
    }
}