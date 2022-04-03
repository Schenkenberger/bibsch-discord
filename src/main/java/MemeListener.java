import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class MemeListener extends ListenerAdapter {
    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);

        if (event.getMessage().getContentDisplay().equals("!hi")) {
            event.getChannel().sendMessage("https://i.pinimg.com/550x/21/91/e9/2191e9f913c3e84fb7bdf9ab87580e4a.jpg").queue();
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        super.onReady(event);

        JDA jda = event.getJDA();

        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {

                JSONArray jsonArray = getData();

                for (int i = 0; i < jsonArray.length(); i++) {
                    jda.getGuildById("831446969234358292").getTextChannelById("909802873032962118").sendMessage("" + jsonArray.getJSONObject(i).get("url")).queue();
                }
            }
        };

        new Timer().schedule(timerTask, 0, 86400000);
    }

    public JSONArray getData() {

        JSONArray jsonArray = new JSONArray();
        try {
            URLConnection connection = new URL("https://9gag.com/v1/group-posts/group/default/type/hot").openConnection();
            connection.addRequestProperty("User-Agent", "facebookexternalhit/1.1 (+http://www.facebook.com/externalhit_uatext.php)");

            String json = "";
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(connection.getInputStream()));


            while(bufferedReader.ready()) {
                json+=(bufferedReader.readLine());
            }

            JSONObject jsonObject = new JSONObject(json);
            JSONObject data = jsonObject.getJSONObject("data");
            jsonArray = data.getJSONArray("posts");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }
}
