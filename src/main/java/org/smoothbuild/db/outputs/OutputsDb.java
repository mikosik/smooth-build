package org.smoothbuild.db.outputs;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.db.outputs.OutputsDbException.corruptedHashSequenceException;
import static org.smoothbuild.db.outputs.OutputsDbException.corruptedValueException;
import static org.smoothbuild.lang.message.Message.isValidSeverity;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.hashed.NotEnoughBytesException;
import org.smoothbuild.db.hashed.Unmarshaller;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.FileSystemException;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.message.Messages;
import org.smoothbuild.lang.message.MessagesDb;
import org.smoothbuild.lang.type.ConcreteArrayType;
import org.smoothbuild.lang.type.ConcreteType;
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
    try (Marshaller marshaller = hashedDb.newMarshaller(taskHash)) {
      ImmutableList<Message> messageList = output.messages();
      marshaller.sink().write(writeMessages(messageList).hash().asBytes());
      if (!Messages.containsErrors(messageList)) {
        marshaller.sink().write(output.result().hash().asBytes());
      }
    } catch (IOException e) {
      throw new HashedDbException("IOException when storing output", e);
    }
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

  public Output read(HashCode taskHash, ConcreteType type) {
    try (Unmarshaller unmarshaller = hashedDb.newUnmarshaller(taskHash)) {
      Value messagesValue = valuesDb.get(readHash(unmarshaller, taskHash));
      ConcreteArrayType messageArrayType = typesDb.array(messagesDb.messageType());
      if (!messagesValue.type().equals(messageArrayType)) {
        throw corruptedValueException(taskHash, "Expected " + messageArrayType
            + " as first child of its merkle root, but got " + messagesValue.type());
      }

      List<Message> messages = stream(((Array) messagesValue).asIterable(Struct.class))
          .map(struct -> new Message(struct))
          .collect(toImmutableList());
      messages.stream().forEach(m -> {
        if (!isValidSeverity(m.severity())) {
          throw corruptedValueException(taskHash,
              "One of messages has invalid severity = '" + m.severity() + "'");
        }
      });
      if (Messages.containsErrors(messages)) {
        return new Output(messages);
      } else {
        HashCode resultObjectHash = readHash(unmarshaller, taskHash);
        Value value = valuesDb.get(resultObjectHash);
        if (!type.equals(value.type())) {
          throw corruptedValueException(taskHash, "Expected value of type " + type
              + " as second child of its merkle root, but got " + value.type());
        }
        return new Output(value, messages);
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
  }

  private static HashCode readHash(Unmarshaller unmarshaller, HashCode taskHash) {
    try {
      return unmarshaller.readHash();
    } catch (NotEnoughBytesException e) {
      throw corruptedHashSequenceException(taskHash);
    }
  }
}
