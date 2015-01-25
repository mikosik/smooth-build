package org.smoothbuild.db.taskoutputs;

import static com.google.common.collect.Lists.newArrayList;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.db.objects.ObjectsDb;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageType;
import org.smoothbuild.message.base.Messages;
import org.smoothbuild.task.base.TaskOutput;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class TaskOutputsDb {
  private final HashedDb hashedDb;
  private final ObjectsDb objectsDb;

  @Inject
  public TaskOutputsDb(@TaskOutputs HashedDb hashedDb, ObjectsDb objectsDb) {
    this.hashedDb = hashedDb;
    this.objectsDb = objectsDb;
  }

  public void write(HashCode taskHash, TaskOutput taskOutput) {
    Marshaller marshaller = new Marshaller();

    ImmutableList<Message> messages = taskOutput.messages();
    marshaller.write(messages.size());
    for (Message message : messages) {
      SString messageString = objectsDb.string(message.message());

      marshaller.write(AllMessageTypes.INSTANCE.valueToByte(message.type()));
      marshaller.write(messageString.hash());
    }

    if (!Messages.containsProblems(messages)) {
      marshaller.write(taskOutput.result().hash());
    }

    hashedDb.write(taskHash, marshaller.getBytes());
  }

  public boolean contains(HashCode taskHash) {
    return hashedDb.contains(taskHash);
  }

  public TaskOutput read(HashCode taskHash, Type type) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, taskHash)) {
      int size = unmarshaller.readInt();
      List<Message> messages = newArrayList();
      for (int i = 0; i < size; i++) {
        MessageType messageType = unmarshaller.readEnum(AllMessageTypes.INSTANCE);
        HashCode messageStringHash = unmarshaller.readHash();
        SString messageSString = (SString) objectsDb.read(Types.STRING, messageStringHash);
        String messageString = messageSString.value();
        messages.add(new Message(messageType, messageString));
      }

      if (Messages.containsProblems(messages)) {
        return new TaskOutput(messages);
      } else {
        HashCode resultObjectHash = unmarshaller.readHash();
        Value value = objectsDb.read(type, resultObjectHash);
        return new TaskOutput(value, messages);
      }
    }
  }
}
