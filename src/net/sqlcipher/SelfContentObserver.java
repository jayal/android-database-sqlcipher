package net.sqlcipher;

import java.lang.ref.WeakReference;

import android.database.ContentObserver;

/**
 * Cursors use this class to track changes others make to their URI.
 */
public class SelfContentObserver extends ContentObserver {
    WeakReference<AbstractCursor> mCursor;

    public SelfContentObserver(AbstractCursor cursor) {
        super(null);
        mCursor = new WeakReference<AbstractCursor>(cursor);
    }

    @Override
    public boolean deliverSelfNotifications() {
        return false;
    }

    @Override
    public void onChange(boolean selfChange) {
        AbstractCursor cursor = mCursor.get();
        if (cursor != null) {
            cursor.onChange(false);
        }
    }
}
