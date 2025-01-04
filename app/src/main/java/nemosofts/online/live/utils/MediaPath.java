package nemosofts.online.live.utils;

import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class MediaPath {

    private MediaPath() {
        throw new IllegalStateException("Utility class");
    }

    public static String getPathImage(Context ctx, Uri uri) {
        if (uri == null) {
            return null;
        }

        // Handle the document Uri (API level 19 and above)
        if (DocumentsContract.isDocumentUri(ctx, uri)) {
            // External storage document
            if (isExternalStorageDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // Handle non-primary volumes, typically with storage emulation
                // You'll need to extend this logic depending on your app's requirements.
            }
            // Downloads folder
            else if (isDownloadsDocument(uri)) {
                String id = DocumentsContract.getDocumentId(uri);
                Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));

                return getDataColumn(ctx, contentUri, null, null);
            }
            // Media provider (images, videos, etc.)
            else if (isMediaDocument(uri)) {
                String docId = DocumentsContract.getDocumentId(uri);
                String[] split = docId.split(":");
                String type = split[0];
                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                }

                String selection = MediaStore.Images.Media._ID + "=?";
                String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(ctx, contentUri, selection, selectionArgs);
            }
        }
        // Handle content URIs (e.g., from gallery, file manager)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // Return the remote address or attempt to get file path
            return getDataColumn(ctx, uri, null, null);
        }
        // Handle file URIs
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for MediaStore Uris and other file-based content providers.
     */
    @Nullable
    private static String getDataColumn(@NonNull Context context, Uri uri, String selection,
                                        String[] selectionArgs) {
        Cursor cursor = null;
        final String column = MediaStore.Images.Media.DATA;
        final String[] projection = {column};

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * Check if the Uri authority is ExternalStorageProvider.
     */
    private static boolean isExternalStorageDocument(@NonNull Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * Check if the Uri authority is DownloadsProvider.
     */
    private static boolean isDownloadsDocument(@NonNull Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * Check if the Uri authority is MediaProvider.
     */
    private static boolean isMediaDocument(@NonNull Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

}
