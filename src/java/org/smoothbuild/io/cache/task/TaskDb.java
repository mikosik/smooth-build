package org.smoothbuild.io.cache.task;

import static com.google.common.collect.Lists.newArrayList;
import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.message.base.MessageType.FATAL;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.hash.Marshaller;
import org.smoothbuild.io.cache.hash.TasksCache;
import org.smoothbuild.io.cache.hash.Unmarshaller;
import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.lang.type.StringValue;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Value;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageType;
import org.smoothbuild.message.listen.ErrorMessageException;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class TaskDb {
  private final HashedDb taskResultsDb;
  private final ValueDb valueDb;

  @Inject
  public TaskDb(@TasksCache HashedDb taskResultsDb, ValueDb valueDb) {
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
      marshaller.write(AllObjectTypes.INSTANCE.valueToByte(value.type()));
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
        Type type = unmarshaller.readEnum(AllObjectTypes.INSTANCE);
        HashCode resultObjectHash = unmarshaller.readHash();
        Value value = readValue(type, resultObjectHash);
        return new CachedResult(value, messages);
      }
    }
  }

  private Value readValue(Type type, HashCode resultObjectHash) {
    if (type == Type.STRING) {
      return valueDb.string(resultObjectHash);
    }
    if (type == Type.STRING_ARRAY) {
      return valueDb.stringArray(resultObjectHash);
    }
    if (type == Type.FILE) {
      return valueDb.file(resultObjectHash);
    }
    if (type == Type.FILE_ARRAY) {
      return valueDb.fileArray(resultObjectHash);
    }
    if (type == Type.BLOB) {
      return valueDb.blob(resultObjectHash);
    }
    if (type == Type.BLOB_ARRAY) {
      return valueDb.blobArray(resultObjectHash);
    }
    throw new ErrorMessageException(new Message(FATAL,
        "Bug in smooth binary: Unexpected value type " + type));
  }
}
