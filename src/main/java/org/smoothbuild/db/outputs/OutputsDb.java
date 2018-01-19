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
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.SString;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Output;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class OutputsDb {
  private final HashedDb hashedDb;
  private final ValuesDb valuesDb;
  private final TypesDb typesDb;

  @Inject
  public OutputsDb(@Outputs HashedDb hashedDb, ValuesDb valuesDb, TypesDb typesDb) {
    this.hashedDb = hashedDb;
    this.valuesDb = valuesDb;
    this.typesDb = typesDb;
  }

  public void write(HashCode taskHash, Output output) {
    Marshaller marshaller = hashedDb.newMarshaller(taskHash);

    ImmutableList<Message> messages = output.messages();
    marshaller.writeInt(messages.size());
    for (Message message : messages) {
      SString messageString = valuesDb.string(message.message());
      SString messageKindString = valuesDb.string(messageTypeToString(message));
      marshaller.writeHash(messageKindString.hash());
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
        HashCode messageKindHash = unmarshaller.readHash();
        Value messageKind = valuesDb.get(messageKindHash);
        if (!typesDb.string().equals(messageKind.type())) {
          throw new CorruptedValueException(messageKindHash, "Expected message of type "
              + typesDb.string() + " but got " + messageKind.type());
        }
        HashCode messageStringHash = unmarshaller.readHash();
        Value messageValue = valuesDb.get(messageStringHash);
        if (!typesDb.string().equals(messageValue.type())) {
          throw new CorruptedValueException(messageStringHash, "Expected message of type "
              + typesDb.string() + " but got " + messageValue.type());
        }
        String kindString = ((SString) messageKind).data();
        String messageString = ((SString) messageValue).data();
        messages.add(newMessage(kindString, messageString));
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

  private static String messageTypeToString(Message message) {
    if (message instanceof ErrorMessage) {
      return "error";
    }
    if (message instanceof WarningMessage) {
      return "warning";
    }
    if (message instanceof InfoMessage) {
      return "info";
    }
    throw new RuntimeException("Unsupported Message type: " + message.getClass()
        .getCanonicalName());
  }

  private static Message newMessage(String type, String message) {
    switch (type) {
      case "error":
        return new ErrorMessage(message);
      case "warning":
        new WarningMessage(message);
      case "info":
        new InfoMessage(message);
      default:
        throw new RuntimeException("Illegal message type. Outputs DB corrupted?");
    }
  }
}
