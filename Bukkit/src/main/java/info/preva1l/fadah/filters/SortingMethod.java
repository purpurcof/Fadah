package info.preva1l.fadah.filters;

import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.records.listing.Listing;
import info.preva1l.fadah.utils.Text;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

@Getter
@AllArgsConstructor
public enum SortingMethod {
    AGE(
            Lang.i().getSort().getAge().getName(),
            Comparator.comparingLong(Listing::getCreationDate).reversed(),
            Comparator.comparingLong(Listing::getCreationDate)
    ),
    ALPHABETICAL(
            Lang.i().getSort().getName().getName(),
            new AlphabeticalComparator(),
            new AlphabeticalComparator().reversed()
    ),
    PRICE(
            Lang.i().getSort().getPrice().getName(),
            Comparator.comparingDouble(Listing::getPrice).reversed(),
            Comparator.comparingDouble(Listing::getPrice)
    );

    private final String friendlyName;
    private final Comparator<Listing> normalSorter;
    private final Comparator<Listing> reversedSorter;

    public Component getFriendlyName() {
        return Text.modernMessage(friendlyName);
    }

    public Comparator<Listing> getSorter(@NotNull SortingDirection direction) {
        return switch (direction) {
            case ASCENDING -> normalSorter;
            case DESCENDING -> reversedSorter;
        };
    }

    public Component getLang(@NotNull SortingDirection direction) {
        return switch (this) {
            case AGE -> direction.getAgeName();
            case ALPHABETICAL -> direction.getAlphaName();
            case PRICE -> direction.getPriceName();
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
            Component display1 = o1.getItemStack().getItemMeta().displayName();
            String check1 = o1.getItemStack().hasItemMeta()
                    ? (display1 != null ? ((TextComponent) display1).content() : o1.getItemStack().getType().name())
                    : o1.getItemStack().getType().name();
            Component display2 = o2.getItemStack().getItemMeta().displayName();
            String check2 = o2.getItemStack().hasItemMeta()
                    ? (display2 != null ? ((TextComponent) display2).content() : o2.getItemStack().getType().name())
                    : o2.getItemStack().getType().name();
            return check1.compareToIgnoreCase(check2);
        }
    }
}
