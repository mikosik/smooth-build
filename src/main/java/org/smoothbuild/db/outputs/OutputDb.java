package org.smoothbuild.db.outputs;

import static org.smoothbuild.db.outputs.OutputDbException.corruptedValueException;
import static org.smoothbuild.db.outputs.OutputDbException.outputDbException;
import static org.smoothbuild.install.ProjectPaths.OUTPUTS_DB_PATH;
import static org.smoothbuild.record.base.Messages.containsErrors;
import static org.smoothbuild.record.base.Messages.isValidSeverity;
import static org.smoothbuild.record.base.Messages.severity;

import java.io.IOException;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.exec.comp.Output;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.record.base.Array;
import org.smoothbuild.record.base.Record;
import org.smoothbuild.record.base.Tuple;
import org.smoothbuild.record.db.RecordDb;
import org.smoothbuild.record.db.RecordFactory;
import org.smoothbuild.record.spec.ArraySpec;
import org.smoothbuild.record.spec.Spec;

import okio.BufferedSink;
import okio.BufferedSource;

/**
 * This class is thread-safe.
 */
public class OutputDb {
  private final FileSystem fileSystem;
  private final RecordDb recordDb;
  private final RecordFactory recordFactory;

  @Inject
  public OutputDb(FileSystem fileSystem, RecordDb recordDb, RecordFactory recordFactory) {
    this.fileSystem = fileSystem;
    this.recordDb = recordDb;
    this.recordFactory = recordFactory;
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
    return switch (pathState) {
      case FILE -> true;
      case NOTHING -> false;
      case DIR -> throw corruptedValueException(taskHash, path + " is directory not a file.");
    };
  }

  public synchronized Output read(Hash taskHash, Spec spec) throws OutputDbException {
    try (BufferedSource source = fileSystem.source(toPath(taskHash))) {
      Record messagesObject = recordDb.get(Hash.read(source));
      ArraySpec messageArraySpec = recordFactory.arraySpec(recordFactory.messageSpec());
      if (!messagesObject.spec().equals(messageArraySpec)) {
        throw corruptedValueException(taskHash, "Expected " + messageArraySpec
            + " as first child of its Merkle root, but got " + messagesObject.spec());
      }

      Array messages = (Array) messagesObject;
      Iterable<Tuple> structs = messages.asIterable(Tuple.class);
      for (Tuple m : structs) {
        String severity = severity(m);
        if (!isValidSeverity(severity)) {
          throw corruptedValueException(taskHash,
              "One of messages has invalid severity = '" + severity + "'");
        }
      }
      if (containsErrors(messages)) {
        return new Output(null, messages);
      } else {
        Hash resultRecordHash = Hash.read(source);
        Record record = recordDb.get(resultRecordHash);
        if (!spec.equals(record.spec())) {
          throw corruptedValueException(taskHash, "Expected value of type " + spec
              + " as second child of its Merkle root, but got " + record.spec());
        }
        return new Output(record, messages);
      }
    } catch (IOException e) {
      throw outputDbException(e);
    }
  }

  static Path toPath(Hash taskHash) {
    return OUTPUTS_DB_PATH.appendPart(taskHash.hex());
  }
}
