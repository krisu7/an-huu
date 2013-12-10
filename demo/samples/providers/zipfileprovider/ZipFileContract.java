/*
 *    Copyright (c) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package ...;

public class ZipFileContract {

    /**
     * The raw authority of this provider.
     */
    public static final String AUTHORITY = "your-authority";

    /**
     * The unique ID of this provider.
     */
    public static final String _ID = "your-UUID";

    /**
     * Custom scheme for this provider.
     * <p>
     * The full URI for this scheme has this form:
     * 
     * <pre>
     * <code>zipfile://[authority]/[path]</code>
     * </pre>
     * 
     * </p>
     * <p>
     * Where:
     * <li><code>[authority]</code> is an encoded string which points to full
     * pathname to the zip file on local file system. E.g:
     * <code>/sdcard/test.zip</code></li>
     * <li><code>[path]</code> is an encoded string which points to full
     * pathname of a single file in the zip file given. E.g: <code>readme</code>
     * </li>
     * </p>
     */
    public static final String SCHEME = "zipfile";
}
