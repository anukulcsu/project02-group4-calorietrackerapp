package com.example.calorietracker.database;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
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
public final class UserDAO_Impl implements UserDAO {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<User> __insertAdapterOfUser;

  private final EntityDeleteOrUpdateAdapter<User> __deleteAdapterOfUser;

  private final EntityDeleteOrUpdateAdapter<User> __updateAdapterOfUser;

  public UserDAO_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfUser = new EntityInsertAdapter<User>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `users` (`userId`,`username`,`password`,`isAdmin`) VALUES (nullif(?, 0),?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final User entity) {
        statement.bindLong(1, entity.userId);
        if (entity.username == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.username);
        }
        if (entity.password == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.password);
        }
        final int _tmp = entity.isAdmin ? 1 : 0;
        statement.bindLong(4, _tmp);
      }
    };
    this.__deleteAdapterOfUser = new EntityDeleteOrUpdateAdapter<User>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `users` WHERE `userId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final User entity) {
        statement.bindLong(1, entity.userId);
      }
    };
    this.__updateAdapterOfUser = new EntityDeleteOrUpdateAdapter<User>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `users` SET `userId` = ?,`username` = ?,`password` = ?,`isAdmin` = ? WHERE `userId` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement, final User entity) {
        statement.bindLong(1, entity.userId);
        if (entity.username == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.username);
        }
        if (entity.password == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.password);
        }
        final int _tmp = entity.isAdmin ? 1 : 0;
        statement.bindLong(4, _tmp);
        statement.bindLong(5, entity.userId);
      }
    };
  }

  @Override
  public void insert(final User users) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __insertAdapterOfUser.insert(_connection, users);
      return null;
    });
  }

  @Override
  public void deleteUser(final User user) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __deleteAdapterOfUser.handle(_connection, user);
      return null;
    });
  }

  @Override
  public void updateUser(final User user) {
    DBUtil.performBlocking(__db, false, true, (_connection) -> {
      __updateAdapterOfUser.handle(_connection, user);
      return null;
    });
  }

  @Override
  public User getUserByUsername(final String username) {
    final String _sql = "SELECT * FROM users WHERE username = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (username == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, username);
        }
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfUsername = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "username");
        final int _columnIndexOfPassword = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "password");
        final int _columnIndexOfIsAdmin = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isAdmin");
        final User _result;
        if (_stmt.step()) {
          _result = new User();
          _result.userId = (int) (_stmt.getLong(_columnIndexOfUserId));
          if (_stmt.isNull(_columnIndexOfUsername)) {
            _result.username = null;
          } else {
            _result.username = _stmt.getText(_columnIndexOfUsername);
          }
          if (_stmt.isNull(_columnIndexOfPassword)) {
            _result.password = null;
          } else {
            _result.password = _stmt.getText(_columnIndexOfPassword);
          }
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsAdmin));
          _result.isAdmin = _tmp != 0;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public User getUserById(final String userId) {
    final String _sql = "SELECT * FROM users WHERE userId = ?";
    return DBUtil.performBlocking(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        if (userId == null) {
          _stmt.bindNull(_argIndex);
        } else {
          _stmt.bindText(_argIndex, userId);
        }
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfUsername = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "username");
        final int _columnIndexOfPassword = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "password");
        final int _columnIndexOfIsAdmin = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isAdmin");
        final User _result;
        if (_stmt.step()) {
          _result = new User();
          _result.userId = (int) (_stmt.getLong(_columnIndexOfUserId));
          if (_stmt.isNull(_columnIndexOfUsername)) {
            _result.username = null;
          } else {
            _result.username = _stmt.getText(_columnIndexOfUsername);
          }
          if (_stmt.isNull(_columnIndexOfPassword)) {
            _result.password = null;
          } else {
            _result.password = _stmt.getText(_columnIndexOfPassword);
          }
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsAdmin));
          _result.isAdmin = _tmp != 0;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public LiveData<List<User>> getAllUsers() {
    final String _sql = "SELECT * FROM users";
    return __db.getInvalidationTracker().createLiveData(new String[] {"users"}, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfUserId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "userId");
        final int _columnIndexOfUsername = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "username");
        final int _columnIndexOfPassword = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "password");
        final int _columnIndexOfIsAdmin = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "isAdmin");
        final List<User> _result = new ArrayList<User>();
        while (_stmt.step()) {
          final User _item;
          _item = new User();
          _item.userId = (int) (_stmt.getLong(_columnIndexOfUserId));
          if (_stmt.isNull(_columnIndexOfUsername)) {
            _item.username = null;
          } else {
            _item.username = _stmt.getText(_columnIndexOfUsername);
          }
          if (_stmt.isNull(_columnIndexOfPassword)) {
            _item.password = null;
          } else {
            _item.password = _stmt.getText(_columnIndexOfPassword);
          }
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfIsAdmin));
          _item.isAdmin = _tmp != 0;
          _result.add(_item);
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
