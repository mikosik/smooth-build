package org.smoothbuild.db.outputs;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.lang.message.Message.isValidSeverity;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.message.Messages;
import org.smoothbuild.lang.message.MessagesDb;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.TypesDb;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Output;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

public class OutputsDb {
  private final HashedDb hashedDb;
  private final ValuesDb valuesDb;
  private final MessagesDb messagesDb;
  private final TypesDb typesDb;

  @Inject
  public OutputsDb(@Outputs HashedDb hashedDb, ValuesDb valuesDb, MessagesDb messagesDb,
      TypesDb typesDb) {
    this.hashedDb = hashedDb;
    this.valuesDb = valuesDb;
    this.messagesDb = messagesDb;
    this.typesDb = typesDb;
  }

  public void write(HashCode taskHash, Output output) {
    Marshaller marshaller = hashedDb.newMarshaller(taskHash);
    ImmutableList<Message> messageList = output.messages();
    marshaller.writeHash(writeMessages(messageList).hash());
    if (!Messages.containsErrors(messageList)) {
      marshaller.writeHash(output.result().hash());
    }
    marshaller.close();
  }

  private Array writeMessages(ImmutableList<Message> messages) {
    ArrayBuilder builder = valuesDb.arrayBuilder(messagesDb.messageType());
    for (Message message : messages) {
      builder.add(message.value());
    }
    return builder.build();
  }

  public boolean contains(HashCode taskHash) {
    return hashedDb.contains(taskHash);
  }

  public Output read(HashCode taskHash, Type type) {
    try (Unmarshaller unmarshaller = hashedDb.newUnmarshaller(taskHash)) {
      Value messagesValue = valuesDb.get(unmarshaller.readHash());
      ArrayType messageArrayType = typesDb.array(messagesDb.messageType());
      if (!messagesValue.type().equals(messageArrayType)) {
        throw new CorruptedOutputException(taskHash, "Expected " + messageArrayType
            + " as first child of its merkle root, but got " + messagesValue.type());
      }

      List<Message> messages = stream(((Array) messagesValue).asIterable(Struct.class))
          .map(struct -> new Message(struct))
          .collect(toImmutableList());
      messages.stream().forEach(m -> {
        if (!isValidSeverity(m.severity())) {
          throw new CorruptedOutputException(taskHash,
              "One of messages has invalid severity = '" + m.severity() + "'");
        }
      });
      if (Messages.containsErrors(messages)) {
        return new Output(messages);
      } else {
        HashCode resultObjectHash = unmarshaller.readHash();
        Value value = valuesDb.get(resultObjectHash);
        if (!type.equals(value.type())) {
          throw new CorruptedOutputException(taskHash, "Expected value of type " + type
              + " as second child of its merkle root, but got " + value.type());
        }
        return new Output(value, messages);
      }
    }
  }
}
