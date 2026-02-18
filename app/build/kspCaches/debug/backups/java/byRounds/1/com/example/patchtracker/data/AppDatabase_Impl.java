package com.example.patchtracker.data;

import androidx.annotation.NonNull;
import androidx.room.DatabaseConfiguration;
import androidx.room.InvalidationTracker;
import androidx.room.RoomDatabase;
import androidx.room.RoomOpenHelper;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.SupportSQLiteOpenHelper;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile DoseDao _doseDao;

  private volatile LogDao _logDao;

  @Override
  @NonNull
  protected SupportSQLiteOpenHelper createOpenHelper(@NonNull final DatabaseConfiguration config) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(config, new RoomOpenHelper.Delegate(2) {
      @Override
      public void createAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `dose_records` (`id` TEXT NOT NULL, `createdAtMillis` INTEGER NOT NULL, `clicks` INTEGER NOT NULL, `concentration` INTEGER NOT NULL, `totalUnits` REAL NOT NULL, `insulinName` TEXT NOT NULL, `uploadStatus` TEXT NOT NULL, `lastError` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS `log_entries` (`id` TEXT NOT NULL, `timestampMillis` INTEGER NOT NULL, `level` TEXT NOT NULL, `message` TEXT NOT NULL, `details` TEXT, PRIMARY KEY(`id`))");
        db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '0db8d863b8a9c364cc6d925b43b0bcf3')");
      }

      @Override
      public void dropAllTables(@NonNull final SupportSQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS `dose_records`");
        db.execSQL("DROP TABLE IF EXISTS `log_entries`");
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onDestructiveMigration(db);
          }
        }
      }

      @Override
      public void onCreate(@NonNull final SupportSQLiteDatabase db) {
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onCreate(db);
          }
        }
      }

      @Override
      public void onOpen(@NonNull final SupportSQLiteDatabase db) {
        mDatabase = db;
        internalInitInvalidationTracker(db);
        final List<? extends RoomDatabase.Callback> _callbacks = mCallbacks;
        if (_callbacks != null) {
          for (RoomDatabase.Callback _callback : _callbacks) {
            _callback.onOpen(db);
          }
        }
      }

      @Override
      public void onPreMigrate(@NonNull final SupportSQLiteDatabase db) {
        DBUtil.dropFtsSyncTriggers(db);
      }

      @Override
      public void onPostMigrate(@NonNull final SupportSQLiteDatabase db) {
      }

      @Override
      @NonNull
      public RoomOpenHelper.ValidationResult onValidateSchema(
          @NonNull final SupportSQLiteDatabase db) {
        final HashMap<String, TableInfo.Column> _columnsDoseRecords = new HashMap<String, TableInfo.Column>(8);
        _columnsDoseRecords.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDoseRecords.put("createdAtMillis", new TableInfo.Column("createdAtMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDoseRecords.put("clicks", new TableInfo.Column("clicks", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDoseRecords.put("concentration", new TableInfo.Column("concentration", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDoseRecords.put("totalUnits", new TableInfo.Column("totalUnits", "REAL", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDoseRecords.put("insulinName", new TableInfo.Column("insulinName", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDoseRecords.put("uploadStatus", new TableInfo.Column("uploadStatus", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsDoseRecords.put("lastError", new TableInfo.Column("lastError", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysDoseRecords = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesDoseRecords = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoDoseRecords = new TableInfo("dose_records", _columnsDoseRecords, _foreignKeysDoseRecords, _indicesDoseRecords);
        final TableInfo _existingDoseRecords = TableInfo.read(db, "dose_records");
        if (!_infoDoseRecords.equals(_existingDoseRecords)) {
          return new RoomOpenHelper.ValidationResult(false, "dose_records(com.example.patchtracker.data.DoseRecord).\n"
                  + " Expected:\n" + _infoDoseRecords + "\n"
                  + " Found:\n" + _existingDoseRecords);
        }
        final HashMap<String, TableInfo.Column> _columnsLogEntries = new HashMap<String, TableInfo.Column>(5);
        _columnsLogEntries.put("id", new TableInfo.Column("id", "TEXT", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLogEntries.put("timestampMillis", new TableInfo.Column("timestampMillis", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLogEntries.put("level", new TableInfo.Column("level", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLogEntries.put("message", new TableInfo.Column("message", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsLogEntries.put("details", new TableInfo.Column("details", "TEXT", false, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final HashSet<TableInfo.ForeignKey> _foreignKeysLogEntries = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesLogEntries = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoLogEntries = new TableInfo("log_entries", _columnsLogEntries, _foreignKeysLogEntries, _indicesLogEntries);
        final TableInfo _existingLogEntries = TableInfo.read(db, "log_entries");
        if (!_infoLogEntries.equals(_existingLogEntries)) {
          return new RoomOpenHelper.ValidationResult(false, "log_entries(com.example.patchtracker.data.LogEntry).\n"
                  + " Expected:\n" + _infoLogEntries + "\n"
                  + " Found:\n" + _existingLogEntries);
        }
        return new RoomOpenHelper.ValidationResult(true, null);
      }
    }, "0db8d863b8a9c364cc6d925b43b0bcf3", "040757e85af2eb1138d737e944b15031");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(config.context).name(config.name).callback(_openCallback).build();
    final SupportSQLiteOpenHelper _helper = config.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final HashMap<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final HashMap<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "dose_records","log_entries");
  }

  @Override
  public void clearAllTables() {
    super.assertNotMainThread();
    final SupportSQLiteDatabase _db = super.getOpenHelper().getWritableDatabase();
    try {
      super.beginTransaction();
      _db.execSQL("DELETE FROM `dose_records`");
      _db.execSQL("DELETE FROM `log_entries`");
      super.setTransactionSuccessful();
    } finally {
      super.endTransaction();
      _db.query("PRAGMA wal_checkpoint(FULL)").close();
      if (!_db.inTransaction()) {
        _db.execSQL("VACUUM");
      }
    }
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final HashMap<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(DoseDao.class, DoseDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(LogDao.class, LogDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final HashSet<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public DoseDao doseDao() {
    if (_doseDao != null) {
      return _doseDao;
    } else {
      synchronized(this) {
        if(_doseDao == null) {
          _doseDao = new DoseDao_Impl(this);
        }
        return _doseDao;
      }
    }
  }

  @Override
  public LogDao logDao() {
    if (_logDao != null) {
      return _logDao;
    } else {
      synchronized(this) {
        if(_logDao == null) {
          _logDao = new LogDao_Impl(this);
        }
        return _logDao;
      }
    }
  }
}
