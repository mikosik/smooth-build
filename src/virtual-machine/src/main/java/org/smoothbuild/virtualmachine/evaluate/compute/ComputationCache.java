package org.smoothbuild.virtualmachine.evaluate.compute;

import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.containsErrorOrAbove;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.isValidLevel;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.levelAsString;
import static org.smoothbuild.virtualmachine.evaluate.compute.ComputeException.computeException;
import static org.smoothbuild.virtualmachine.evaluate.compute.ComputeException.corruptedValueException;

import com.google.common.annotations.VisibleForTesting;
import jakarta.inject.Inject;
import java.io.IOException;
import okio.BufferedSink;
import okio.BufferedSource;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeF;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ArrayB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.ValueB;
import org.smoothbuild.virtualmachine.bytecode.type.value.TypeB;
import org.smoothbuild.virtualmachine.evaluate.task.Output;
import org.smoothbuild.virtualmachine.wire.ComputationDb;

/**
 * This class is thread-safe.
 */
public class ComputationCache {
  private final FileSystem fileSystem;
  private final ExprDb exprDb;
  private final BytecodeF bytecodeF;

  @Inject
  public ComputationCache(
      @ComputationDb FileSystem fileSystem, ExprDb exprDb, BytecodeF bytecodeF) {
    this.fileSystem = fileSystem;
    this.exprDb = exprDb;
    this.bytecodeF = bytecodeF;
  }

  public synchronized void write(Hash hash, Output output) throws ComputeException {
    try (BufferedSink sink = fileSystem.sink(toPath(hash))) {
      var storedLogs = output.storedLogs();
      sink.write(storedLogs.hash().toByteString());
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
      var storedLogsHash = Hash.read(source);
      var storedLogs = exprDb.get(storedLogsHash);
      var storedLogsArrayT = bytecodeF.arrayType(bytecodeF.storedLogType());
      if (!storedLogs.category().equals(storedLogsArrayT)) {
        throw corruptedValueException(
            hash,
            "Expected " + storedLogsArrayT.q() + " as first child of its Merkle root, but got "
                + storedLogs.category().q());
      }

      var storedLogArray = (ArrayB) storedLogs;
      for (var storedLog : storedLogArray.elements(TupleB.class)) {
        var level = levelAsString(storedLog);
        if (!isValidLevel(level)) {
          throw corruptedValueException(
              hash, "One of storedLogs has invalid level = '" + level + "'");
        }
      }
      if (containsErrorOrAbove(storedLogArray)) {
        return new Output(null, storedLogArray);
      } else {
        var valueHash = Hash.read(source);
        var value = exprDb.get(valueHash);
        if (!type.equals(value.evaluationType())) {
          throw corruptedValueException(
              hash,
              "Expected value of type " + type.q() + " as second child of its Merkle root, but got "
                  + value.evaluationType().q());
        } else {
          return new Output((ValueB) value, storedLogArray);
        }
      }
    } catch (IOException e) {
      throw computeException(e);
    } catch (BytecodeException e) {
      throw computeException(e);
    }
  }

  @VisibleForTesting
  Path toPath(Hash hash) {
    return path(hash.toHexString());
  }
}
