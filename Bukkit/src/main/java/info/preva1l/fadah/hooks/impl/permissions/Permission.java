package info.preva1l.fadah.hooks.impl.permissions;

import info.preva1l.fadah.config.Config;

public enum Permission {
    MAX_LISTINGS("fadah.max-listings.", Config.i().getDefaultMaxListings(), true),
    LISTING_TAX("fadah.listing-tax.", 0.00D),
    ADVERT_PRICE("fadah.advert-price.", Config.i().getListingAdverts().getDefaultPrice()),
    ;

    final String nodePrefix;
    final Number defaultValue;
    final boolean findHighest;

    Permission(String nodePrefix, Number defaultValue) {
        this.nodePrefix = nodePrefix;
        this.defaultValue = defaultValue;
        this.findHighest = false;
    }

    Permission(String nodePrefix, Number defaultValue, boolean findHighest) {
        this.nodePrefix = nodePrefix;
        this.defaultValue = defaultValue;
        this.findHighest = findHighest;
    }
}