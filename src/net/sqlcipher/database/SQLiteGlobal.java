/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.sqlcipher.database;

import android.os.StatFs;

/**
 * Provides access to SQLite functions that affect all database connection,
 * such as memory management.
 *
 * The native code associated with SQLiteGlobal is also sets global configuration options
 * using sqlite3_config() then calls sqlite3_initialize() to ensure that the SQLite
 * library is properly initialized exactly once before any other framework or application
 * code has a chance to run.
 *
 * Verbose SQLite logging is enabled if the "log.tag.SQLiteLog" property is set to "V".
 * (per {@link SQLiteDebug#DEBUG_SQL_LOG}).
 *
 * @hide
 */
public final class SQLiteGlobal {
    private static final String TAG = "SQLiteGlobal";
    
    /**
     * The default journal mode to use use when Write-Ahead Logging is not active.
     * Choices are: OFF, DELETE, TRUNCATE, PERSIST and MEMORY.
     * PERSIST may improve performance by reducing how often journal blocks are
     * reallocated (compared to truncation) resulting in better data block locality
     * and less churn of the storage media. 
     */
    private static final String DEFAULT_JOURNAL_MODE = "PERSIST";
    
    /**
     * Maximum size of the persistent journal file in bytes. 
     * If the journal file grows to be larger than this amount then SQLite will
     * truncate it after committing the transaction
     */
    private static final int JOURNAL_SIZE_LIMIT = 524288;
    
    /**
     * The database synchronization mode when using the default journal mode.
     * FULL is safest and preserves durability at the cost of extra fsyncs.
     * NORMAL also preserves durability in non-WAL modes and uses checksums to ensure
     * integrity although there is a small chance that an error might go unnoticed.
     * Choices are: FULL, NORMAL, OFF. 
     */
    private static final String DEFAULT_SYNC_MODE = "FULL";
    
    /**
     * The database synchronization mode when using Write-Ahead Logging.
     * FULL is safest and preserves durability at the cost of extra fsyncs.
     * NORMAL sacrifices durability in WAL mode because syncs are only performed before
     * and after checkpoint operations.  If checkpoints are infrequent and power loss
     * occurs, then committed transactions could be lost and applications might break.
     * Choices are: FULL, NORMAL, OFF.
     */
    private static final String WAL_SYNC_MODE = "FULL";
    
    /**
     * The Write-Ahead Log auto-checkpoint interval in database pages (typically 1 to 4KB).
     * The log is checkpointed automatically whenever it exceeds this many pages.
     * When a database is reopened, its journal mode is set back to the default
     * journal mode, which may cause a checkpoint operation to occur.  Checkpoints
     * can also happen at other times when transactions are committed.
     * The bigger the WAL file, the longer a checkpoint operation takes, so we try
     * to keep the WAL file relatively small to avoid long delays.
     * The size of the WAL file is also constrained by 'db_journal_size_limit'.
     */
    private static final int WAL_AUTOCHECKPOINT = 100;
    
    /**
     * Maximum number of database connections opened and managed by framework layer
     * to handle queries on each database when using Write-Ahead Logging.
     */
    private static final int WAL_CONNECTION_POOL_SIZE = 4;

    private static final Object sLock = new Object();
    private static int sDefaultPageSize;

    private static native int nativeReleaseMemory();
    
    /**
     * Sets the root directory to search for the ICU data file
     */
    public static native void nativeSetupEnv(String icuPathStr);

    private SQLiteGlobal() {
    }

    /**
     * Attempts to release memory by pruning the SQLite page cache and other
     * internal data structures.
     *
     * @return The number of bytes that were freed.
     */
    public static int releaseMemory() {
        return nativeReleaseMemory();
    }
    
    public static void setupEnv(String icuPathStr) {
        nativeSetupEnv(icuPathStr);
    }

    /**
     * Gets the default page size to use when creating a database.
     */
    public static int getDefaultPageSize() {
        synchronized (sLock) {
            if (sDefaultPageSize == 0) {
                sDefaultPageSize = new StatFs("/data").getBlockSize();
            }
            return sDefaultPageSize;
        }
    }

    /**
     * Gets the default journal mode when WAL is not in use.
     */
    public static String getDefaultJournalMode() {
        return DEFAULT_JOURNAL_MODE;
                
    }

    /**
     * Gets the journal size limit in bytes.
     */
    public static int getJournalSizeLimit() {
        return JOURNAL_SIZE_LIMIT;
    }

    /**
     * Gets the default database synchronization mode when WAL is not in use.
     */
    public static String getDefaultSyncMode() {
        return DEFAULT_SYNC_MODE;
    }

    /**
     * Gets the database synchronization mode when in WAL mode.
     */
    public static String getWALSyncMode() {
        return WAL_SYNC_MODE;
    }

    /**
     * Gets the WAL auto-checkpoint integer in database pages.
     */
    public static int getWALAutoCheckpoint() {
        int value = WAL_AUTOCHECKPOINT;
        return Math.max(1, value);
    }

    /**
     * Gets the connection pool size when in WAL mode.
     */
    public static int getWALConnectionPoolSize() {
        int value = WAL_CONNECTION_POOL_SIZE;
        return Math.max(2, value);
    }
}
