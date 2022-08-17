import helper.BibschUtils;
import listener.MemeListener;
import listener.MusicListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.JDAInfo;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;
import net.dv8tion.jda.api.utils.MarkdownSanitizer;
import net.dv8tion.jda.api.utils.MiscUtil;
import net.dv8tion.jda.api.utils.TimeUtil;

import javax.security.auth.login.LoginException;
import java.util.concurrent.TimeUnit;

public class App extends ListenerAdapter {

    private JDA jda;
    private long gId = 1006172676190130307l;

    public App() throws LoginException, InterruptedException {

        jda = JDABuilder.createDefault(BibschUtils.prop("tokens"))
                .build();
        jda.addEventListener(new MusicListener());
        jda.addEventListener(new MemeListener());

        jda.awaitReady();



        CommandCreateAction skip = jda.getGuildById(gId).upsertCommand("skip", "Skip a playback");
        skip.timeout(2l, TimeUnit.SECONDS);
        skip.queue();


        CommandCreateAction play = jda.getGuildById(gId).upsertCommand("play", "/play <URL>").addOption(OptionType.STRING, "url", "Start playback");
        play.timeout(2l, TimeUnit.SECONDS);
        play.queue();

        BibschUtils.updateCommands(jda, gId);


    }

    public static void main(String[] args) throws LoginException, InterruptedException {
        new App();

    }
}