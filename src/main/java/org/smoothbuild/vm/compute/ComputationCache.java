package org.smoothbuild.vm.compute;

import static org.smoothbuild.eval.artifact.MessageStruct.containsErrors;
import static org.smoothbuild.eval.artifact.MessageStruct.isValidSeverity;
import static org.smoothbuild.eval.artifact.MessageStruct.severity;
import static org.smoothbuild.install.ProjectPaths.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.io.fs.space.Space.PRJ;

import java.io.IOException;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.obj.ObjDb;
import org.smoothbuild.bytecode.obj.base.ObjB;
import org.smoothbuild.bytecode.obj.val.ArrayB;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.bytecode.obj.val.ValB;
import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.db.Hash;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.PathS;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.io.fs.space.ForSpace;
import org.smoothbuild.vm.job.algorithm.Output;

import okio.BufferedSink;
import okio.BufferedSource;

/**
 * This class is thread-safe.
 */
public class ComputationCache {
  private final FileSystem fileSystem;
  private final ObjDb objDb;
  private final BytecodeF bytecodeF;

  @Inject
  public ComputationCache(@ForSpace(PRJ) FileSystem fileSystem, ObjDb objDb,
      BytecodeF bytecodeF) {
    this.fileSystem = fileSystem;
    this.objDb = objDb;
    this.bytecodeF = bytecodeF;
  }

  public synchronized void write(Hash computationHash, Output output)
      throws ComputationCacheExc {
    try (BufferedSink sink = fileSystem.sink(toPath(computationHash))) {
      ArrayB messages = output.messages();
      sink.write(messages.hash().toByteString());
      if (!containsErrors(messages)) {
        sink.write(output.val().hash().toByteString());
      }
    } catch (IOException e) {
      throw ComputationCacheExc.computationCacheException(e);
    }
  }

  public synchronized boolean contains(Hash taskHash) throws ComputationCacheExc {
    PathS path = toPath(taskHash);
    PathState pathState = fileSystem.pathState(path);
    return switch (pathState) {
      case FILE -> true;
      case NOTHING -> false;
      case DIR -> throw ComputationCacheExc.corruptedValueException(taskHash, path + " is directory not a file.");
    };
  }

  public synchronized Output read(Hash taskHash, TypeB type) throws ComputationCacheExc {
    try (BufferedSource source = fileSystem.source(toPath(taskHash))) {
      ObjB messagesObj = objDb.get(Hash.read(source));
      ArrayTB messageArrayT = bytecodeF.arrayT(bytecodeF.messageT());
      if (!messagesObj.cat().equals(messageArrayT)) {
        throw ComputationCacheExc.corruptedValueException(taskHash, "Expected " + messageArrayT
            + " as first child of its Merkle root, but got " + messagesObj.cat());
      }

      ArrayB messages = (ArrayB) messagesObj;
      Iterable<TupleB> tuples = messages.elems(TupleB.class);
      for (TupleB m : tuples) {
        String severity = severity(m);
        if (!isValidSeverity(severity)) {
          throw ComputationCacheExc.corruptedValueException(taskHash,
              "One of messages has invalid severity = '" + severity + "'");
        }
      }
      if (containsErrors(messages)) {
        return new Output(null, messages);
      } else {
        Hash resultObjectHash = Hash.read(source);
        ObjB obj = objDb.get(resultObjectHash);
        if (!type.equals(obj.cat())) {
          throw ComputationCacheExc.corruptedValueException(taskHash, "Expected value of type " + type
              + " as second child of its Merkle root, but got " + obj.cat());
        } else {
          return new Output((ValB) obj, messages);
        }
      }
    } catch (IOException e) {
      throw ComputationCacheExc.computationCacheException(e);
    }
  }

  static PathS toPath(Hash computationHash) {
    return COMPUTATION_CACHE_PATH.appendPart(computationHash.toHexString());
  }
}
