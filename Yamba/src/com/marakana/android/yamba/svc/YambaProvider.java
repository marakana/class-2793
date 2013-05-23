package com.marakana.android.yamba.svc;

import android.annotation.SuppressLint;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import com.marakana.android.yamba.BuildConfig;
import com.marakana.android.yamba.YambaContract;


/**
 * YambaProvider
 */
public class YambaProvider extends ContentProvider {
    private static final String TAG = "CP";

    private static final int TIMELINE_DIR = 1;
    private static final int TIMELINE_ITEM = 2;
    private static final int POST_DIR = 3;

    private static final UriMatcher uriMatcher;
    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
       uriMatcher.addURI(
                YambaContract.AUTHORITY,
                YambaContract.Timeline.TABLE,
                TIMELINE_DIR);
        uriMatcher.addURI(
                YambaContract.AUTHORITY,
                YambaContract.Timeline.TABLE + "/#",
                TIMELINE_ITEM);
        uriMatcher.addURI(
                YambaContract.AUTHORITY,
                YambaContract.Posts.TABLE,
                POST_DIR);
    }

    private static final ProjectionMap PROJ_MAP_TIMELINE = new ProjectionMap.Builder()
        .addColumn(YambaContract.Timeline.Columns.ID, YambaHelper.COL_ID)
        .addColumn(YambaContract.Timeline.Columns.TIMESTAMP, YambaHelper.COL_TIMESTAMP)
        .addColumn(YambaContract.Timeline.Columns.USER, YambaHelper.COL_USER)
        .addColumn(YambaContract.Timeline.Columns.STATUS, YambaHelper.COL_STATUS)
        .addColumn(YambaContract.Timeline.Columns.MAX_TIMESTAMP, "max(" + YambaHelper.COL_TIMESTAMP + ")")
        .build();

    private static final ColumnMap COL_MAP_TIMELINE = new ColumnMap.Builder()
        .addColumn(
                YambaContract.Timeline.Columns.ID,
                YambaHelper.COL_ID,
                ColumnMap.Type.LONG)
        .addColumn(
                YambaContract.Timeline.Columns.TIMESTAMP,
                YambaHelper.COL_TIMESTAMP,
                ColumnMap.Type.LONG)
        .addColumn(
                YambaContract.Timeline.Columns.USER,
                YambaHelper.COL_USER,
                ColumnMap.Type.STRING)
        .addColumn(
                YambaContract.Timeline.Columns.STATUS,
                YambaHelper.COL_STATUS,
                ColumnMap.Type.STRING)
        .build();

    private static final ColumnMap COL_MAP_POSTS = new ColumnMap.Builder()
        .addColumn(
                YambaContract.Posts.Columns.XACT,
                YambaHelper.COL_XACT,
                ColumnMap.Type.STRING)
        .addColumn(
                YambaContract.Posts.Columns.TIMESTAMP,
                YambaHelper.COL_TIMESTAMP,
                ColumnMap.Type.LONG)
        .addColumn(
                YambaContract.Posts.Columns.STATUS,
                YambaHelper.COL_STATUS,
                ColumnMap.Type.STRING)
        .build();


    private YambaHelper dbHelper;

    @Override
    public boolean onCreate() {
        dbHelper = new YambaHelper(getContext());
        return dbHelper != null;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
            case TIMELINE_ITEM:
                return YambaContract.Timeline.ITEM_TYPE;
            case TIMELINE_DIR:
                return YambaContract.Timeline.DIR_TYPE;
            case POST_DIR:
                return YambaContract.Posts.DIR_TYPE;
            default:
                return null;
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("fallthrough")
    @Override
    public Cursor query(Uri uri, String[] proj, String sel, String[] selArgs, String sort) {
        ProjectionMap pm;
        String table;
        long pk = -1;

        if (BuildConfig.DEBUG) { Log.d(TAG, "query: " + uri); }
        switch (uriMatcher.match(uri)) {
            case TIMELINE_ITEM:
                pk = ContentUris.parseId(uri);

            case TIMELINE_DIR:
                table = YambaHelper.TABLE_TIMELINE;
                pm = PROJ_MAP_TIMELINE;
                break;

            default:
                throw new IllegalArgumentException("URI unsupported in query: " + uri);
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            qb.setStrict(true);
        }

        qb.setProjectionMap(pm.getProjectionMap());

        qb.setTables(table);

        if (0 <= pk) { qb.appendWhere(YambaHelper.COL_ID + "=" + pk); }

        Cursor c = qb.query(getDb(), proj, sel, selArgs, null, null, sort);

        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public Uri insert(Uri uri, ContentValues vals) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "insert: " + uri); }
        switch (uriMatcher.match(uri)) {
            case POST_DIR:
                break;

            default:
                throw new IllegalArgumentException("URI unsupported in insert: " + uri);
        }

        String xact = YambaService.post(getContext(), vals.getAsString(YambaContract.Posts.Columns.STATUS));
        vals.put(YambaContract.Posts.Columns.XACT, xact);

        vals = COL_MAP_POSTS.translateCols(vals);

        long pk = getDb().insertOrThrow(YambaHelper.TABLE_POSTS, null, vals);
        if (0 >= pk) { return null; }

        uri = uri.buildUpon().appendPath(String.valueOf(pk)).build();

        getContext().getContentResolver().notifyChange(uri, null);

        return uri;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] vals) {
        String table;
        ColumnMap cm;

        if (BuildConfig.DEBUG) { Log.d(TAG, "bulk insert: " + uri); }

        switch (uriMatcher.match(uri)) {
            case TIMELINE_DIR:
                table = YambaHelper.TABLE_TIMELINE;
                cm = COL_MAP_TIMELINE;
                break;

            default:
                throw new UnsupportedOperationException("URI unsupported in bulk insert: " + uri);
        }

        SQLiteDatabase db = getDb();
        int count = 0;
        try {
            db.beginTransaction();
            for (ContentValues row: vals) {
                if (0 < db.insert(table, null, cm.translateCols(row))) { count++; }
            }
            db.setTransactionSuccessful();
        }
        finally { db.endTransaction(); }

        if (0 < count) {
            getContext().getContentResolver().notifyChange(YambaContract.Timeline.URI, null);
        }

        return count;
    }

    @Override
    public int update(Uri uri, ContentValues vals, String where, String[] whereArgs) {
        if (BuildConfig.DEBUG) { Log.d(TAG, "update: " + uri); }
        switch (uriMatcher.match(uri)) {
            case POST_DIR:
                break;

            default:
                throw new IllegalArgumentException("URI unsupported in update: " + uri);
        }

        vals = COL_MAP_POSTS.translateCols(vals);
        int n = getDb().update(YambaHelper.TABLE_POSTS, vals, where, whereArgs);

        if (0 < n) { getContext().getContentResolver().notifyChange(uri, null); }

        return n;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        throw new UnsupportedOperationException("Unsupported operation: delete");
    }

    private SQLiteDatabase getDb() { return dbHelper.getWritableDatabase(); }
}