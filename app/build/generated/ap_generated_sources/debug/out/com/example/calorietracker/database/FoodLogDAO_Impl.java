package com.example.calorietracker.database;

import androidx.annotation.NonNull;
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
public final class FoodLogDAO_Impl implements FoodLogDAO {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<FoodLog> __insertAdapterOfFoodLog;

  public FoodLogDAO_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfFoodLog = new EntityInsertAdapter<FoodLog>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `food_logs` (`id`,`userId`,`foodName`,`calories`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final FoodLog entity) {
        statement.bindLong(1, entity.id);
        statement.bindLong(2, entity.userId);
        if (entity.foodName == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.foodName);
        }
        statement.bindLong(4, entity.calories);
      }
    };
  }

  @Override
  public void insert(final FoodLog food) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __insertAdapterOfFoodLog.insert(_connection, food);
      return null;
    });
  }

  @Override
  public List<FoodLog> getFoodsForUser(final int userId) {
    final String _sql = "SELECT * FROM food_logs WHERE userId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfFoodName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "foodName");
        final int _columnIndexOfCalories = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "calories");
        final List<FoodLog> _result = new ArrayList<FoodLog>();
        while (_stmt.step()) {
          final FoodLog _item;
          final int _tmpUserId;
          _tmpUserId = (int) (_stmt.getLong(_columnIndexOfUserId));
          final String _tmpFoodName;
          if (_stmt.isNull(_columnIndexOfFoodName)) {
            _tmpFoodName = null;
          } else {
            _tmpFoodName = _stmt.getText(_columnIndexOfFoodName);
          }
          final int _tmpCalories;
          _tmpCalories = (int) (_stmt.getLong(_columnIndexOfCalories));
          _item = new FoodLog(_tmpUserId,_tmpFoodName,_tmpCalories);
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
  public void clearFoodsForUser(final int userId) {
    final String _sql = "DELETE FROM food_logs WHERE userId = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, userId);
        _stmt.step();
        return null;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public void deleteFoodItem(final int id) {
    final String _sql = "DELETE FROM food_logs WHERE id = ?";
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        _stmt.step();
        return null;
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
