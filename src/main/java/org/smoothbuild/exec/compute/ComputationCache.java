package org.smoothbuild.exec.compute;

import static org.smoothbuild.exec.base.MessageStruct.containsErrors;
import static org.smoothbuild.exec.base.MessageStruct.isValidSeverity;
import static org.smoothbuild.exec.base.MessageStruct.severity;
import static org.smoothbuild.exec.compute.ComputationCacheException.computationCacheException;
import static org.smoothbuild.exec.compute.ComputationCacheException.corruptedValueException;
import static org.smoothbuild.install.ProjectPaths.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.io.fs.space.Space.PRJ;

import java.io.IOException;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.db.ObjFactory;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.base.ObjectH;
import org.smoothbuild.db.object.obj.base.ValueH;
import org.smoothbuild.db.object.obj.val.ArrayH;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.db.object.type.base.TypeHV;
import org.smoothbuild.db.object.type.val.ArrayTypeH;
import org.smoothbuild.exec.base.Output;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.io.fs.space.ForSpace;

import okio.BufferedSink;
import okio.BufferedSource;

/**
 * This class is thread-safe.
 */
public class ComputationCache {
  private final FileSystem fileSystem;
  private final ObjectHDb objectHDb;
  private final ObjFactory objFactory;

  @Inject
  public ComputationCache(@ForSpace(PRJ) FileSystem fileSystem, ObjectHDb objectHDb,
      ObjFactory objFactory) {
    this.fileSystem = fileSystem;
    this.objectHDb = objectHDb;
    this.objFactory = objFactory;
  }

  public synchronized void write(Hash computationHash, Output output)
      throws ComputationCacheException {
    try (BufferedSink sink = fileSystem.sink(toPath(computationHash))) {
      ArrayH messages = output.messages();
      sink.write(messages.hash().toByteString());
      if (!containsErrors(messages)) {
        sink.write(output.value().hash().toByteString());
      }
    } catch (IOException e) {
      throw computationCacheException(e);
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

  public synchronized Output read(Hash taskHash, TypeHV type) throws ComputationCacheException {
    try (BufferedSource source = fileSystem.source(toPath(taskHash))) {
      ObjectH messagesObject = objectHDb.get(Hash.read(source));
      ArrayTypeH messageArrayType = objFactory.arrayT(objFactory.messageType());
      if (!messagesObject.type().equals(messageArrayType)) {
        throw corruptedValueException(taskHash, "Expected " + messageArrayType
            + " as first child of its Merkle root, but got " + messagesObject.type());
      }

      ArrayH messages = (ArrayH) messagesObject;
      Iterable<TupleH> tuples = messages.elems(TupleH.class);
      for (TupleH m : tuples) {
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
        ObjectH obj = objectHDb.get(resultObjectHash);
        if (!type.equals(obj.type())) {
          throw corruptedValueException(taskHash, "Expected value of type " + type
              + " as second child of its Merkle root, but got " + obj.type());
        } else {
          return new Output((ValueH) obj, messages);
        }
      }
    } catch (IOException e) {
      throw computationCacheException(e);
    }
  }

  static Path toPath(Hash computationHash) {
    return COMPUTATION_CACHE_PATH.appendPart(computationHash.toHexString());
  }
}
