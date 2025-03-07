package info.preva1l.fadah.multiserver;

import com.google.gson.annotations.Expose;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

/**
 * Created on 8/03/2025
 *
 * @author Preva1l
 */
@Getter
@AllArgsConstructor
public class WatchNotification {
    @Expose private final UUID player;
    @Expose private final UUID listing;
}
