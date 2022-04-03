import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;

public class App extends ListenerAdapter {
    public static JDA jda;
    public static void main(String[] args) {
        try {
            jda = JDABuilder.createDefault("ODA1NzQ2MjU2OTk5MDg4MTc5.YBfXrQ.JZ7Zel_Fe2yC8mo_lA5J267Zc9w").build();
           // jda.addEventListener(new MemeListener());
            jda.addEventListener(new MusicListener());
            jda.addEventListener(new FooListener());

        } catch (LoginException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void disconnect() {
        Guild guild = jda.getGuildById("831446969234358292");
        guild.getAudioManager().closeAudioConnection();
    }
}