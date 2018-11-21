package com.lapism.searchview.database;

import android.arch.persistence.room.*;
import cz.seznam.zpravy.sreporter.entity.ReportMedia;
import io.reactivex.Maybe;
import io.reactivex.Single;

import java.util.List;

/**
 * Interface pro ovladani tabulky komentu
 * Doba vyvoreni se pouziva jako unikatni id
 */
@Dao
public interface ReportMediaDao {

    /**
     * @return vsechny media z db obalene do {@link Maybe}
     */
    @Transaction
    @Query("SELECT * FROM reportmedias")
    Maybe<List<ReportMedia>> getAll();

    /**
     * @param id
     * @return media s konkretnim id obaleny do {@link Single}
     */
    @Transaction
    @Query("SELECT * FROM reportmedias WHERE id = :id")
    Maybe<ReportMedia> getReportMedia(String id);

    /**
     * @param creation
     * @return media s konkretni dobou vytvoreni obaleny do {@link Single}
     */
    @Transaction
    @Query("SELECT * FROM reportmedias WHERE creation = :creation")
    Single<ReportMedia> getReportMedia(Long creation);

    /**
     * Updatuje zaznam media v databazi
     *
     * @param media media k updatovani
     */
    @Transaction
    @Update
    void update(ReportMedia media);

    /**
     * Vlozi medias do databaze
     *
     * @param medias - vararg medias k vlozeni
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(ReportMedia... medias);

    /**
     * Smaze report
     *
     * @param media - report ke smazani
     */
    @Transaction
    @Delete
    void delete(ReportMedia media);

    /**
     * Dropne vsechny reporty z db
     */
    @Transaction
    @Query("DELETE FROM reportMedias")
    void deleteAll();
}
