package info.preva1l.fadah.filters;

import info.preva1l.fadah.config.Lang;
import info.preva1l.fadah.utils.Text;
import lombok.AllArgsConstructor;
import net.kyori.adventure.text.Component;

@AllArgsConstructor
public enum SortingDirection {
    ASCENDING(Lang.i().getSort().getName().getAscending(), Lang.i().getSort().getAge().getAscending(), Lang.i().getSort().getPrice().getAscending()),
    DESCENDING(Lang.i().getSort().getName().getDescending(), Lang.i().getSort().getAge().getDescending(), Lang.i().getSort().getPrice().getDescending()),
    ;
    private final String alphaName;
    private final String ageName;
    private final String priceName;

    public Component getAlphaName() {
        return Text.text(alphaName);
    }

    public Component getAgeName() {
        return Text.text(ageName);
    }

    public Component getPriceName() {
        return Text.text(priceName);
    }
}
