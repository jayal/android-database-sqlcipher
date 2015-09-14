package net.sqlcipher.database;

/**
 * Allows optional callback hooks around when SQLCipher database is setup with
 * encryption key
 * 
 * @author jayal
 */
public interface SQLiteDatabaseHook {
    void preKey(SQLiteDatabase database);

    void postKey(SQLiteDatabase database);
}
