package com.appacts.plugin.Data.Interfaces;

import java.util.UUID;

import android.database.sqlite.SQLiteDatabase;

import com.appacts.plugin.Models.ExceptionDatabaseLayer;

public interface IData {
    void Create() throws ExceptionDatabaseLayer;
    void Setup(UUID applicationId) throws ExceptionDatabaseLayer;
    boolean Exists();
    SQLiteDatabase OpenReadWriteConnection() throws ExceptionDatabaseLayer;
    void CloseReadWriteConnection() throws ExceptionDatabaseLayer;
    SQLiteDatabase OpenReadOnlyConnection() throws ExceptionDatabaseLayer;
    void CloseReadOnlyConnection() throws ExceptionDatabaseLayer;
    void Dispose();
    boolean UpgradeSchema(int pluginVersionNumericCurrent, int schemaVersionNumericOld) 
    		throws ExceptionDatabaseLayer;
}