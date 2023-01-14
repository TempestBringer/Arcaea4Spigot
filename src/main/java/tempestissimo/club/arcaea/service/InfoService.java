package tempestissimo.club.arcaea.service;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.configuration.Configuration;
import org.bukkit.entity.Player;

import java.util.ArrayList;

import static org.bukkit.Bukkit.getServer;

public class InfoService {
    public Configuration config;

    private ArrayList<TextComponent> pluginHead(){
        ArrayList<TextComponent> results = new ArrayList<>();
        TextComponent head_0=new TextComponent("[Arc");
        head_0.setColor(net.md_5.bungee.api.ChatColor.AQUA);
        TextComponent head_1=new TextComponent("aea]");
        head_1.setColor(net.md_5.bungee.api.ChatColor.LIGHT_PURPLE);
        results.add(head_0);
        results.add(head_1);
        return results;
    }


    public void sendInfo(String path, ArrayList<String> texts, Player player){
        ArrayList<TextComponent> results = pluginHead();
        String raw = readConfigByKey(path);
        for (String text:texts){
            if (path.contains("<!>")){
                raw.replaceFirst("<!>", text);
            }
        }
        TextComponent text = new TextComponent(raw);
        text.setColor(ChatColor.WHITE);
        results.add(text);
        sendTextComponents(results, player);
    }

    public void sendWarn(String path, ArrayList<String> texts, Player player){
        ArrayList<TextComponent> results = pluginHead();
        String raw = readConfigByKey(path);
        raw = stringPlaceHolder(raw, texts);
        TextComponent text = new TextComponent(raw);
        text.setColor(ChatColor.RED);
        results.add(text);
        sendTextComponents(results, player);
    }

    public void sendSuccess(String path, ArrayList<String> texts, Player player){
        ArrayList<TextComponent> results = pluginHead();
        String raw = readConfigByKey(path);
        raw = stringPlaceHolder(raw, texts);
        TextComponent text = new TextComponent(raw);
        text.setColor(ChatColor.GREEN);
        results.add(text);
        sendTextComponents(results, player);
    }

    public void broadcastInfo(String path, ArrayList<String> texts){
        ArrayList<TextComponent> results = pluginHead();
        String raw = readConfigByKey(path);
        raw = stringPlaceHolder(raw, texts);
        TextComponent text = new TextComponent(raw);
        text.setColor(ChatColor.WHITE);
        results.add(text);
        broadcastTextComponents(results);
    }

    public void broadcastWarn(String path, ArrayList<String> texts){
        ArrayList<TextComponent> results = pluginHead();
        String raw = readConfigByKey(path);
        raw = stringPlaceHolder(raw, texts);
        TextComponent text = new TextComponent(raw);
        text.setColor(ChatColor.RED);
        results.add(text);
        broadcastTextComponents(results);
    }

    public void broadcastSuccess(String path, ArrayList<String> texts){
        ArrayList<TextComponent> results = pluginHead();
        String raw = readConfigByKey(path);
        raw = stringPlaceHolder(raw, texts);
        TextComponent text = new TextComponent(raw);
        text.setColor(ChatColor.GREEN);
        results.add(text);
        broadcastTextComponents(results);
    }

    private String stringPlaceHolder(String raw, ArrayList<String> texts){
        for (String text:texts){
            if (raw.contains("<!>")){
                raw.replaceFirst("<!>", text);
            }
        }
        return raw;
    }

    private String readConfigByKey(String key){
       return config.getString("Info.".concat(key));
    }

    private void broadcastTextComponents(ArrayList<TextComponent> texts){
        for (Player player:getServer().getOnlinePlayers()){
            player.spigot().sendMessage(texts.toArray(new TextComponent[texts.size()]));
        }
    }

    private void sendTextComponents(ArrayList<TextComponent> texts, Player player){
        player.spigot().sendMessage(texts.toArray(new TextComponent[texts.size()]));
    }

    public InfoService(Configuration config) {
        this.config = config;
    }
}
