package org.smoothbuild.io.cache.task;

import static com.google.common.collect.Lists.newArrayList;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.message.base.MessageType.ERROR;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.io.cache.hash.HashedDb;
import org.smoothbuild.io.cache.hash.Marshaller;
import org.smoothbuild.io.cache.hash.TaskResults;
import org.smoothbuild.io.cache.hash.Unmarshaller;
import org.smoothbuild.io.cache.value.ObjectsDb;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageType;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class TaskResultsDb {
  private final HashedDb hashedDb;
  private final ObjectsDb objectsDb;

  @Inject
  public TaskResultsDb(@TaskResults HashedDb hashedDb, ObjectsDb objectsDb) {
    this.hashedDb = hashedDb;
    this.objectsDb = objectsDb;
  }

  public void store(HashCode taskHash, TaskResult<? extends SValue> cachedResult) {
    Marshaller marshaller = new Marshaller();

    SValue value = cachedResult.value();

    boolean hasErrors = false;
    ImmutableList<Message> messages = cachedResult.messages();
    marshaller.write(messages.size());
    for (Message message : messages) {
      SString messageString = objectsDb.writeString(message.message());

      marshaller.write(AllMessageTypes.INSTANCE.valueToByte(message.type()));
      marshaller.write(messageString.hash());
      hasErrors = hasErrors || message.type() == ERROR;
    }

    if (!hasErrors) {
      marshaller.write(value.hash());
    }

    hashedDb.store(taskHash, marshaller.getBytes());
  }

  public boolean contains(HashCode taskHash) {
    return hashedDb.contains(taskHash);
  }

  public <T extends SValue> TaskResult<T> read(HashCode taskHash, SType<T> type) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, taskHash);) {
      int size = unmarshaller.readInt();
      boolean hasErrors = false;
      List<Message> messages = newArrayList();
      for (int i = 0; i < size; i++) {
        MessageType messageType = unmarshaller.readEnum(AllMessageTypes.INSTANCE);
        HashCode messageStringHash = unmarshaller.readHash();
        String messageString = objectsDb.read(STRING, messageStringHash).value();
        messages.add(new Message(messageType, messageString));
        hasErrors = hasErrors || messageType == ERROR;
      }

      if (hasErrors) {
        return new TaskResult<T>(null, messages);
      } else {
        HashCode resultObjectHash = unmarshaller.readHash();
        T value = objectsDb.read(type, resultObjectHash);
        return new TaskResult<>(value, messages);
      }
    }
  }
}
