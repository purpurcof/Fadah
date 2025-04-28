package info.preva1l.fadah.data.fixers.v2;

import java.util.UUID;

public interface V2Fixer {
    static Empty empty() { return new Empty(); }

    void fixExpiredItems(UUID player);
    void fixCollectionBox(UUID player);
    boolean needsFixing(UUID player);

    class Empty implements V2Fixer {
        @Override
        public void fixExpiredItems(UUID player) {
            // do nothing
        }

        @Override
        public void fixCollectionBox(UUID player) {
            // do nothing
        }

        @Override
        public boolean needsFixing(UUID player) {
            return false;
        }
    }
}
