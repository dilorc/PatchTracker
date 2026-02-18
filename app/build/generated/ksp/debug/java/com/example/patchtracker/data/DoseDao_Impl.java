package com.example.patchtracker.data;

import android.database.Cursor;
import android.os.CancellationSignal;
import androidx.annotation.NonNull;
import androidx.room.CoroutinesRoom;
import androidx.room.EntityInsertionAdapter;
import androidx.room.RoomDatabase;
import androidx.room.RoomSQLiteQuery;
import androidx.room.SharedSQLiteStatement;
import androidx.room.util.CursorUtil;
import androidx.room.util.DBUtil;
import androidx.sqlite.db.SupportSQLiteStatement;
import java.lang.Class;
import java.lang.Exception;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation"})
public final class DoseDao_Impl implements DoseDao {
  private final RoomDatabase __db;

  private final EntityInsertionAdapter<DoseRecord> __insertionAdapterOfDoseRecord;

  private final Converters __converters = new Converters();

  private final SharedSQLiteStatement __preparedStmtOfMarkUploaded;

  private final SharedSQLiteStatement __preparedStmtOfMarkFailed;

  private final SharedSQLiteStatement __preparedStmtOfDeleteAll;

  public DoseDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertionAdapterOfDoseRecord = new EntityInsertionAdapter<DoseRecord>(__db) {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `dose_records` (`id`,`createdAtMillis`,`clicks`,`concentration`,`totalUnits`,`insulinName`,`uploadStatus`,`lastError`) VALUES (?,?,?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SupportSQLiteStatement statement,
          @NonNull final DoseRecord entity) {
        statement.bindString(1, entity.getId());
        statement.bindLong(2, entity.getCreatedAtMillis());
        statement.bindLong(3, entity.getClicks());
        final int _tmp = __converters.fromConcentration(entity.getConcentration());
        statement.bindLong(4, _tmp);
        statement.bindDouble(5, entity.getTotalUnits());
        statement.bindString(6, entity.getInsulinName());
        final String _tmp_1 = __converters.fromUploadStatus(entity.getUploadStatus());
        statement.bindString(7, _tmp_1);
        if (entity.getLastError() == null) {
          statement.bindNull(8);
        } else {
          statement.bindString(8, entity.getLastError());
        }
      }
    };
    this.__preparedStmtOfMarkUploaded = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE dose_records SET uploadStatus = 'UPLOADED', lastError = NULL WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfMarkFailed = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "UPDATE dose_records SET uploadStatus = 'FAILED', lastError = ? WHERE id = ?";
        return _query;
      }
    };
    this.__preparedStmtOfDeleteAll = new SharedSQLiteStatement(__db) {
      @Override
      @NonNull
      public String createQuery() {
        final String _query = "DELETE FROM dose_records";
        return _query;
      }
    };
  }

  @Override
  public Object insert(final DoseRecord doseRecord, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        __db.beginTransaction();
        try {
          __insertionAdapterOfDoseRecord.insert(doseRecord);
          __db.setTransactionSuccessful();
          return Unit.INSTANCE;
        } finally {
          __db.endTransaction();
        }
      }
    }, $completion);
  }

  @Override
  public Object markUploaded(final String id, final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkUploaded.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkUploaded.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object markFailed(final String id, final String error,
      final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfMarkFailed.acquire();
        int _argIndex = 1;
        _stmt.bindString(_argIndex, error);
        _argIndex = 2;
        _stmt.bindString(_argIndex, id);
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfMarkFailed.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Object deleteAll(final Continuation<? super Unit> $completion) {
    return CoroutinesRoom.execute(__db, true, new Callable<Unit>() {
      @Override
      @NonNull
      public Unit call() throws Exception {
        final SupportSQLiteStatement _stmt = __preparedStmtOfDeleteAll.acquire();
        try {
          __db.beginTransaction();
          try {
            _stmt.executeUpdateDelete();
            __db.setTransactionSuccessful();
            return Unit.INSTANCE;
          } finally {
            __db.endTransaction();
          }
        } finally {
          __preparedStmtOfDeleteAll.release(_stmt);
        }
      }
    }, $completion);
  }

  @Override
  public Flow<List<DoseRecord>> listRecent() {
    final String _sql = "SELECT * FROM dose_records ORDER BY createdAtMillis DESC";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 0);
    return CoroutinesRoom.createFlow(__db, false, new String[] {"dose_records"}, new Callable<List<DoseRecord>>() {
      @Override
      @NonNull
      public List<DoseRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final int _cursorIndexOfClicks = CursorUtil.getColumnIndexOrThrow(_cursor, "clicks");
          final int _cursorIndexOfConcentration = CursorUtil.getColumnIndexOrThrow(_cursor, "concentration");
          final int _cursorIndexOfTotalUnits = CursorUtil.getColumnIndexOrThrow(_cursor, "totalUnits");
          final int _cursorIndexOfInsulinName = CursorUtil.getColumnIndexOrThrow(_cursor, "insulinName");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadStatus");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final List<DoseRecord> _result = new ArrayList<DoseRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DoseRecord _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            final int _tmpClicks;
            _tmpClicks = _cursor.getInt(_cursorIndexOfClicks);
            final Concentration _tmpConcentration;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfConcentration);
            _tmpConcentration = __converters.toConcentration(_tmp);
            final double _tmpTotalUnits;
            _tmpTotalUnits = _cursor.getDouble(_cursorIndexOfTotalUnits);
            final String _tmpInsulinName;
            _tmpInsulinName = _cursor.getString(_cursorIndexOfInsulinName);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfUploadStatus);
            _tmpUploadStatus = __converters.toUploadStatus(_tmp_1);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            _item = new DoseRecord(_tmpId,_tmpCreatedAtMillis,_tmpClicks,_tmpConcentration,_tmpTotalUnits,_tmpInsulinName,_tmpUploadStatus,_tmpLastError);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
        }
      }

      @Override
      protected void finalize() {
        _statement.release();
      }
    });
  }

  @Override
  public Object getPending(final int limit,
      final Continuation<? super List<DoseRecord>> $completion) {
    final String _sql = "SELECT * FROM dose_records WHERE uploadStatus = 'PENDING' ORDER BY createdAtMillis ASC LIMIT ?";
    final RoomSQLiteQuery _statement = RoomSQLiteQuery.acquire(_sql, 1);
    int _argIndex = 1;
    _statement.bindLong(_argIndex, limit);
    final CancellationSignal _cancellationSignal = DBUtil.createCancellationSignal();
    return CoroutinesRoom.execute(__db, false, _cancellationSignal, new Callable<List<DoseRecord>>() {
      @Override
      @NonNull
      public List<DoseRecord> call() throws Exception {
        final Cursor _cursor = DBUtil.query(__db, _statement, false, null);
        try {
          final int _cursorIndexOfId = CursorUtil.getColumnIndexOrThrow(_cursor, "id");
          final int _cursorIndexOfCreatedAtMillis = CursorUtil.getColumnIndexOrThrow(_cursor, "createdAtMillis");
          final int _cursorIndexOfClicks = CursorUtil.getColumnIndexOrThrow(_cursor, "clicks");
          final int _cursorIndexOfConcentration = CursorUtil.getColumnIndexOrThrow(_cursor, "concentration");
          final int _cursorIndexOfTotalUnits = CursorUtil.getColumnIndexOrThrow(_cursor, "totalUnits");
          final int _cursorIndexOfInsulinName = CursorUtil.getColumnIndexOrThrow(_cursor, "insulinName");
          final int _cursorIndexOfUploadStatus = CursorUtil.getColumnIndexOrThrow(_cursor, "uploadStatus");
          final int _cursorIndexOfLastError = CursorUtil.getColumnIndexOrThrow(_cursor, "lastError");
          final List<DoseRecord> _result = new ArrayList<DoseRecord>(_cursor.getCount());
          while (_cursor.moveToNext()) {
            final DoseRecord _item;
            final String _tmpId;
            _tmpId = _cursor.getString(_cursorIndexOfId);
            final long _tmpCreatedAtMillis;
            _tmpCreatedAtMillis = _cursor.getLong(_cursorIndexOfCreatedAtMillis);
            final int _tmpClicks;
            _tmpClicks = _cursor.getInt(_cursorIndexOfClicks);
            final Concentration _tmpConcentration;
            final int _tmp;
            _tmp = _cursor.getInt(_cursorIndexOfConcentration);
            _tmpConcentration = __converters.toConcentration(_tmp);
            final double _tmpTotalUnits;
            _tmpTotalUnits = _cursor.getDouble(_cursorIndexOfTotalUnits);
            final String _tmpInsulinName;
            _tmpInsulinName = _cursor.getString(_cursorIndexOfInsulinName);
            final UploadStatus _tmpUploadStatus;
            final String _tmp_1;
            _tmp_1 = _cursor.getString(_cursorIndexOfUploadStatus);
            _tmpUploadStatus = __converters.toUploadStatus(_tmp_1);
            final String _tmpLastError;
            if (_cursor.isNull(_cursorIndexOfLastError)) {
              _tmpLastError = null;
            } else {
              _tmpLastError = _cursor.getString(_cursorIndexOfLastError);
            }
            _item = new DoseRecord(_tmpId,_tmpCreatedAtMillis,_tmpClicks,_tmpConcentration,_tmpTotalUnits,_tmpInsulinName,_tmpUploadStatus,_tmpLastError);
            _result.add(_item);
          }
          return _result;
        } finally {
          _cursor.close();
          _statement.release();
        }
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
