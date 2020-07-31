package org.smoothbuild.exec.compute;

import static org.smoothbuild.db.record.db.MessageStruct.containsErrors;
import static org.smoothbuild.db.record.db.MessageStruct.isValidSeverity;
import static org.smoothbuild.db.record.db.MessageStruct.severity;
import static org.smoothbuild.exec.compute.ComputationCacheException.corruptedValueException;
import static org.smoothbuild.exec.compute.ComputationCacheException.outputDbException;
import static org.smoothbuild.install.ProjectPaths.COMPUTATION_CACHE_PATH;

import java.io.IOException;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.record.base.Array;
import org.smoothbuild.db.record.base.Record;
import org.smoothbuild.db.record.base.Tuple;
import org.smoothbuild.db.record.db.RecordDb;
import org.smoothbuild.db.record.db.RecordFactory;
import org.smoothbuild.db.record.spec.ArraySpec;
import org.smoothbuild.db.record.spec.Spec;
import org.smoothbuild.exec.algorithm.Output;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;

import okio.BufferedSink;
import okio.BufferedSource;

/**
 * This class is thread-safe.
 */
public class ComputationCache {
  private final FileSystem fileSystem;
  private final RecordDb recordDb;
  private final RecordFactory recordFactory;

  @Inject
  public ComputationCache(FileSystem fileSystem, RecordDb recordDb, RecordFactory recordFactory) {
    this.fileSystem = fileSystem;
    this.recordDb = recordDb;
    this.recordFactory = recordFactory;
  }

  public synchronized void write(Hash computationHash, Output output)
      throws ComputationCacheException {
    try (BufferedSink sink = fileSystem.sink(toPath(computationHash))) {
      Array messages = output.messages();
      sink.write(messages.hash());
      if (!containsErrors(messages)) {
        sink.write(output.value().hash());
      }
    } catch (IOException e) {
      throw outputDbException(e);
    }
  }

  public synchronized boolean contains(Hash taskHash) throws ComputationCacheException {
    Path path = toPath(taskHash);
    PathState pathState = fileSystem.pathState(path);
    return switch (pathState) {
      case FILE -> true;
      case NOTHING -> false;
      case DIR -> throw corruptedValueException(taskHash, path + " is directory not a file.");
    };
  }

  public synchronized Output read(Hash taskHash, Spec spec) throws ComputationCacheException {
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

  static Path toPath(Hash computationHash) {
    return COMPUTATION_CACHE_PATH.appendPart(computationHash.hex());
  }
}
