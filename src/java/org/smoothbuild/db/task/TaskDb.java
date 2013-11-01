package org.smoothbuild.db.task;

import javax.inject.Inject;

import org.smoothbuild.db.hash.HashedDb;
import org.smoothbuild.db.hash.HashedDbWithTasks;
import org.smoothbuild.db.hash.Marshaller;
import org.smoothbuild.db.hash.Unmarshaller;
import org.smoothbuild.db.value.ValueDb;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.plugin.Value;

import com.google.common.hash.HashCode;

public class TaskDb {
  private static final int FILE_SET_FLAG = 1;
  private static final int STRING_SET_FLAG = 2;
  private static final int FILE_FLAG = 3;
  private static final int STRING_FLAG = 4;

  private final HashedDb taskResultsDb;
  private final ValueDb valueDb;

  @Inject
  public TaskDb(@HashedDbWithTasks HashedDb taskResultsDb, ValueDb valueDb) {
    this.taskResultsDb = taskResultsDb;
    this.valueDb = valueDb;
  }

  public void store(HashCode taskHash, Value value) {
    Marshaller marshaller = new Marshaller();
    marshaller.addInt(flagFor(value));
    marshaller.addHash(value.hash());

    taskResultsDb.store(taskHash, marshaller.getBytes());
  }

  public boolean contains(HashCode taskHash) {
    return taskResultsDb.contains(taskHash);
  }

  public Value read(HashCode taskHash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(taskResultsDb, taskHash);) {
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
