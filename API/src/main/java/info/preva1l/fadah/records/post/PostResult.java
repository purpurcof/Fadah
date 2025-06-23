package info.preva1l.fadah.records.post;

import java.util.Objects;
import java.util.function.Consumer;

/**
 * The result of a post.
 * <br><br>
 * Created on 7/03/2025
 *
 * @since 3.0.0
 * @author Preva1l
 */
public class PostResult {
    /**
     * The post listed successfully.
     */
    public static final PostResult SUCCESS = new PostResult(0, "Success");
    /**
     * The post listed successfully, but the advert failed to post from a lack of funds.
     */
    public static final PostResult SUCCESS_ADVERT_FAIL = new PostResult(1, "Success (Advert Failed)");
    /**
     * The post was not listed because the item is restricted.
     */
    public static final PostResult RESTRICTED_ITEM = new PostResult(2, "Restricted Item");
    /**
     * The post was not listing because the player is at their max listings.
     */
    public static final PostResult MAX_LISTINGS = new PostResult(3, "Max Listings");

    private final int ordinal;
    private final String message;

    /**
     * Create a post result instance.
     *
     * @param ordinal the ordinal value of the post result.
     * @param message the readable description of the result.
     * @since 3.0.0
     */
    private PostResult(int ordinal, String message) {
        this.ordinal = ordinal;
        this.message = message;
    }

    /**
     * Gets a post result with a custom message.
     * <p>
     * The ordinal of a custom post result is {@code -999}.
     *
     * @param message the readable description of the result.
     * @return the post result.
     * @since 3.0.0
     */
    public static PostResult custom(String message) {
        return new PostResult(-999, message);
    }

    /**
     * Get the ordinal value of the post result.
     *
     * @return the post result's ordinal value.
     */
    public int ordinal() {
        return ordinal;
    }

    /**
     * Get the readable
     *
     * @return the readable description of the result.
     */
    public String message() {
        return message;
    }

    /**
     * Check if the post result was a successful post.
     *
     * @return true if the result was successful, else false
     * @implSpec The default implementation is only successful if the result is
     * {@link PostResult#SUCCESS} or {@link PostResult#SUCCESS_ADVERT_FAIL}. This could possibly change in the future.
     */
    public boolean successful() {
        return this == PostResult.SUCCESS || this == PostResult.SUCCESS_ADVERT_FAIL;
    }

    public PostResult success(Consumer<PostResult> supplier) {
        if (successful()) supplier.accept(this);
        return this;
    }

    public PostResult failure(Consumer<PostResult> supplier) {
        if (!successful()) supplier.accept(this);
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostResult that = (PostResult) o;
        return ordinal == that.ordinal;
    }

    @Override
    public int hashCode() {
        return Objects.hash(ordinal);
    }
}
