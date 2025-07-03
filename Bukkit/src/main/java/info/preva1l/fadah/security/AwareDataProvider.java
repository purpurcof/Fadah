package info.preva1l.fadah.security;

/**
 * Created on 16/06/2025
 *
 * @author Preva1l
 */
public interface AwareDataProvider<T> {
    /**
     * This method only runs action if the object is safe to modify.
     */
    void execute(T obj, Runnable action);
}
