package com.wwd.screenshot;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import com.tbruyelle.rxpermissions2.RxPermissions;
import io.reactivex.Observable;

/**
 * 创建者: wwd
 * 创建日期:2019-06-11
 * 类的功能描述:
 */
public class ScreenshotDetector {
    private static final String TAG = "RxScreenshotDetector";
    private static final String EXTERNAL_CONTENT_URI_MATCHER =
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();
    private static final String[] PROJECTION = new String[]{
            MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED
    };
    private static final String SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";
    private static final long DEFAULT_DETECT_WINDOW_SECONDS = 10;
    private final FragmentActivity mActivity;
    private final RxPermissions mRxPermissions;
    private ContentResolver contentResolver;
    private ContentObserver contentObserver;

    public ScreenshotDetector(final FragmentActivity activity) {
        mActivity = activity;
        contentResolver = activity.getContentResolver();
        mRxPermissions = new RxPermissions(activity);
    }

    private static boolean matchPath(String path) {
        return path.toLowerCase().contains("screenshot") || path.contains("截屏") ||
                path.contains("截图");
    }

    private static boolean matchTime(long currentTime, long dateAdded) {
        return Math.abs(currentTime - dateAdded) <= DEFAULT_DETECT_WINDOW_SECONDS;
    }

    public Observable<String> start() {
        return mRxPermissions
                .request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .flatMap(granted -> {
                    if (granted) {
                        return startAfterPermissionGranted();
                    } else {
                        return Observable.error(new SecurityException("Permission not granted"));
                    }
                });
    }

    public void unregisterObserver() {
        if (contentResolver != null && contentObserver != null) {
            Log.d("www", "取消了监听");
            contentResolver.unregisterContentObserver(contentObserver);
        }
    }

    private Observable<String> startAfterPermissionGranted() {
        return Observable.create(emitter -> {
            contentObserver = new ContentObserver(null) {
                @Override
                public void onChange(boolean selfChange, Uri uri) {
                    if (uri.toString().startsWith(EXTERNAL_CONTENT_URI_MATCHER)) {
                        Cursor cursor = null;
                        try {
                            cursor = contentResolver.query(uri, PROJECTION, null, null,
                                    SORT_ORDER);
                            if (cursor != null && cursor.moveToFirst()) {
                                String path = cursor.getString(
                                        cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                                long dateAdded = cursor.getLong(cursor.getColumnIndex(
                                        MediaStore.Images.Media.DATE_ADDED));
                                long currentTime = System.currentTimeMillis() / 1000;
                                if (matchPath(path) && matchTime(currentTime, dateAdded)) {
                                    emitter.onNext(path);
                                }
                            }
                        } catch (Exception e) {
                        } finally {
                            if (cursor != null) {
                                cursor.close();
                            }
                        }
                    }
                    super.onChange(selfChange, uri);
                }
            };
            contentResolver.registerContentObserver(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, contentObserver);

            emitter.setCancellable(
                    () -> {
                        Log.d("www", "取消了监听");
                        contentResolver.unregisterContentObserver(contentObserver);
                    });
        });
    }

}
