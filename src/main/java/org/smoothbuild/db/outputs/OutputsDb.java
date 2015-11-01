package org.smoothbuild.db.outputs;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Types;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.base.MessageType;
import org.smoothbuild.message.base.Messages;
import org.smoothbuild.task.base.Output;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class OutputsDb {
  private final HashedDb hashedDb;
  private final ValuesDb valuesDb;

  @Inject
  public OutputsDb(@Outputs HashedDb hashedDb, ValuesDb valuesDb) {
    this.hashedDb = hashedDb;
    this.valuesDb = valuesDb;
  }

  public void write(HashCode taskHash, Output output) {
    Marshaller marshaller = new Marshaller();

    ImmutableList<Message> messages = output.messages();
    marshaller.write(messages.size());
    for (Message message : messages) {
      SString messageString = valuesDb.string(message.message());

      marshaller.write(AllMessageTypes.INSTANCE.valueToByte(message.type()));
      marshaller.write(messageString.hash());
    }

    if (!Messages.containsErrors(messages)) {
      marshaller.write(output.result().hash());
    }

    hashedDb.write(taskHash, marshaller.getBytes());
  }

  public boolean contains(HashCode taskHash) {
    return hashedDb.contains(taskHash);
  }

  public Output read(HashCode taskHash, Type type) {
    try (Unmarshaller unmarshaller = new Unmarshaller(hashedDb, taskHash)) {
      int size = unmarshaller.readInt();
      List<Message> messages = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        MessageType messageType = unmarshaller.readEnum(AllMessageTypes.INSTANCE);
        HashCode messageStringHash = unmarshaller.readHash();
        SString messageSString = (SString) valuesDb.read(Types.STRING, messageStringHash);
        String messageString = messageSString.value();
        messages.add(new Message(messageType, messageString));
      }

      if (Messages.containsErrors(messages)) {
        return new Output(messages);
      } else {
        HashCode resultObjectHash = unmarshaller.readHash();
        Value value = valuesDb.read(type, resultObjectHash);
        return new Output(value, messages);
      }
    }
  }
}
