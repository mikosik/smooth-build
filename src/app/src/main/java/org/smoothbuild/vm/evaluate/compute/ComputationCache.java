package org.smoothbuild.vm.evaluate.compute;

import static org.smoothbuild.fs.project.ProjectPaths.COMPUTATION_CACHE_PATH;
import static org.smoothbuild.fs.space.Space.PROJECT;
import static org.smoothbuild.run.eval.MessageStruct.containsErrorOrAbove;
import static org.smoothbuild.run.eval.MessageStruct.isValidSeverity;
import static org.smoothbuild.run.eval.MessageStruct.severity;
import static org.smoothbuild.vm.evaluate.compute.ComputationCacheExc.computationCacheException;
import static org.smoothbuild.vm.evaluate.compute.ComputationCacheExc.corruptedValueException;

import java.io.IOException;

import org.smoothbuild.common.fs.base.FileSystem;
import org.smoothbuild.common.fs.base.PathS;
import org.smoothbuild.fs.space.ForSpace;
import org.smoothbuild.vm.bytecode.BytecodeF;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.value.ArrayB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.bytecode.expr.value.ValueB;
import org.smoothbuild.vm.bytecode.hashed.Hash;
import org.smoothbuild.vm.bytecode.type.value.TypeB;
import org.smoothbuild.vm.evaluate.task.Output;

import jakarta.inject.Inject;
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
  public ComputationCache(
      @ForSpace(PROJECT) FileSystem fileSystem,
      BytecodeDb bytecodeDb,
      BytecodeF bytecodeF) {
    this.fileSystem = fileSystem;
    this.bytecodeDb = bytecodeDb;
    this.bytecodeF = bytecodeF;
  }

  public synchronized void write(Hash hash, Output output) throws ComputationCacheExc {
    try (BufferedSink sink = fileSystem.sink(toPath(hash))) {
      var messages = output.messages();
      sink.write(messages.hash().toByteString());
      var valueB = output.valueB();
      if (valueB != null) {
        sink.write(valueB.hash().toByteString());
      }
    } catch (IOException e) {
      throw computationCacheException(e);
    }
  }

  public synchronized boolean contains(Hash hash) throws ComputationCacheExc {
    var path = toPath(hash);
    return switch (fileSystem.pathState(path)) {
      case FILE -> true;
      case NOTHING -> false;
      case DIR -> throw corruptedValueException(hash, path + " is directory not a file.");
    };
  }

  public synchronized Output read(Hash hash, TypeB type) throws ComputationCacheExc {
    try (BufferedSource source = fileSystem.source(toPath(hash))) {
      var messagesHash = Hash.read(source);
      var messages = bytecodeDb.get(messagesHash);
      var messageArrayT = bytecodeF.arrayT(bytecodeF.messageT());
      if (!messages.category().equals(messageArrayT)) {
        throw corruptedValueException(hash, "Expected " + messageArrayT.q()
            + " as first child of its Merkle root, but got " + messages.category().q());
      }

      var messageArray = (ArrayB) messages;
      for (var message : messageArray.elems(TupleB.class)) {
        var severity = severity(message);
        if (!isValidSeverity(severity)) {
          throw corruptedValueException(hash,
              "One of messages has invalid severity = '" + severity + "'");
        }
      }
      if (containsErrorOrAbove(messageArray)) {
        return new Output(null, messageArray);
      } else {
        var valueHash = Hash.read(source);
        var value = bytecodeDb.get(valueHash);
        if (!type.equals(value.evaluationT())) {
          throw corruptedValueException(hash, "Expected value of type " + type.q()
              + " as second child of its Merkle root, but got " + value.evaluationT().q());
        } else {
          return new Output((ValueB) value, messageArray);
        }
      }
    } catch (IOException e) {
      throw computationCacheException(e);
    }
  }

  static PathS toPath(Hash hash) {
    return COMPUTATION_CACHE_PATH.appendPart(hash.toHexString());
  }
}
