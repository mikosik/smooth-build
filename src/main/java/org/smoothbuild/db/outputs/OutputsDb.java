package org.smoothbuild.db.outputs;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static com.google.common.collect.Streams.stream;
import static org.smoothbuild.db.outputs.OutputsDbException.corruptedValueException;
import static org.smoothbuild.db.outputs.OutputsDbException.outputsDbException;
import static org.smoothbuild.lang.message.Message.isValidSeverity;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.Marshaller;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.message.Message;
import org.smoothbuild.lang.message.Messages;
import org.smoothbuild.lang.plugin.Types;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.ArrayBuilder;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Output;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;

import okio.BufferedSource;

public class OutputsDb {
  private final HashedDb hashedDb;
  private final ValuesDb valuesDb;
  private final Types types;

  @Inject
  public OutputsDb(@Outputs HashedDb hashedDb, ValuesDb valuesDb, Types types) {
    this.hashedDb = hashedDb;
    this.valuesDb = valuesDb;
    this.types = types;
  }

  public void write(HashCode taskHash, Output output) {
    try (Marshaller marshaller = hashedDb.newMarshaller(taskHash)) {
      ImmutableList<Message> messageList = output.messages();
      marshaller.sink().write(storeMessageArray(messageList).hash().asBytes());
      if (!Messages.containsErrors(messageList)) {
        marshaller.sink().write(output.result().hash().asBytes());
      }
    } catch (IOException e) {
      throw outputsDbException(e);
    }
  }

  private Array storeMessageArray(ImmutableList<Message> messages) {
    ArrayBuilder builder = valuesDb.arrayBuilder(types.message());
    for (Message message : messages) {
      builder.add(message.value());
    }
    return builder.build();
  }

  public boolean contains(HashCode taskHash) {
    return hashedDb.contains(taskHash);
  }

  public Output read(HashCode taskHash, ConcreteType type) {
    try (BufferedSource source = hashedDb.source(taskHash)) {
      Value messagesValue = valuesDb.get(Hash.read(source));
      ArrayType messageArrayType = types.array(types.message());
      if (!messagesValue.type().equals(messageArrayType)) {
        throw corruptedValueException(taskHash, "Expected " + messageArrayType
            + " as first child of its Merkle root, but got " + messagesValue.type());
      }

      List<Message> messages = stream(((Array) messagesValue).asIterable(Struct.class))
          .map(Message::new)
          .collect(toImmutableList());
      messages.forEach(m -> {
        if (!isValidSeverity(m.severity())) {
          throw corruptedValueException(taskHash,
              "One of messages has invalid severity = '" + m.severity() + "'");
        }
      });
      if (Messages.containsErrors(messages)) {
        return new Output(messages);
      } else {
        HashCode resultObjectHash = Hash.read(source);
        Value value = valuesDb.get(resultObjectHash);
        if (!type.equals(value.type())) {
          throw corruptedValueException(taskHash, "Expected value of type " + type
              + " as second child of its Merkle root, but got " + value.type());
        }
        return new Output(value, messages);
      }
    } catch (IOException e) {
      throw outputsDbException(e);
    }
  }
}
