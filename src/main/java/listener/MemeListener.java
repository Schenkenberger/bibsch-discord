package listener;

import helper.BibschUtils;
import helper.DatabaseConnector;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class MemeListener extends ListenerAdapter {

    DatabaseConnector databaseConnector = new DatabaseConnector();

    @Override
    public void onGuildMessageReceived(@NotNull GuildMessageReceivedEvent event) {
        super.onGuildMessageReceived(event);

        if (event.getMessage().getContentDisplay().equals("!hi")) {
            event.getChannel().sendMessage("https://i.pinimg.com/550x/21/91/e9/2191e9f913c3e84fb7bdf9ab87580e4a.jpg").queue();
        }

        if (event.getMessage().getContentDisplay().contains("https://9gag.com/gag/") && !event.getAuthor().isBot() && BibschUtils.isUrlValid(event.getMessage().getContentDisplay().substring(event.getMessage().getContentDisplay().indexOf("https://")).split(" ")[0])) {
            String uri = event.getMessage().getContentDisplay().substring(event.getMessage().getContentDisplay().indexOf("https://")).split(" ")[0].substring(0, 28);
            String webm = "https://img-9gag-fun.9cache.com/photo/" + event.getMessage().getContentDisplay().substring(event.getMessage().getContentDisplay().indexOf("https://")).split(" ")[0].substring(21, 28) + "_460svvp9.webm";
            MessageBuilder messageBuilder = new MessageBuilder();
            messageBuilder.append("Source: <" + uri + ">\n\n");
            messageBuilder.append(webm);
            messageBuilder.append("\n");
            messageBuilder.append("**" + BibschUtils.getTitle(uri, "title") + "**");
            messageBuilder.append("\n");
            messageBuilder.append("\n");
            if (event.getMember().getNickname() != null) {
                messageBuilder.append("**" + "Posted by " + event.getMember().getNickname() + "**");
            } else {
                messageBuilder.append("**" + "Posted by " + event.getAuthor().getName() + "**");
            }
            event.getChannel().sendMessage(messageBuilder.build()).queue();
            event.getChannel().deleteMessageById(event.getChannel().getLatestMessageId()).queue();

        }

    }

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        super.onPrivateMessageReceived(event);
        if (event.getAuthor().getId().equals("159632112612278272") || event.getAuthor().getId().equals("1006173484520575076")) {
            if (event.getMessage().getContentDisplay().split(" ")[0].equals("!add")) {

                try {
                    new DatabaseConnector().insertCommand(event.getMessage().getContentDisplay().split(" ")[1], event.getMessage().getContentDisplay().split(" ")[2]);
                } catch (SQLException e) {
                    event.getMessage().reply(e.getMessage()).queue();
                }
            }
            if (event.getMessage().getContentDisplay().split(" ")[0].equals("!rem")) {
                try {
                    databaseConnector.remCommand(Integer.parseInt(event.getMessage().getContentDisplay().split(" ")[1]));
                } catch (SQLException e) {
                    e.printStackTrace();
                }

            }
            if (event.getMessage().getContentDisplay().equals("!commands")) {
                MessageBuilder messageBuilder = new MessageBuilder();

                try {
                    for (List<String> s : databaseConnector.getCommand()
                    ) {
                        messageBuilder.append("ID: " + s.get(0) + " Command: " + s.get(1) + " " + "<" + s.get(2) + ">" + "\n");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                event.getMessage().reply(messageBuilder.build()).queue();
            }
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
                    jda.getGuildById("1026450198496555098").getTextChannelById("1006173484520575076").sendMessage("" + jsonArray.getJSONObject(i).get("url")).queue();
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


            while (bufferedReader.ready()) {
                json += (bufferedReader.readLine());
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
