package org.smoothbuild.vm.compute;

import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.install.ProjectPaths.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.run.eval.MessageStruct.containsErrors;
import static org.smoothbuild.run.eval.MessageStruct.isValidSeverity;
import static org.smoothbuild.run.eval.MessageStruct.severity;
import static org.smoothbuild.vm.compute.ComputationCacheExc.computationCacheException;
import static org.smoothbuild.vm.compute.ComputationCacheExc.corruptedValueException;

import java.io.IOException;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.expr.BytecodeDb;
import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.val.ArrayB;
import org.smoothbuild.bytecode.expr.val.InstB;
import org.smoothbuild.bytecode.expr.val.TupleB;
import org.smoothbuild.bytecode.hashed.Hash;
import org.smoothbuild.bytecode.type.val.ArrayTB;
import org.smoothbuild.bytecode.type.val.TypeB;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.fs.base.PathState;
import org.smoothbuild.fs.space.ForSpace;
import org.smoothbuild.vm.task.Output;

import okio.BufferedSink;
import okio.BufferedSource;

/**
 * This class is thread-safe.
 */
public class ComputationCache {
  private final FileSystem fileSystem;
  private final BytecodeDb bytecodeDb;
  private final BytecodeF bytecodeF;

  @Inject
  public ComputationCache(@ForSpace(PRJ) FileSystem fileSystem, BytecodeDb bytecodeDb,
      BytecodeF bytecodeF) {
    this.fileSystem = fileSystem;
    this.bytecodeDb = bytecodeDb;
    this.bytecodeF = bytecodeF;
  }

  public synchronized void write(Hash computationHash, Output output)
      throws ComputationCacheExc {
    try (BufferedSink sink = fileSystem.sink(toPath(computationHash))) {
      ArrayB messages = output.messages();
      sink.write(messages.hash().toByteString());
      if (!containsErrors(messages)) {
        sink.write(output.instB().hash().toByteString());
      }
    } catch (IOException e) {
      throw computationCacheException(e);
    }
  }

  public synchronized boolean contains(Hash taskHash) throws ComputationCacheExc {
    PathS path = toPath(taskHash);
    PathState pathState = fileSystem.pathState(path);
    return switch (pathState) {
      case FILE -> true;
      case NOTHING -> false;
      case DIR -> throw corruptedValueException(taskHash, path + " is directory not a file.");
    };
  }

  public synchronized Output read(Hash taskHash, TypeB type) throws ComputationCacheExc {
    try (BufferedSource source = fileSystem.source(toPath(taskHash))) {
      ExprB message = bytecodeDb.get(Hash.read(source));
      ArrayTB messageArrayT = bytecodeF.arrayT(bytecodeF.messageT());
      if (!message.category().equals(messageArrayT)) {
        throw corruptedValueException(taskHash, "Expected " + messageArrayT
            + " as first child of its Merkle root, but got " + message.category());
      }

      ArrayB messages = (ArrayB) message;
      Iterable<TupleB> tuples = messages.elems(TupleB.class);
      for (TupleB m : tuples) {
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
        ExprB expr = bytecodeDb.get(resultObjectHash);
        if (!type.equals(expr.category())) {
          throw corruptedValueException(taskHash, "Expected value of type " + type
              + " as second child of its Merkle root, but got " + expr.category());
        } else {
          return new Output((InstB) expr, messages);
        }
      }
    } catch (IOException e) {
      throw computationCacheException(e);
    }
  }

  static PathS toPath(Hash computationHash) {
    return COMPUTATION_CACHE_PATH.appendPart(computationHash.toHexString());
  }
}
