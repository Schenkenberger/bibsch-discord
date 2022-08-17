package listener;

import helper.DatabaseConnector;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import lavaplayer.GuildMusicManager;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.managers.AudioManager;
import org.jetbrains.annotations.NotNull;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MusicListener extends ListenerAdapter {

    private final AudioPlayerManager playerManager;
    private final Map<Long, GuildMusicManager> musicManagers;
    private SlashCommandEvent event;

    public MusicListener() {
        this.musicManagers = new HashMap<>();

        this.playerManager = new DefaultAudioPlayerManager();
        AudioSourceManagers.registerRemoteSources(playerManager);
        AudioSourceManagers.registerLocalSource(playerManager);
    }

    private synchronized GuildMusicManager getGuildAudioPlayer(Guild guild) {
        long guildId = Long.parseLong(guild.getId());
        GuildMusicManager musicManager = musicManagers.get(guildId);

        if (musicManager == null) {
            musicManager = new GuildMusicManager(playerManager, guild);
            musicManagers.put(guildId, musicManager);
        }

        guild.getAudioManager().setSendingHandler(musicManager.getSendHandler());

        return musicManager;
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {

        this.event = event;
        super.onSlashCommand(event);
        try {
            List<List<String>> lc = new DatabaseConnector().getCommand();
            for (int i = 0; i < lc.size(); i++) {
                if (event.getName().equals(lc.get(i).get(1))) {
                    loadAndPlay(event.getTextChannel(), lc.get(i).get(2));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (event.getName().equals("skip")) {
            skipTrack(event.getTextChannel());
        } else if(event.getName().equals("play")) {
            loadAndPlay(event.getTextChannel(), event.getOption("url").getAsString());
        }

    }

    private void loadAndPlay(final TextChannel channel, final String trackUrl) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());

        playerManager.loadItemOrdered(musicManager, trackUrl, new AudioLoadResultHandler() {
            @Override
            public void trackLoaded(AudioTrack track) {

                event.reply("Warteschleife hinzugefügt " + track.getInfo().title).setEphemeral(true).queue();

                play(channel.getGuild(), musicManager, track);
            }

            @Override
            public void playlistLoaded(AudioPlaylist playlist) {
                AudioTrack firstTrack = playlist.getSelectedTrack();

                if (firstTrack == null) {
                    firstTrack = playlist.getTracks().get(0);
                }

                event.reply("Warteschleife hinzugefügt " + firstTrack.getInfo().title + " (first track of playlist " + playlist.getName() + ")").setEphemeral(true).queue();

                play(channel.getGuild(), musicManager, firstTrack);
            }

            @Override
            public void noMatches() {
                channel.sendMessage("Nichts gefunden " + trackUrl).queue();
            }

            @Override
            public void loadFailed(FriendlyException exception) {
                event.reply("Fehler beim abspielen: " + exception.getMessage()).setEphemeral(true).queue();
            }
        });
    }

    private void play(Guild guild, GuildMusicManager musicManager, AudioTrack track) {
        connectToFirstVoiceChannel(guild.getAudioManager());
        musicManager.scheduler.queue(track);
    }

    private void skipTrack(TextChannel channel) {
        GuildMusicManager musicManager = getGuildAudioPlayer(channel.getGuild());
        musicManager.scheduler.nextTrack();
        event.reply("Skipped to next track.").setEphemeral(true).queue();
    }

    private void connectToFirstVoiceChannel(AudioManager audioManager) {
        if (!audioManager.isConnected() && !audioManager.isAttemptingToConnect()) {
            for (VoiceChannel voiceChannel : audioManager.getGuild().getVoiceChannels()) {
                if (voiceChannel.getMembers().toString().contains(event.getUser().getName())) {
                    audioManager.openAudioConnection(voiceChannel);
                }

            }
        }
    }
}
