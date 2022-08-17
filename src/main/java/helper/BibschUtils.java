package helper;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.requests.restaction.CommandCreateAction;

import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class BibschUtils {

    public static boolean isUrlValid(String u) {
        int res = 0;
        boolean check = false;
        try {
            URL url = new URL("https://img-9gag-fun.9cache.com/photo/" + u.substring(21, 28) + "_460svvp9.webm");
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            if (urlConnection.getResponseCode() != 404) {
                check = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return check;
    }

    public static void updateCommands(JDA jda, Long gId) {
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                try {
                    List<List<String>> list = new DatabaseConnector().getCommand();

                    for (int i = 0; i < list.size(); i++) {
                        CommandCreateAction command = jda.getGuildById(gId).upsertCommand(list.get(i).get(1), "Start playback");
                        command.queue();
                    }

                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        };

        new Timer().schedule(timerTask, 0, 10000);
    }

    public static String getTitle(String url, String element) {
        URLConnection connection = null;
        try {
            connection = new URL(url).openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.addRequestProperty("User-Agent", "facebookexternalhit/1.1 (+http://www.facebook.com/externalhit_uatext.php)");

        HTMLEditorKit htmlKit = new HTMLEditorKit();
        HTMLDocument htmlDoc = (HTMLDocument) htmlKit.createDefaultDocument();
        HTMLEditorKit.Parser parser = new ParserDelegator();
        try {
            parser.parse(new InputStreamReader(connection.getInputStream()),
                    htmlDoc.getReader(0), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return htmlDoc.getProperty(element).toString();
    }
}
