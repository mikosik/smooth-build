package org.smoothbuild.db.task;

import static com.google.common.collect.Lists.newArrayList;
import static org.smoothbuild.message.message.MessageType.ERROR;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.hash.HashedDb;
import org.smoothbuild.db.hash.HashedDbWithTasks;
import org.smoothbuild.db.hash.Marshaller;
import org.smoothbuild.db.hash.Unmarshaller;
import org.smoothbuild.db.value.ValueDb;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.MessageType;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.FileSet;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.plugin.Value;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class TaskDb {
  private static final byte FILE_SET_FLAG = 1;
  private static final byte STRING_SET_FLAG = 2;
  private static final byte FILE_FLAG = 3;
  private static final byte STRING_FLAG = 4;

  private final HashedDb taskResultsDb;
  private final ValueDb valueDb;

  @Inject
  public TaskDb(@HashedDbWithTasks HashedDb taskResultsDb, ValueDb valueDb) {
    this.taskResultsDb = taskResultsDb;
    this.valueDb = valueDb;
  }

  public void store(HashCode taskHash, CachedResult cachedResult) {
    Marshaller marshaller = new Marshaller();

    Value value = cachedResult.value();

    boolean hasErrors = false;
    ImmutableList<Message> messages = cachedResult.messages();
    marshaller.write(messages.size());
    for (Message message : messages) {
      StringValue messageString = valueDb.string(message.message());

      marshaller.write(AllMessageTypes.INSTANCE.valueToByte(message.type()));
      marshaller.write(messageString.hash());
      hasErrors = hasErrors || message.type() == ERROR;
    }

    if (!hasErrors) {
      marshaller.write(flagFor(value));
      marshaller.write(value.hash());
    }

    taskResultsDb.store(taskHash, marshaller.getBytes());
  }

  public boolean contains(HashCode taskHash) {
    return taskResultsDb.contains(taskHash);
  }

  public CachedResult read(HashCode taskHash) {
    try (Unmarshaller unmarshaller = new Unmarshaller(taskResultsDb, taskHash);) {
      int size = unmarshaller.readInt();
      boolean hasErrors = false;
      List<Message> messages = newArrayList();
      for (int i = 0; i < size; i++) {
        MessageType type = unmarshaller.readEnum(AllMessageTypes.INSTANCE);
        HashCode messageStringHash = unmarshaller.readHash();
        String messageString = valueDb.string(messageStringHash).value();
        messages.add(new Message(type, messageString));
        hasErrors = hasErrors || type == ERROR;
      }

      if (hasErrors) {
        return new CachedResult(null, messages);
      } else {
        byte flag = unmarshaller.readByte();
        HashCode resultObjectHash = unmarshaller.readHash();
        Value value = readValue(flag, resultObjectHash);
        return new CachedResult(value, messages);
      }
    }
  }

  private Value readValue(byte flag, HashCode resultObjectHash) {
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

  private static byte flagFor(Value value) {
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
