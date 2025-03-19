package info.preva1l.fadah.data.dao.common_sql;

import info.preva1l.fadah.records.listing.BidListing;
import info.preva1l.fadah.records.listing.BinListing;
import info.preva1l.fadah.records.listing.Listing;
import info.preva1l.fadah.utils.ItemSerializer;
import lombok.experimental.UtilityClass;
import org.bukkit.inventory.ItemStack;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.TreeSet;
import java.util.UUID;

/**
 * Created on 19/03/2025
 *
 * @author Preva1l
 */
@UtilityClass
public class CommonSQLDao {
    public PreparedStatement buildStatement(Listing listing, PreparedStatement statement) throws SQLException {
        statement.setString(1, listing.getId().toString());
        statement.setString(2, listing.getOwner().toString());
        statement.setString(3, listing.getOwnerName());
        statement.setString(4, listing.getCategoryID() + "~" + listing.getCurrencyId());
        statement.setLong(5, listing.getCreationDate());
        statement.setLong(6, listing.getDeletionDate());
        statement.setDouble(7, listing.getPrice());
        statement.setDouble(8, listing.getTax());
        statement.setString(9, ItemSerializer.serialize(listing.getItemStack()));
        statement.setBoolean(10, false);
        statement.setString(11, "");
        return statement;
    }

    public Listing create(UUID id, ResultSet resultSet) throws SQLException {
        final UUID ownerUUID = UUID.fromString(resultSet.getString("ownerUUID"));
        final String ownerName = resultSet.getString("ownerName");
        String temp = resultSet.getString("category");
        String currency;
        String categoryID;
        if (temp.contains("~")) {
            String[] t2 = temp.split("~");
            currency = t2[1];
            categoryID = t2[0];
        } else {
            currency = "vault";
            categoryID = temp;
        }
        final long creationDate = resultSet.getLong("creationDate");
        final long deletionDate = resultSet.getLong("deletionDate");
        final double price = resultSet.getDouble("price");
        final double tax = resultSet.getDouble("tax");
        final ItemStack itemStack = ItemSerializer.deserialize(resultSet.getString("itemStack"))[0];
        final boolean biddable = resultSet.getBoolean("biddable");

        final Listing listing;
        if (biddable) {
            listing = new BidListing(
                    id,
                    ownerUUID, ownerName,
                    itemStack,
                    categoryID,
                    currency, price, tax,
                    creationDate, deletionDate,
                    new TreeSet<>()
            );
        } else {
            listing = new BinListing(
                    id,
                    ownerUUID, ownerName,
                    itemStack,
                    categoryID,
                    currency, price, tax,
                    creationDate, deletionDate,
                    new TreeSet<>()
            );
        }

        return listing;
    }
}
