import net.dv8tion.jda.api.events.user.UserTypingEvent;
import net.dv8tion.jda.api.events.user.update.UserUpdateOnlineStatusEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

public class FooListener extends ListenerAdapter {
    @Override
    public void onUserUpdateOnlineStatus(@NotNull UserUpdateOnlineStatusEvent event) {
        super.onUserUpdateOnlineStatus(event);
        System.out.println(event.getNewOnlineStatus().name());
    }

    @Override
    public void onUserTyping(@NotNull UserTypingEvent event) {
        super.onUserTyping(event);
        System.out.println(event.getUser().getName());
    }
}
