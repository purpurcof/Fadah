package info.preva1l.fadah.config;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

public record TimeLength(long amount, ChronoUnit unit) {
    public Duration toDuration() {
        return Duration.of(amount, unit);
    }
}
