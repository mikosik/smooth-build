package org.smoothbuild.db.taskresults;

import static com.google.common.collect.Lists.newArrayList;
import static org.smoothbuild.lang.base.STypes.STRING;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageType;
import org.smoothbuild.message.base.Messages;

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

  public void store(HashCode taskHash, TaskResult<? extends SValue> taskResult) {
    Marshaller marshaller = new Marshaller();

    SValue value = taskResult.value();

    ImmutableList<Message> messages = taskResult.messages();
    marshaller.write(messages.size());
    for (Message message : messages) {
      SString messageString = objectsDb.string(message.message());

      marshaller.write(AllMessageTypes.INSTANCE.valueToByte(message.type()));
      marshaller.write(messageString.hash());
    }

    if (!Messages.containsProblems(messages)) {
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
      List<Message> messages = newArrayList();
      for (int i = 0; i < size; i++) {
        MessageType messageType = unmarshaller.readEnum(AllMessageTypes.INSTANCE);
        HashCode messageStringHash = unmarshaller.readHash();
        String messageString = objectsDb.read(STRING, messageStringHash).value();
        messages.add(new Message(messageType, messageString));
      }

      if (Messages.containsProblems(messages)) {
        return new TaskResult<T>(null, messages);
      } else {
        HashCode resultObjectHash = unmarshaller.readHash();
        T value = objectsDb.read(type, resultObjectHash);
        return new TaskResult<>(value, messages);
      }
    }
  }
}
