package info.preva1l.fadah.filters;

import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.records.listing.BidListing;
import info.preva1l.fadah.records.listing.BinListing;
import info.preva1l.fadah.records.listing.Listing;
import info.preva1l.fadah.utils.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

@Getter
@AllArgsConstructor
public enum SortingMethod {
    AGE(
            Lang.i().getSort().getAge().getName(),
            Comparator.comparingLong(Listing::getCreationDate).reversed()
    ),
    ALPHABETICAL(
            Lang.i().getSort().getName().getName(),
            new AlphabeticalComparator()
    ),
    PRICE(
            Lang.i().getSort().getPrice().getName(),
            Comparator.comparingDouble(Listing::getPrice).reversed()
    ),
    MODE(
            Lang.i().getSort().getMode().getName(),
            Comparator.comparingInt(listing -> {
                if (listing instanceof BinListing) return 0;
                if (listing instanceof BidListing) return 1;
                return 2;
            })
    );

    private final String friendlyName;
    private final Comparator<Listing> normalSorter;

    public Component getFriendlyName() {
        return Text.text(friendlyName);
    }

    public Comparator<Listing> getSorter(@NotNull SortingDirection direction) {
        return switch (direction) {
            case ASCENDING -> normalSorter;
            case DESCENDING -> normalSorter.reversed();
        };
    }

    public Component getLang(@NotNull SortingDirection direction) {
        return switch (this) {
            case AGE -> direction.getAgeName();
            case ALPHABETICAL -> direction.getAlphaName();
            case PRICE -> direction.getPriceName();
            case MODE -> direction.getModeName();
        };
    }

    public SortingMethod next() {
        int currentOrd = this.ordinal();
        if (currentOrd + 1 >= SortingMethod.values().length) return null;
        return SortingMethod.values()[currentOrd + 1];
    }

    public SortingMethod previous() {
        int currentOrd = this.ordinal();
        if (currentOrd - 1 < 0) return null;
        return SortingMethod.values()[currentOrd - 1];
    }

    private static class AlphabeticalComparator implements Comparator<Listing> {
        @Override
        public int compare(Listing o1, Listing o2) {
            String name1 = Text.extractItemNameToString(o1.getItemStack());
            String name2 = Text.extractItemNameToString(o2.getItemStack());
            return name1.compareToIgnoreCase(name2);
        }
    }
}
