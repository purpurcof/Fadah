package info.preva1l.fadah;

import info.preva1l.trashcan.extension.libloader.BaseLibraryLoader;

import java.nio.file.Path;
import java.util.function.Predicate;

/**
 * Created on 29/04/2025
 *
 * @author Preva1l
 */
public class FadahLibraryloader extends BaseLibraryLoader {
    @Override
    protected Predicate<Path> remapPredicate() {
        return path -> path.toString().toLowerCase().contains("anvilgui");
    }
}
