package org.smoothbuild.io.cache.task;

import static com.google.common.collect.Lists.newArrayList;
import static org.smoothbuild.lang.type.Type.STRING_T;
import static org.smoothbuild.message.base.MessageType.ERROR;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.hash.Marshaller;
import org.smoothbuild.io.cache.hash.TasksCache;
import org.smoothbuild.io.cache.hash.Unmarshaller;
import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Value;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageType;

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
      SString messageString = valueDb.writeString(message.message());

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
        String messageString = valueDb.read(STRING_T, messageStringHash).value();
        messages.add(new Message(type, messageString));
        hasErrors = hasErrors || type == ERROR;
      }

      if (hasErrors) {
        return new CachedResult(null, messages);
      } else {
        Type<?> type = unmarshaller.readEnum(AllObjectTypes.INSTANCE);
        HashCode resultObjectHash = unmarshaller.readHash();
        Value value = valueDb.read(type.javaTypeLiteral(), resultObjectHash);
        return new CachedResult(value, messages);
      }
    }
  }
}
