package be.rvponp.build.util;

import org.apache.commons.io.FilenameUtils;

/**
 * User: canas
 * Date: 8/2/13
 * Time: 4:27 PM
 * To change this template use File | Settings | File Templates.
 */
public final class Util {
    private Util() {}

    public static boolean isFile(Object o) {
        String key = o.toString();
        return !FilenameUtils.getExtension(key).isEmpty();
    }
}
