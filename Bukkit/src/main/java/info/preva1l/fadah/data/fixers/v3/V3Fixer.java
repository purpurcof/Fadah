package info.preva1l.fadah.data.fixers.v3;

import java.util.UUID;

public interface V3Fixer {
    void fixHistory(UUID player);

    boolean needsFixing(UUID player);
}
