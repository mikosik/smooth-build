package org.smoothbuild.object;

import javax.inject.Inject;

import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.plugin.Value;

import com.google.common.hash.HashCode;

public class ResultDb {
  private static final int FILE_SET_FLAG = 1;
  private static final int STRING_SET_FLAG = 2;
  private static final int FILE_FLAG = 3;
  private static final int STRING_FLAG = 4;

  private final HashedDb taskToResultsDb;
  private final ValueDb valueDb;

  @Inject
  public ResultDb(@HashedDbWithResults HashedDb taskToResultsDb, ValueDb valueDb) {
    this.taskToResultsDb = taskToResultsDb;
    this.valueDb = valueDb;
  }

  public void store(HashCode taskHash, Value value) {
    // TODO remove once null values are forbidden
    if (value == null) {
      return;
    }

    Marshaller marshaller = new Marshaller();
    marshaller.addInt(flagFor(value));
    marshaller.addHash(value.hash());

    taskToResultsDb.store(taskHash, marshaller.getBytes());
  }

  public boolean contains(HashCode taskHash) {
    return taskToResultsDb.contains(taskHash);
  }

  public Value read(HashCode taskHash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(taskToResultsDb, taskHash);) {
      int flag = unmarshaller.readInt();
      HashCode resultObjectHash = unmarshaller.readHash();
      return readValue(flag, resultObjectHash);
    }
  }

  private Value readValue(int flag, HashCode resultObjectHash) {
    switch (flag) {
      case FILE_SET_FLAG:
        return valueDb.fileSet(resultObjectHash);
      case STRING_SET_FLAG:
        return valueDb.stringSet(resultObjectHash);
      case FILE_FLAG:
        return valueDb.file(resultObjectHash);
      case STRING_FLAG:
        return valueDb.string(resultObjectHash);
      default:
        throw new RuntimeException("Internal error in smooth binary. Unknown value flag = " + flag);
    }
  }

  private static int flagFor(Value value) {
    if (value instanceof FileSet) {
      return FILE_SET_FLAG;
    }
    if (value instanceof StringSet) {
      return STRING_SET_FLAG;
    }
    if (value instanceof File) {
      return FILE_FLAG;
    }
    if (value instanceof StringValue) {
      return STRING_FLAG;
    }
    throw new RuntimeException("Internal error in smooth binary. Unknown value type = "
        + value.getClass().getName());
  }
}
