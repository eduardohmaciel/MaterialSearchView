package com.lapism.searchview.database;

import android.arch.persistence.room.*;
import cz.seznam.zpravy.sreporter.entity.ReportWithMedia;
import cz.seznam.zpravy.sreporter.record.Report;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.Single;

import java.util.List;

/**
 * Interface pro ovladani tabulky komentu
 * Doba vyvoreni se pouziva jako unikatni id
 */
@Dao
public interface ReportDao {

    /**
     * @return vsechny komenty z db obalene do {@link Maybe}
     */
    @Transaction
    @Query("SELECT * FROM reports")
    Maybe<List<ReportWithMedia>> getAll();

    /**
     * @return vsechny komenty z db obalene do {@link Maybe}
     */
    @Transaction
    @Query("SELECT * FROM reports")
    Flowable<ReportWithMedia> getAllFlowable();

    /**
     * @param id
     * @return koment s konkretni dobou vytvoreni obaleny do {@link Flowable}
     */
    @Transaction
    @Query("SELECT * FROM reports WHERE id = :id")
    Flowable<ReportWithMedia> getReportLive(String id);


    /**
     * @param creation
     * @return report s konkretni dobou vytvoreni obaleny do {@link Single}
     */
    @Transaction
    @Query("SELECT * FROM reports WHERE creation = :creation")
    Single<ReportWithMedia> getReport(Long creation);


    /**
     * @param id
     * @return report s konkretni dobou vytvoreni obaleny do {@link Single}
     */
    @Transaction
    @Query("SELECT * FROM reports WHERE id = :id")
    Single<ReportWithMedia> getReport(String id);

    /**
     * Updatuje zaznam report v databazi
     *
     * @param report report k updatovani
     */
    @Transaction
    @Update(onConflict = OnConflictStrategy.REPLACE)
    void update(Report report);

    /**
     * Vlozi reporty do databaze
     *
     * @param reports - vararg reportu k vlozeni
     */
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Report... reports);

    /**
     * Smaze report
     *
     * @param report - report ke smazani
     */
    @Transaction
    @Delete
    void delete(Report report);

    /**
     * Dropne vsechny reporty z db
     */
    @Transaction
    @Query("DELETE FROM reports")
    void deleteAll();
}
