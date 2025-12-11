package com.example.calorietracker.database;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class CalorieHistoryDAO_Impl implements CalorieHistoryDAO {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<CalorieHistory> __insertAdapterOfCalorieHistory;

  private final EntityDeleteOrUpdateAdapter<CalorieHistory> __deleteAdapterOfCalorieHistory;

  private final EntityDeleteOrUpdateAdapter<CalorieHistory> __updateAdapterOfCalorieHistory;

  public CalorieHistoryDAO_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfCalorieHistory = new EntityInsertAdapter<CalorieHistory>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `calorie_history` (`id`,`userId`,`date`,`totalCalories`,`targetCalories`) VALUES (nullif(?, 0),?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final CalorieHistory entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.userId);
        if (entity.date == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.date);
        }
        statement.bindLong(4, entity.totalCalories);
        statement.bindLong(5, entity.targetCalories);
      }
    };
    this.__deleteAdapterOfCalorieHistory = new EntityDeleteOrUpdateAdapter<CalorieHistory>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `calorie_history` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final CalorieHistory entity) {
        statement.bindLong(1, entity.id);
      }
    };
    this.__updateAdapterOfCalorieHistory = new EntityDeleteOrUpdateAdapter<CalorieHistory>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `calorie_history` SET `id` = ?,`userId` = ?,`date` = ?,`totalCalories` = ?,`targetCalories` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final CalorieHistory entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.userId);
        if (entity.date == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.date);
        }
        statement.bindLong(4, entity.totalCalories);
        statement.bindLong(5, entity.targetCalories);
        statement.bindLong(6, entity.id);
      }
    };
  }

  @Override
  public void insert(final CalorieHistory history) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __insertAdapterOfCalorieHistory.insert(_connection, history);
      return null;
    });
  }

  @Override
  public void delete(final CalorieHistory history) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfCalorieHistory.handle(_connection, history);
      return null;
    });
  }

  @Override
  public void update(final CalorieHistory history) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfCalorieHistory.handle(_connection, history);
      return null;
    });
  }

  @Override
  public List<CalorieHistory> getHistoryForUser(final int userId) {
    final String _sql = "SELECT * FROM calorie_history WHERE userId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "date");
        final int _columnIndexOfTotalCalories = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalCalories");
        final int _columnIndexOfTargetCalories = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "targetCalories");
        final List<CalorieHistory> _result = new ArrayList<CalorieHistory>();
        while (_stmt.step()) {
          final CalorieHistory _item;
          final int _tmpUserId;
          _tmpUserId = (int) (_stmt.getLong(_columnIndexOfUserId));
          final String _tmpDate;
          if (_stmt.isNull(_columnIndexOfDate)) {
            _tmpDate = null;
          } else {
            _tmpDate = _stmt.getText(_columnIndexOfDate);
          }
          final int _tmpTotalCalories;
          _tmpTotalCalories = (int) (_stmt.getLong(_columnIndexOfTotalCalories));
          final int _tmpTargetCalories;
          _tmpTargetCalories = (int) (_stmt.getLong(_columnIndexOfTargetCalories));
          _item = new CalorieHistory(_tmpUserId,_tmpDate,_tmpTotalCalories,_tmpTargetCalories);
          _item.id = (int) (_stmt.getLong(_columnIndexOfId));
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public List<String> getAllDates(final int userId) {
    final String _sql = "SELECT DISTINCT date FROM calorie_history WHERE userId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
        final List<String> _result = new ArrayList<String>();
        while (_stmt.step()) {
          final String _item;
          if (_stmt.isNull(0)) {
            _item = null;
          } else {
            _item = _stmt.getText(0);
          }
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public CalorieHistory getHistoryByDate(final int userId, final String date) {
    final String _sql = "SELECT * FROM calorie_history WHERE userId = ? AND date = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
        _argIndex = 2;
        if (date == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, date);
        }
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfDate = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "date");
        final int _columnIndexOfTotalCalories = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "totalCalories");
        final int _columnIndexOfTargetCalories = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "targetCalories");
        final CalorieHistory _result;
        if (_stmt.step()) {
          final int _tmpUserId;
          _tmpUserId = (int) (_stmt.getLong(_columnIndexOfUserId));
          final String _tmpDate;
          if (_stmt.isNull(_columnIndexOfDate)) {
            _tmpDate = null;
          } else {
            _tmpDate = _stmt.getText(_columnIndexOfDate);
          }
          final int _tmpTotalCalories;
          _tmpTotalCalories = (int) (_stmt.getLong(_columnIndexOfTotalCalories));
          final int _tmpTargetCalories;
          _tmpTargetCalories = (int) (_stmt.getLong(_columnIndexOfTargetCalories));
          _result = new CalorieHistory(_tmpUserId,_tmpDate,_tmpTotalCalories,_tmpTargetCalories);
          _result.id = (int) (_stmt.getLong(_columnIndexOfId));
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
