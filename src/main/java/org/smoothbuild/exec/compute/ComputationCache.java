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
import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.base.Obj;
import org.smoothbuild.db.object.obj.base.Val;
import org.smoothbuild.db.object.obj.val.Array;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.db.object.type.base.ValType;
import org.smoothbuild.db.object.type.val.ArrayOType;
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
  private final ObjDb objDb;
  private final ObjFactory objFactory;

  @Inject
  public ComputationCache(@ForSpace(PRJ) FileSystem fileSystem, ObjDb objDb,
      ObjFactory objFactory) {
    this.fileSystem = fileSystem;
    this.objDb = objDb;
    this.objFactory = objFactory;
  }

  public synchronized void write(Hash computationHash, Output output)
      throws ComputationCacheException {
    try (BufferedSink sink = fileSystem.sink(toPath(computationHash))) {
      Array messages = output.messages();
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

  public synchronized Output read(Hash taskHash, ValType type) throws ComputationCacheException {
    try (BufferedSource source = fileSystem.source(toPath(taskHash))) {
      Obj messagesObject = objDb.get(Hash.read(source));
      ArrayOType messageArrayType = objFactory.arrayType(objFactory.messageType());
      if (!messagesObject.type().equals(messageArrayType)) {
        throw corruptedValueException(taskHash, "Expected " + messageArrayType
            + " as first child of its Merkle root, but got " + messagesObject.type());
      }

      Array messages = (Array) messagesObject;
      Iterable<Struc_> structs = messages.elements(Struc_.class);
      for (Struc_ m : structs) {
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
        Obj obj = objDb.get(resultObjectHash);
        if (!type.equals(obj.type())) {
          throw corruptedValueException(taskHash, "Expected value of type " + type
              + " as second child of its Merkle root, but got " + obj.type());
        } else {
          return new Output((Val) obj, messages);
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
