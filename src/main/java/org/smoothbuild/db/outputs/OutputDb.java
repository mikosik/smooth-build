package org.smoothbuild.db.outputs;

import static org.smoothbuild.SmoothConstants.OUTPUTS_DB_PATH;
import static org.smoothbuild.db.outputs.OutputDbException.corruptedValueException;
import static org.smoothbuild.db.outputs.OutputDbException.outputDbException;
import static org.smoothbuild.lang.object.base.Messages.containsErrors;
import static org.smoothbuild.lang.object.base.Messages.isValidSeverity;
import static org.smoothbuild.lang.object.base.Messages.severity;

import java.io.IOException;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.comp.Output;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.db.ObjectDb;
import org.smoothbuild.lang.object.db.ObjectFactory;
import org.smoothbuild.lang.object.type.ArrayType;
import org.smoothbuild.lang.object.type.ConcreteType;

import okio.BufferedSink;
import okio.BufferedSource;

/**
 * This class is thread-safe.
 */
public class OutputDb {
  private final FileSystem fileSystem;
  private final ObjectDb objectDb;
  private final ObjectFactory objectFactory;

  @Inject
  public OutputDb(FileSystem fileSystem, ObjectDb objectDb, ObjectFactory objectFactory) {
    this.fileSystem = fileSystem;
    this.objectDb = objectDb;
    this.objectFactory = objectFactory;
  }

  public synchronized void write(Hash taskHash, Output output) throws OutputDbException {
    try (BufferedSink sink = fileSystem.sink(toPath(taskHash))) {
      Array messages = output.messages();
      sink.write(messages.hash());
      if (!containsErrors(messages)) {
        sink.write(output.value().hash());
      }
    } catch (IOException e) {
      throw outputDbException(e);
    }
  }

  public synchronized boolean contains(Hash taskHash) throws OutputDbException {
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

  public synchronized Output read(Hash taskHash, ConcreteType type) throws OutputDbException {
    try (BufferedSource source = fileSystem.source(toPath(taskHash))) {
      SObject messagesObject = objectDb.get(Hash.read(source));
      ArrayType messageArrayType = objectFactory.arrayType(objectFactory.messageType());
      if (!messagesObject.type().equals(messageArrayType)) {
        throw corruptedValueException(taskHash, "Expected " + messageArrayType
            + " as first child of its Merkle root, but got " + messagesObject.type());
      }

      Array messages = (Array) messagesObject;
      Iterable<Struct> structs = messages.asIterable(Struct.class);
      for (Struct m : structs) {
        String severity = severity(m);
        if (!isValidSeverity(severity)) {
          throw corruptedValueException(taskHash,
              "One of messages has invalid severity = '" + severity + "'");
        }
      }
      if (containsErrors(messages)) {
        return new Output(null, messages);
      } else {
        Hash resultObjectHash = Hash.read(source);
        SObject object = objectDb.get(resultObjectHash);
        if (!type.equals(object.type())) {
          throw corruptedValueException(taskHash, "Expected value of type " + type
              + " as second child of its Merkle root, but got " + object.type());
        }
        return new Output(object, messages);
      }
    } catch (IOException e) {
      throw outputDbException(e);
    }
  }

  static Path toPath(Hash taskHash) {
    return OUTPUTS_DB_PATH.appendPart(taskHash.hex());
  }
}
