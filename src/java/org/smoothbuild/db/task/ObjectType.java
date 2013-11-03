package org.smoothbuild.db.task;

import org.smoothbuild.db.value.ValueDb;
import org.smoothbuild.plugin.Value;

import com.google.common.hash.HashCode;

public enum ObjectType {
  STRING_TYPE {
    @Override
    public Value readFrom(ValueDb valueDb, HashCode hash) {
      return valueDb.string(hash);
    }
  },
  STRING_SET_TYPE {
    @Override
    public Value readFrom(ValueDb valueDb, HashCode hash) {
      return valueDb.stringSet(hash);
    }
  },
  FILE_TYPE {
    @Override
    public Value readFrom(ValueDb valueDb, HashCode hash) {
      return valueDb.file(hash);
    }
  },
  FILE_SET_TYPE {
    @Override
    public Value readFrom(ValueDb valueDb, HashCode hash) {
      return valueDb.fileSet(hash);
    }
  };

  public abstract Value readFrom(ValueDb valueDb, HashCode hash);
}
