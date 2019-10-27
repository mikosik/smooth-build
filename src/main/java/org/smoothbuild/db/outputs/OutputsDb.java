package org.smoothbuild.db.outputs;

import static org.smoothbuild.SmoothConstants.OUTPUTS_DB_PATH;
import static org.smoothbuild.db.outputs.OutputsDbException.corruptedValueException;
import static org.smoothbuild.db.outputs.OutputsDbException.outputsDbException;
import static org.smoothbuild.lang.message.Messages.containsErrors;
import static org.smoothbuild.lang.message.Messages.isValidSeverity;
import static org.smoothbuild.lang.message.Messages.severity;

import java.io.IOException;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.lang.plugin.Types;
import org.smoothbuild.lang.type.ArrayType;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.lang.value.Array;
import org.smoothbuild.lang.value.Struct;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Output;

import okio.BufferedSink;
import okio.BufferedSource;

public class OutputsDb {
  private final FileSystem fileSystem;
  private final ValuesDb valuesDb;
  private final Types types;

  @Inject
  public OutputsDb(FileSystem fileSystem, ValuesDb valuesDb, Types types) {
    this.fileSystem = fileSystem;
    this.valuesDb = valuesDb;
    this.types = types;
  }

  public void write(Hash taskHash, Output output) {
    try (BufferedSink sink = fileSystem.sink(toPath(taskHash))) {
      Array messages = output.messages();
      sink.write(messages.hash());
      if (!containsErrors(messages)) {
        sink.write(output.result().hash());
      }
    } catch (IOException e) {
      throw outputsDbException(e);
    }
  }

  public boolean contains(Hash taskHash) {
    Path path = toPath(taskHash);
    PathState pathState = fileSystem.pathState(path);
    switch (pathState) {
      case FILE:
        return true;
      case NOTHING:
        return false;
      case DIR:
        throw corruptedValueException(taskHash, path + " is directory not a file.");
      default:
        throw new RuntimeException("Unexpected case " + pathState);
    }
  }

  public Output read(Hash taskHash, ConcreteType type) {
    try (BufferedSource source = fileSystem.source(toPath(taskHash))) {
      Value messagesValue = valuesDb.get(Hash.read(source));
      ArrayType messageArrayType = types.array(types.message());
      if (!messagesValue.type().equals(messageArrayType)) {
        throw corruptedValueException(taskHash, "Expected " + messageArrayType
            + " as first child of its Merkle root, but got " + messagesValue.type());
      }

      Array messages = (Array) messagesValue;
      messages.asIterable(Struct.class).forEach(m -> {
        String severity = severity(m);
        if (!isValidSeverity(severity)) {
          throw corruptedValueException(taskHash,
              "One of messages has invalid severity = '" + severity + "'");
        }
      });
      if (containsErrors(messages)) {
        return new Output(null, messages);
      } else {
        Hash resultObjectHash = Hash.read(source);
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

  static Path toPath(Hash taskHash) {
    return OUTPUTS_DB_PATH.append(Hash.toPath(taskHash));
  }
}
