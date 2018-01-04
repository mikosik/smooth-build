package org.smoothbuild.db.outputs;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.db.values.CorruptedValueException;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.message.ErrorMessage;
import org.smoothbuild.lang.message.InfoMessage;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.message.Messages;
import org.smoothbuild.lang.message.WarningMessage;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypeSystem;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Output;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class OutputsDb {
  private final HashedDb hashedDb;
  private final ValuesDb valuesDb;
  private final TypeSystem typeSystem;

  @Inject
  public OutputsDb(@Outputs HashedDb hashedDb, ValuesDb valuesDb, TypeSystem typeSystem) {
    this.hashedDb = hashedDb;
    this.valuesDb = valuesDb;
    this.typeSystem = typeSystem;
  }

  public void write(HashCode taskHash, Output output) {
    Marshaller marshaller = hashedDb.newMarshaller(taskHash);

    ImmutableList<Message> messages = output.messages();
    marshaller.writeInt(messages.size());
    for (Message message : messages) {
      SString messageString = valuesDb.string(message.message());

      marshaller.writeInt(messageTypeToInt(message));
      marshaller.writeHash(messageString.hash());
    }

    if (!Messages.containsErrors(messages)) {
      marshaller.writeHash(output.result().hash());
    }
    marshaller.close();
  }

  public boolean contains(HashCode taskHash) {
    return hashedDb.contains(taskHash);
  }

  public Output read(HashCode taskHash, Type type) {
    try (Unmarshaller unmarshaller = hashedDb.newUnmarshaller(taskHash)) {
      int size = unmarshaller.readInt();
      List<Message> messages = new ArrayList<>();
      for (int i = 0; i < size; i++) {
        int messageType = unmarshaller.readInt();
        HashCode messageStringHash = unmarshaller.readHash();
        Value messageValue = valuesDb.get(messageStringHash);
        if (!typeSystem.string().equals(messageValue.type())) {
          throw new CorruptedValueException(messageStringHash, "Expected message of type "
              + typeSystem.string() + " but got " + messageValue.type());
        }
        SString messageSString = (SString) messageValue;
        String messageString = messageSString.data();
        messages.add(newMessage(messageType, messageString));
      }

      if (Messages.containsErrors(messages)) {
        return new Output(messages);
      } else {
        HashCode resultObjectHash = unmarshaller.readHash();
        Value value = valuesDb.get(resultObjectHash);
        if (!type.equals(value.type())) {
          throw new CorruptedValueException(resultObjectHash, "Expected result with type " + type
              + " but got " + value.type());
        }
        return new Output(value, messages);
      }
    }
  }

  private static int messageTypeToInt(Message message) {
    if (message instanceof ErrorMessage) {
      return 0;
    }
    if (message instanceof WarningMessage) {
      return 1;
    }
    if (message instanceof InfoMessage) {
      return 2;
    }
    throw new RuntimeException("Unsupported Message type: " + message.getClass()
        .getCanonicalName());
  }

  private static Message newMessage(int type, String message) {
    if (type == 0) {
      return new ErrorMessage(message);
    }
    if (type == 1) {
      return new WarningMessage(message);
    }
    if (type == 2) {
      return new InfoMessage(message);
    }
    throw new RuntimeException("Illegal message type. Outputs DB corrupted?");
  }
}
