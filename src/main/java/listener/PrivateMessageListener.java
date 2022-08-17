package listener;

import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class PrivateMessageListener extends ListenerAdapter {

    @Override
    public void onPrivateMessageReceived(@NotNull PrivateMessageReceivedEvent event) {
        super.onPrivateMessageReceived(event);

    }
}
