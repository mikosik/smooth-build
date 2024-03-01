package org.smoothbuild.virtualmachine.evaluate.compute;

import static org.smoothbuild.virtualmachine.bytecode.helper.MessageStruct.containsErrorOrAbove;
import static org.smoothbuild.virtualmachine.bytecode.helper.MessageStruct.isValidSeverity;
import static org.smoothbuild.virtualmachine.bytecode.helper.MessageStruct.severity;
import static org.smoothbuild.virtualmachine.evaluate.compute.ComputeException.computeException;
import static org.smoothbuild.virtualmachine.evaluate.compute.ComputeException.corruptedValueException;

import com.google.common.annotations.VisibleForTesting;
import jakarta.inject.Inject;
import java.io.IOException;
import okio.BufferedSink;
import okio.BufferedSource;
import org.smoothbuild.common.Hash;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;
import org.smoothbuild.virtualmachine.evaluate.task.Output;

/**
 * This class is thread-safe.
 */
public class ComputationCache {
  private final FileSystem fileSystem;
  private final PathS diskCachePath;
  private final ExprDb exprDb;
  private final BytecodeF bytecodeF;

  @Inject
  public ComputationCache(ComputationCacheConfig config, ExprDb exprDb, BytecodeF bytecodeF) {
    this.fileSystem = config.fileSystem();
    this.diskCachePath = config.diskCachePath();
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
      for (var message : messageArray.elements(TupleB.class)) {
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

  @VisibleForTesting
  PathS toPath(Hash hash) {
    return diskCachePath.appendPart(hash.toHexString());
  }
}
