package tempestissimo.club.arcaea.utils;

import org.bukkit.Note;
import org.bukkit.configuration.file.FileConfiguration;
import tempestissimo.club.arcaea.Arcaea4pigot;
import tempestissimo.club.arcaea.utils.entities.Song;
import tempestissimo.club.arcaea.utils.entities.hit_related.HitJob;

import java.util.ArrayList;

public class HitCalculator {
    public Arcaea4pigot plugin;
    public FileConfiguration config;

    public ArrayList<HitJob> compileHit(Integer songIndex,Integer ratingClass){
        ArrayList<HitJob> results = new ArrayList<>();
        Song song = plugin.songScanner.song_list.get(songIndex);
        return results;
    }

    public ArrayList<HitJob> compileNote(ArrayList<Note> notes, String timingGroupPrefix){
        ArrayList<HitJob> results = new ArrayList<>();

        return results;
    }

}
