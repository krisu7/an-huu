/*
 *    Copyright (c) 2012 Hai Bison
 *
 *    See the file LICENSE at the root directory of this project for copying
 *    permission.
 */

package ...;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.MatrixCursor.RowBuilder;
import android.net.Uri;
import android.util.Log;

import com.haibison.android.lib.anhuu.providers.BaseFileProviderUtils;
import com.haibison.android.lib.anhuu.providers.ProviderUtils;
import com.haibison.android.lib.anhuu.providers.basefile.BaseFileContract.BaseFile;
import com.haibison.android.lib.anhuu.providers.basefile.BaseFileProvider;
import com.haibison.android.lib.anhuu.utils.FileUtils;
import com.haibison.android.lib.anhuu.utils.Texts;

public class ZipFileProvider extends BaseFileProvider {

    private static final String CLASSNAME = ZipFileProvider.class.getName();

    private ZipFile mZipFile;

    @Override
    public boolean onCreate() {
        URI_MATCHER.addURI(ZipFileContract.AUTHORITY, BaseFile.PATH_DIR + "/*",
                URI_DIRECTORY);
        URI_MATCHER.addURI(ZipFileContract.AUTHORITY,
                BaseFile.PATH_FILE + "/*", URI_FILE);
        URI_MATCHER.addURI(ZipFileContract.AUTHORITY, BaseFile.PATH_API,
                URI_API);
        URI_MATCHER.addURI(ZipFileContract.AUTHORITY, BaseFile.PATH_API + "/*",
                URI_API_COMMAND);

        BaseFileProviderUtils.registerProviderInfo(ZipFileContract._ID,
                ZipFileContract.AUTHORITY);

        return true;
    }// onCreate()

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        if (BuildConfig.DEBUG)
            Log.d(CLASSNAME, "query() >> " + uri);

        switch (URI_MATCHER.match(uri)) {
        case URI_API: {
            /*
             * If there is no command given, return provider ID and name.
             */
            MatrixCursor matrixCursor = new MatrixCursor(new String[] {
                    BaseFile.COLUMN_PROVIDER_ID, BaseFile.COLUMN_PROVIDER_NAME,
                    BaseFile.COLUMN_PROVIDER_ICON_ATTR });
            matrixCursor.newRow().add(ZipFileContract._ID)
                    .add(getContext().getString(R.string.zipfile))
                    .add(R.drawable.badge_fileprovider_zipfile);
            return matrixCursor;
        }// URI_API
        case URI_API_COMMAND: {
            return doAnswerApiCommand(uri);
        }// URI_API_COMMAND

        case URI_DIRECTORY: {
            return doListFiles(uri);
        }// URI_DIRECTORY

        case URI_FILE: {
            return doRetrieveFileInfo(uri);
        }// URI_FILE

        default:
            throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }// query()

    /**
     * Answers the incoming URI.
     * 
     * @param uri
     *            the request URI.
     * @return the response.
     */
    private MatrixCursor doAnswerApiCommand(Uri uri) {
        MatrixCursor matrixCursor = null;

        if (BaseFile.CMD_CANCEL.equals(uri.getLastPathSegment())) {
            int taskId = ProviderUtils.getIntQueryParam(uri,
                    BaseFile.PARAM_TASK_ID, 0);
            synchronized (mMapInterruption) {
                if (taskId == 0) {
                    for (int i = 0; i < mMapInterruption.size(); i++)
                        mMapInterruption.put(mMapInterruption.keyAt(i), true);
                } else if (mMapInterruption.indexOfKey(taskId) >= 0)
                    mMapInterruption.put(taskId, true);
            }
            return null;
        } else if (BaseFile.CMD_GET_DEFAULT_PATH.equals(uri
                .getLastPathSegment())) {
            /*
             * We don't have a default path.
             */
        }// get default path
        else if (BaseFile.CMD_IS_ANCESTOR_OF.equals(uri.getLastPathSegment())) {
            return doCheckAncestor(uri);
        } else if (BaseFile.CMD_GET_PARENT.equals(uri.getLastPathSegment())) {
            return doGetParent(uri);
        } else if (BaseFile.CMD_SHUTDOWN.equals(uri.getLastPathSegment())) {
            /*
             * TODO Stop all tasks. If the activity call this command in
             * onDestroy(), it seems that this code block will be suspended and
             * started next time the activity starts. So we comment out this.
             * Let the Android system do what it wants to do!!!! I hate this.
             */
            // synchronized (mMapInterruption) {
            // for (int i = 0; i < mMapInterruption.size(); i++)
            // mMapInterruption.put(mMapInterruption.keyAt(i), true);
            // }
        }

        return matrixCursor;
    }// doAnswerApiCommand()

    /**
     * Checks ancestor with {@link BaseFile#CMD_IS_ANCESTOR_OF},
     * {@link BaseFile#PARAM_SOURCE} and {@link BaseFile#PARAM_TARGET}.
     * 
     * @param uri
     *            the original URI from client.
     * @return {@code null} if source is not ancestor of target; or a
     *         <i>non-null but empty</i> cursor if the source is.
     */
    private MatrixCursor doCheckAncestor(Uri uri) {
        String srcZipPath = Uri.parse(
                uri.getQueryParameter(BaseFile.PARAM_SOURCE)).getAuthority();
        if (!srcZipPath.equals(Uri.parse(
                uri.getQueryParameter(BaseFile.PARAM_TARGET)).getAuthority()))
            return null;

        /*
         * Open source zip file.
         */
        ZipFile zipFile = null;
        try {
            zipFile = new ZipFile(srcZipPath);
        } catch (IOException e) {
            return null;
        }

        try {
            ZipEntry source = zipFile.getEntry(Uri
                    .parse(uri.getQueryParameter(BaseFile.PARAM_SOURCE))
                    .getPath().replaceFirst("^/+", ""));
            ZipEntry target = zipFile.getEntry(Uri
                    .parse(uri.getQueryParameter(BaseFile.PARAM_TARGET))
                    .getPath().replaceFirst("^/+", ""));
            if (source == null || target == null)
                return null;

            boolean validate = ProviderUtils.getBooleanQueryParam(uri,
                    BaseFile.PARAM_VALIDATE, true);
            if (validate && !source.isDirectory())
                return null;

            if (source.getName().equals(target.getName()))
                return null;

            /*
             * Prefix file path with `/` so the "fake" file will take that as an
             * absolute path.
             */
            File fakeSource = newFakeFile(source);
            File fakeTarget = newFakeFile(target);
            if (fakeSource.equals(fakeTarget.getParentFile())
                    || (fakeTarget.getParent() != null && fakeTarget
                            .getParent().startsWith(
                                    fakeSource.getAbsolutePath())))
                return BaseFileProviderUtils.newClosedCursor();
        } finally {
            try {
                zipFile.close();
            } catch (IOException e) {
                /*
                 * Ignore it.
                 */
            }
        }

        return null;
    }// doCheckAncestor()

    /**
     * Gets parent file of a file.
     * 
     * @param uri
     *            the URI points to a file.
     * @return the cursor containing parent file information of the given file.
     *         Can be {@code null} if not found.
     */
    private MatrixCursor doGetParent(Uri uri) {
        MatrixCursor cursor = null;

        Uri sourceUri = Uri.parse(uri.getQueryParameter(BaseFile.PARAM_SOURCE));
        if (BuildConfig.DEBUG)
            Log.d(CLASSNAME, "CMD_GET_PARENT >> path = " + sourceUri.getPath());

        if (!openZipFileWithInternalUri(sourceUri))
            return null;

        if (android.text.TextUtils.isEmpty(sourceUri.getPath().replaceFirst(
                "^/+", "")))
            return null;

        File fakeParentFile = new File(sourceUri.getPath()).getParentFile();
        ZipEntry zipEntry = null;
        if (fakeParentFile != null) {
            zipEntry = mZipFile.getEntry(fakeParentFile.getAbsolutePath()
                    .replaceFirst("^/+", ""));
            if (zipEntry == null) {
                /*
                 * APK files don't list directories. So we find it manually.
                 */
                Enumeration<? extends ZipEntry> entries = mZipFile.entries();
                String path = fakeParentFile.getAbsolutePath();
                while (entries.hasMoreElements()) {
                    ZipEntry ze = entries.nextElement();
                    File fakeFile = newFakeFile(ze);
                    while (fakeFile != null) {
                        if (fakeFile.getAbsolutePath().equals(path)) {
                            zipEntry = new ZipEntry(fakeFile.getAbsolutePath()
                                    .replaceFirst("^/+", "") + "/");
                            zipEntry.setSize(0);
                            zipEntry.setTime(ze.getTime());
                            break;
                        }
                        fakeFile = fakeFile.getParentFile();
                    }// while
                    if (zipEntry != null)
                        break;
                }// while
            }
        }

        if (BuildConfig.DEBUG)
            Log.d(CLASSNAME, "fakeParentFile = " + fakeParentFile);

        cursor = BaseFileProviderUtils.newBaseFileCursor();

        Uri zipFileUri = Uri
                .parse(ZipFileContract.SCHEME + "://")
                .buildUpon()
                .authority(sourceUri.getAuthority())
                .appendPath(
                        zipEntry == null ? "" : zipEntry.getName()
                                .replaceFirst("/+$", "")).build();
        RowBuilder newRow = cursor.newRow();
        newRow.add(0);// _ID
        newRow.add(BaseFile.genContentIdUriBase(ZipFileContract.AUTHORITY)
                .buildUpon().appendPath(zipFileUri.toString()).build()
                .toString());
        newRow.add(zipFileUri.toString());
        newRow.add(fakeParentFile == null
                || fakeParentFile.getParentFile() == null ? new File(mZipFile
                .getName()).getName() : fakeParentFile.getName());
        newRow.add(1);
        newRow.add(0);
        newRow.add(0);
        newRow.add(BaseFile.FILE_TYPE_DIRECTORY);
        newRow.add(zipEntry == null ? new File(sourceUri.getAuthority())
                .lastModified() : zipEntry.getTime());
        newRow.add(FileUtils.getResIcon(BaseFile.FILE_TYPE_DIRECTORY, ""));

        return cursor;
    }// doGetParent()

    /**
     * Lists the content of a directory, if available.
     * 
     * @param uri
     *            the URI pointing to a directory.
     * @return the content of a directory, or {@code null} if not available.
     */
    private MatrixCursor doListFiles(Uri uri) {
        MatrixCursor matrixCursor = BaseFileProviderUtils.newBaseFileCursor();

        if (!openZipFile(uri))
            return null;

        String rootPath = extractRootPath(uri);

        if (BuildConfig.DEBUG)
            Log.d(CLASSNAME, "mZipFile = " + mZipFile);

        /*
         * Prepare params...
         */
        int taskId = ProviderUtils.getIntQueryParam(uri,
                BaseFile.PARAM_TASK_ID, 0);
        boolean showHiddenFiles = ProviderUtils.getBooleanQueryParam(uri,
                BaseFile.PARAM_SHOW_HIDDEN_FILES);
        boolean sortAscending = ProviderUtils.getBooleanQueryParam(uri,
                BaseFile.PARAM_SORT_ASCENDING, true);
        int sortBy = ProviderUtils.getIntQueryParam(uri,
                BaseFile.PARAM_SORT_BY, BaseFile.SORT_BY_NAME);
        int filterMode = ProviderUtils.getIntQueryParam(uri,
                BaseFile.PARAM_FILTER_MODE,
                BaseFile.FILTER_FILES_AND_DIRECTORIES);
        int limit = ProviderUtils.getIntQueryParam(uri, BaseFile.PARAM_LIMIT,
                1000);
        String positiveRegex = uri
                .getQueryParameter(BaseFile.PARAM_POSITIVE_REGEX_FILTER);
        String negativeRegex = uri
                .getQueryParameter(BaseFile.PARAM_NEGATIVE_REGEX_FILTER);

        mMapInterruption.put(taskId, false);

        boolean[] hasMoreFiles = { false };
        List<ZipEntry> files = new ArrayList<ZipEntry>();
        listFiles(taskId, mZipFile, rootPath, showHiddenFiles, filterMode,
                limit, positiveRegex, negativeRegex, files, hasMoreFiles);
        if (!mMapInterruption.get(taskId)) {
            sortFiles(taskId, files, sortAscending, sortBy);
            if (!mMapInterruption.get(taskId)) {
                final String zipFileUri = Uri.parse(uri.getLastPathSegment())
                        .getAuthority();
                for (int i = 0; i < files.size(); i++) {
                    if (mMapInterruption.get(taskId))
                        break;

                    ZipEntry ze = files.get(i);
                    Uri zeUri = Uri.parse(ZipFileContract.SCHEME + "://")
                            .buildUpon().authority(zipFileUri)
                            .appendPath(ze.getName().replaceFirst("/+$", ""))
                            .build();
                    String name = new File(ze.getName()).getName();

                    int type = ze.isDirectory() ? BaseFile.FILE_TYPE_DIRECTORY
                            : BaseFile.FILE_TYPE_FILE;
                    RowBuilder newRow = matrixCursor.newRow();
                    newRow.add(i);// _ID
                    newRow.add(BaseFile
                            .genContentIdUriBase(ZipFileContract.AUTHORITY)
                            .buildUpon().appendPath(zeUri.toString()).build()
                            .toString());
                    newRow.add(zeUri.toString());
                    newRow.add(name);
                    newRow.add(1);
                    newRow.add(0);
                    newRow.add(ze.getSize());
                    newRow.add(type);
                    newRow.add(ze.getTime());
                    newRow.add(FileUtils.getResIcon(type, name));
                }// for files

                /*
                 * The last row contains:
                 * 
                 * - The ID;
                 * 
                 * - The base file URI to original directory, which has
                 * parameter BaseFile.PARAM_HAS_MORE_FILES to indicate the
                 * directory has more files or not.
                 * 
                 * - The system absolute path to original directory.
                 * 
                 * - The name of original directory.
                 */
                RowBuilder newRow = matrixCursor.newRow();
                newRow.add(files.size());// _ID
                newRow.add(BaseFile
                        .genContentIdUriBase(ZipFileContract.AUTHORITY)
                        .buildUpon()
                        .appendPath(uri.getLastPathSegment())
                        .appendQueryParameter(BaseFile.PARAM_HAS_MORE_FILES,
                                Boolean.toString(hasMoreFiles[0])).build()
                        .toString());
                newRow.add(uri.getLastPathSegment());
                newRow.add(new File(mZipFile.getName()).getName());
            }
        }

        try {
            if (mMapInterruption.get(taskId)) {
                return null;
            }
        } finally {
            mMapInterruption.delete(taskId);
        }

        return matrixCursor;
    }// doListFiles()

    /**
     * Lists all file inside {@code zipFile}.
     * 
     * @param taskId
     *            the task ID.
     * @param zipFile
     *            the source zip file.
     * @param rootPath
     *            the sub directory within the {@code zipFile} to be listed the
     *            contents. Can be {@code null} if you want to list the root
     *            one.
     * @param showHiddenFiles
     *            {@code true} or {@code false}.
     * @param filterMode
     *            can be one of {@link BaseFile#_FilterDirectoriesOnly},
     *            {@link BaseFile#_FilterFilesOnly},
     *            {@link BaseFile#_FilterFilesAndDirectories}.
     * @param limit
     *            the limit.
     * @param positiveRegex
     *            the positive regex filter.
     * @param negativeRegex
     *            the negative regex filter.
     * @param results
     *            the results.
     * @param hasMoreFiles
     *            the first item will contain a value representing that there is
     *            more files (exceeding {@code limit}) or not.
     */
    private void listFiles(int taskId, ZipFile zipFile, String rootPath,
            boolean showHiddenFiles, int filterMode, int limit,
            String positiveRegex, String negativeRegex, List<ZipEntry> results,
            boolean hasMoreFiles[]) {
        final Pattern positivePattern = Texts.compileRegex(positiveRegex);
        final Pattern negativePattern = Texts.compileRegex(negativeRegex);

        File fakeRoot = null;
        if (!android.text.TextUtils.isEmpty(rootPath))
            fakeRoot = new File(rootPath);

        hasMoreFiles[0] = false;
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            if (mMapInterruption.get(taskId))
                break;

            ZipEntry zipEntry = entries.nextElement();
            if (BuildConfig.DEBUG)
                Log.d(CLASSNAME, "listFiles() >> " + zipEntry.getName());

            if (zipEntry.isDirectory()) {
                if ((zipEntry = findSubDir(results, rootPath, zipEntry)) == null)
                    continue;
            } else {
                File fakeParentEntry = newFakeFile(zipEntry).getParentFile();
                if (fakeRoot != null) {
                    if (fakeParentEntry.getParentFile() == null)
                        continue;
                    if (!fakeRoot.getPath().equals(fakeParentEntry.getPath()))
                        if ((zipEntry = findSubDir(results, rootPath, zipEntry)) == null)
                            continue;
                } else if (fakeParentEntry.getParentFile() != null)
                    if ((zipEntry = findSubDir(results, rootPath, zipEntry)) == null)
                        continue;
            }

            final boolean isFile = !zipEntry.isDirectory();

            /*
             * Filters...
             */

            if (filterMode == BaseFile.FILTER_DIRECTORIES_ONLY && isFile)
                continue;

            String name = new File(zipEntry.getName()).getName();

            if (!showHiddenFiles && name.startsWith("."))
                continue;
            if (isFile && positivePattern != null
                    && !positivePattern.matcher(name).find())
                continue;
            if (isFile && negativePattern != null
                    && negativePattern.matcher(name).find())
                continue;

            /*
             * Limit...
             */
            if (results.size() >= limit) {
                hasMoreFiles[0] = true;
                break;
            }
            results.add(zipEntry);
        }// while
    }// listFiles()

    /**
     * Finds to see if {@code file} or one of ancestors of {@code file} exists
     * in {@code entries} <b><i>and</i></b> has parent {@code root}.
     * 
     * @param entries
     *            the list of entries.
     * @param rootPath
     *            the root entry, can be {@code null} to indicate root level of
     *            the zip file.
     * @param file
     *            the entry to check.
     * @return {@code null} if {@code file} or one of ancestors of {@code file}
     *         exists in {@code entries} <b><i>and</i></b> has parent
     *         {@code root}. Or the ancestor of {@code file} or the {@code file}
     *         itself otherwise.
     */
    private ZipEntry findSubDir(List<ZipEntry> entries, String rootPath,
            ZipEntry file) {
        File fakeDir = newFakeFile(file);
        if (android.text.TextUtils.isEmpty(rootPath)) {
            while (fakeDir.getParentFile() != null
                    && fakeDir.getParentFile().getParentFile() != null)
                fakeDir = fakeDir.getParentFile();
        } else {
            File fakeRoot = new File(rootPath);
            while (true) {
                if (fakeDir.getParentFile() == null)
                    return null;
                if (fakeDir.getParentFile().equals(fakeRoot))
                    break;
                fakeDir = fakeDir.getParentFile();
            }
        }

        boolean found = false;
        String path = fakeDir.getAbsolutePath().replaceFirst("^/+", "");
        for (ZipEntry ze : entries) {
            if (ze.getName().replaceFirst("/+$", "").equals(path)) {
                found = true;
                break;
            }
        }

        if (!found) {
            ZipEntry ze = new ZipEntry(path + "/");
            ze.setSize(0);
            ze.setTime(file.getTime());
            return ze;
        }

        return null;
    }// findSubDir()

    /**
     * Creates new "fake" file of a zip entry.
     * 
     * @param zipEntry
     *            the zip entry.
     * @return the "fake" file.
     */
    private File newFakeFile(ZipEntry zipEntry) {
        return new File("/" + zipEntry.getName());
    }// newFakeFile()

    /**
     * Sorts {@code files}.
     * 
     * @param taskId
     *            the task ID.
     * @param files
     *            list of files.
     * @param ascending
     *            {@code true} or {@code false}.
     * @param sortBy
     *            can be one of {@link BaseFile.#_SortByModificationTime},
     *            {@link BaseFile.#_SortByName}, {@link BaseFile.#_SortBySize}.
     */
    private void sortFiles(final int taskId, final List<ZipEntry> files,
            final boolean ascending, final int sortBy) {
        try {
            Collections.sort(files, new Comparator<ZipEntry>() {

                @Override
                public int compare(ZipEntry lhs, ZipEntry rhs) {
                    if (mMapInterruption.get(taskId))
                        throw new CancellationException();

                    if (lhs.isDirectory() && !rhs.isDirectory())
                        return -1;
                    if (!lhs.isDirectory() && rhs.isDirectory())
                        return 1;

                    /*
                     * Default is to compare by name (case insensitive).
                     */
                    int res = mCollator.compare(lhs.getName(), rhs.getName());

                    switch (sortBy) {
                    case BaseFile.SORT_BY_NAME:
                        break;// SortByName

                    case BaseFile.SORT_BY_SIZE:
                        if (lhs.getSize() > rhs.getSize())
                            res = 1;
                        else if (lhs.getSize() < rhs.getSize())
                            res = -1;
                        break;// SortBySize

                    case BaseFile.SORT_BY_MODIFICATION_TIME:
                        if (lhs.getTime() > rhs.getTime())
                            res = 1;
                        else if (lhs.getTime() < rhs.getTime())
                            res = -1;
                        break;// SortByDate
                    }

                    return ascending ? res : -res;
                }// compare()
            });
        } catch (CancellationException e) {
            if (BuildConfig.DEBUG)
                Log.d(CLASSNAME, "sortFiles() >> cancelled...");
        }
    }// sortFiles()

    /**
     * Retrieves file information of a single file.
     * 
     * @param uri
     *            the URI pointing to a file.
     * @return the file information. Can be {@code null}, based on the input
     *         parameters.
     */
    private MatrixCursor doRetrieveFileInfo(Uri uri) {
        if (BuildConfig.DEBUG)
            Log.d(CLASSNAME, "doRetrieveFileInfo() >> " + uri);

        if (!openZipFile(uri))
            return null;

        ZipEntry zipEntry = null;
        String rootPath = extractRootPath(uri);
        if (!android.text.TextUtils.isEmpty(rootPath)) {
            zipEntry = mZipFile.getEntry(rootPath.replaceFirst("^/+", ""));
            if (zipEntry == null) {
                /*
                 * In this case, perhaps this is an APK file. APK files don't
                 * have directory entries. So we iterate all entries to find the
                 * directory...
                 */
                Enumeration<? extends ZipEntry> entries = mZipFile.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry ze = entries.nextElement();
                    File fakeFile = newFakeFile(ze);
                    while (fakeFile != null) {
                        if (fakeFile.getAbsolutePath().equals(rootPath)) {
                            zipEntry = new ZipEntry(fakeFile.getAbsolutePath()
                                    .replaceFirst("^/+", "") + "/");
                            zipEntry.setSize(0);
                            zipEntry.setTime(ze.getTime());
                            break;
                        }
                        fakeFile = fakeFile.getParentFile();
                    }// while
                    if (zipEntry != null)
                        break;
                }// while
            }
            if (zipEntry == null)
                return null;
        }

        MatrixCursor matrixCursor = BaseFileProviderUtils.newBaseFileCursor();

        int type = zipEntry == null ? BaseFile.FILE_TYPE_DIRECTORY : (zipEntry
                .isDirectory() ? BaseFile.FILE_TYPE_DIRECTORY
                : BaseFile.FILE_TYPE_FILE);
        String name = zipEntry == null ? new File(mZipFile.getName()).getName()
                : new File(zipEntry.getName()).getName();
        RowBuilder newRow = matrixCursor.newRow();
        newRow.add(0);// _ID
        newRow.add(uri.toString());
        newRow.add(Uri.parse(uri.getLastPathSegment()).toString());
        newRow.add(name);
        newRow.add(1);
        newRow.add(0);
        newRow.add(zipEntry == null ? 0 : zipEntry.getSize());
        newRow.add(type);
        newRow.add(zipEntry == null ? new File(Uri.parse(
                uri.getLastPathSegment()).getAuthority()).lastModified()
                : zipEntry.getTime());
        newRow.add(FileUtils.getResIcon(type, name));

        return matrixCursor;
    }// doRetrieveFileInfo()

    /**
     * Extracts the path to sub file inside the zip file from request URI.
     * 
     * @param uri
     *            the original URI.
     * @return the path to sub file in a zip file. Or {@code null} if not
     *         available.
     */
    private static String extractRootPath(Uri uri) {
        String path = Uri.parse(uri.getLastPathSegment()).getPath();
        if (uri.getQueryParameter(BaseFile.PARAM_APPEND_PATH) != null)
            path += Uri
                    .parse(uri.getQueryParameter(BaseFile.PARAM_APPEND_PATH))
                    .getPath();
        if (uri.getQueryParameter(BaseFile.PARAM_APPEND_NAME) != null)
            path += "/" + uri.getQueryParameter(BaseFile.PARAM_APPEND_NAME);

        if (BuildConfig.DEBUG)
            Log.d(CLASSNAME, "extractFilePath() >> " + path);

        return android.text.TextUtils.isEmpty(path) || path.equals("/") ? null
                : path.replaceFirst("/+$", "");
    }// extractRootPath()

    /**
     * Opens ZIP file from original content provider's URI.
     * 
     * @param uri
     *            the original content provider's URI.
     * @return {@code true} if ok, {@code false} if an error occurred.
     */
    private boolean openZipFile(Uri uri) {
        if (BuildConfig.DEBUG)
            Log.d(CLASSNAME, String.format(
                    "openZipFile() >> last-path=[%s],auth=[%s]",
                    uri.getLastPathSegment(),
                    Uri.parse(uri.getLastPathSegment()).getAuthority()));

        return openZipFile(Uri.parse(uri.getLastPathSegment()).getAuthority());
    }// openZipFile()

    /**
     * Opens ZIP file from internal URI.
     * 
     * @param uri
     *            the internal URI.
     * @return {@code true} if ok, {@code false} if an error occurred.
     */
    private boolean openZipFileWithInternalUri(Uri uri) {
        return openZipFile(uri.getAuthority());
    }// openZipFileWithInternalUri()

    /**
     * Opens zip file with {@code pathname}.
     * 
     * @param pathname
     *            the full path to the file.
     * @return {@code true} if ok, {@code false} if an error occurred.
     */
    private boolean openZipFile(String pathname) {
        try {
            if (mZipFile != null && !mZipFile.getName().equals(pathname)) {
                try {
                    mZipFile.close();
                } catch (IOException e) {
                    /*
                     * Ignore it.
                     */
                }
                mZipFile = null;
            }
            if (mZipFile == null)
                mZipFile = new ZipFile(pathname);
            return true;
        } catch (IOException e) {
            return false;
        }
    }// openZipFile()
}
