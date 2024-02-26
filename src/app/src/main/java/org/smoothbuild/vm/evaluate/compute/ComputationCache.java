package org.smoothbuild.vm.evaluate.compute;

import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.filesystem.space.SmoothSpace.PROJECT;
import static org.smoothbuild.run.eval.MessageStruct.containsErrorOrAbove;
import static org.smoothbuild.run.eval.MessageStruct.isValidSeverity;
import static org.smoothbuild.run.eval.MessageStruct.severity;
import static org.smoothbuild.vm.evaluate.compute.ComputeException.computeException;
import static org.smoothbuild.vm.evaluate.compute.ComputeException.corruptedValueException;

import jakarta.inject.Inject;
import java.io.IOException;
import okio.BufferedSink;
import okio.BufferedSource;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.filesystem.space.ForSpace;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.value.TypeB;
import org.smoothbuild.vm.evaluate.task.Output;

/**
 * This class is thread-safe.
 */
public class ComputationCache {
  private final FileSystem fileSystem;
  private final ExprDb exprDb;
  private final BytecodeF bytecodeF;

  @Inject
  public ComputationCache(
      @ForSpace(PROJECT) FileSystem fileSystem, ExprDb exprDb, BytecodeF bytecodeF) {
    this.fileSystem = fileSystem;
    this.exprDb = exprDb;
    this.bytecodeF = bytecodeF;
  }

  public synchronized void write(Hash hash, Output output) throws ComputeException {
    try (BufferedSink sink = fileSystem.sink(toPath(hash))) {
      var messages = output.messages();
      sink.write(messages.hash().toByteString());
      var valueB = output.valueB();
      if (valueB != null) {
        sink.write(valueB.hash().toByteString());
      }
    } catch (IOException e) {
      throw computeException(e);
    }
  }

  public synchronized boolean contains(Hash hash) throws ComputeException {
    var path = toPath(hash);
    return switch (fileSystem.pathState(path)) {
      case FILE -> true;
      case NOTHING -> false;
      case DIR -> throw corruptedValueException(hash, path + " is directory not a file.");
    };
  }

  public synchronized Output read(Hash hash, TypeB type) throws ComputeException {
    try (BufferedSource source = fileSystem.source(toPath(hash))) {
      var messagesHash = Hash.read(source);
      var messages = exprDb.get(messagesHash);
      var messageArrayT = bytecodeF.arrayT(bytecodeF.messageT());
      if (!messages.category().equals(messageArrayT)) {
        throw corruptedValueException(
            hash,
            "Expected " + messageArrayT.q() + " as first child of its Merkle root, but got "
                + messages.category().q());
      }

      var messageArray = (ArrayB) messages;
      for (var message : messageArray.elems(TupleB.class)) {
        var severity = severity(message);
        if (!isValidSeverity(severity)) {
          throw corruptedValueException(
              hash, "One of messages has invalid severity = '" + severity + "'");
        }
      }
      if (containsErrorOrAbove(messageArray)) {
        return new Output(null, messageArray);
      } else {
        var valueHash = Hash.read(source);
        var value = exprDb.get(valueHash);
        if (!type.equals(value.evaluationT())) {
          throw corruptedValueException(
              hash,
              "Expected value of type " + type.q() + " as second child of its Merkle root, but got "
                  + value.evaluationT().q());
        } else {
          return new Output((ValueB) value, messageArray);
        }
      }
    } catch (IOException e) {
      throw computeException(e);
    } catch (BytecodeException e) {
      throw computeException(e);
    }
  }

  static PathS toPath(Hash hash) {
    return COMPUTATION_CACHE_PATH.appendPart(hash.toHexString());
  }
}
