package com.lapism.searchview.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.migration.Migration;
import android.content.Context;
import android.support.annotation.NonNull;
import cz.seznam.zpravy.BuildConfig;
import cz.seznam.zpravy.sreporter.entity.ReportMedia;
import cz.seznam.zpravy.sreporter.record.Report;
import cz.seznam.zpravy.util.CommentPrefs;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Trida pro ovladani room databaze
 */
@Database(entities = {Report.class, ReportMedia.class}, version = BuildConfig.VERSION_CODE, exportSchema = false)
public abstract class ReportDatabase extends RoomDatabase {

    private static ReportDatabase sDb;
    private static AtomicInteger instances = new AtomicInteger(0);

    /**
     * @param context kontext
     * @return intance databaze pro zapis a cteni
     */
    public static ReportDatabase getDatabase(Context context) {
        if (sDb == null || !sDb.isOpen()) {
            Context c = context.getApplicationContext();
            sDb = Room.databaseBuilder(c,
                    ReportDatabase.class, "reportDatabase").addMigrations(getDefaultMigration(c)
                    /*Tady staci pridavat prislusne migrace, pokud nejake budou. Defaultni muze zustat, pripadne ji to overridne*/
            ).fallbackToDestructiveMigration().build();
        }
        instances.incrementAndGet();
        return sDb;
    }

    /**
     * @param context kontext
     * @return objekt migrace mezi verzemi
     */
    private static Migration getDefaultMigration(final Context context) {
        int oldCode = CommentPrefs.getInstance(context).get(CommentPrefs.ROOM_VERSION, 1);
        final int newCode = BuildConfig.VERSION_CODE;

        return new Migration(oldCode, newCode) {
            @Override
            public void migrate(@NonNull SupportSQLiteDatabase database) {
                CommentPrefs.getInstance(context).put(CommentPrefs.ROOM_VERSION, newCode);
            }
        };
    }

    /**
     * @return dao pro ovladani tabulky reportu
     */
    public abstract ReportDao reportDao();

    /**
     * @return dao pro ovladani tabulky reportu
     */
    public abstract ReportMediaDao mediaDao();

    /**
     * Zavira databazi
     */
    @Override
    public void close() {
        if (instances.decrementAndGet() == 0) {
            super.close();
            sDb.close();
            sDb = null;
        }
    }
}