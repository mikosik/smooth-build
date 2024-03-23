package org.smoothbuild.virtualmachine.evaluate.compute;

import static org.smoothbuild.common.bucket.base.Path.path;
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
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BValue;
import org.smoothbuild.virtualmachine.bytecode.type.value.BType;
import org.smoothbuild.virtualmachine.evaluate.task.Output;
import org.smoothbuild.virtualmachine.wire.ComputationDb;

/**
 * This class is thread-safe.
 */
public class ComputationCache {
  private final Bucket bucket;
  private final ExprDb exprDb;
  private final BytecodeFactory bytecodeFactory;

  @Inject
  public ComputationCache(
      @ComputationDb Bucket bucket, ExprDb exprDb, BytecodeFactory bytecodeFactory) {
    this.bucket = bucket;
    this.exprDb = exprDb;
    this.bytecodeFactory = bytecodeFactory;
  }

  public synchronized void write(Hash hash, Output output) throws ComputeException {
    try (BufferedSink sink = bucket.sink(toPath(hash))) {
      var storedLogs = output.storedLogs();
      sink.write(storedLogs.hash().toByteString());
      var value = output.value();
      if (value != null) {
        sink.write(value.hash().toByteString());
      }
    } catch (IOException e) {
      throw computeException(e);
    }
  }

  public synchronized boolean contains(Hash hash) throws ComputeException {
    var path = toPath(hash);
    return switch (bucket.pathState(path)) {
      case FILE -> true;
      case NOTHING -> false;
      case DIR -> throw corruptedValueException(hash, path + " is directory not a file.");
    };
  }

  public synchronized Output read(Hash hash, BType type) throws ComputeException {
    try (BufferedSource source = bucket.source(toPath(hash))) {
      var storedLogsHash = Hash.read(source);
      var storedLogs = exprDb.get(storedLogsHash);
      var storedLogsArrayType = bytecodeFactory.arrayType(bytecodeFactory.storedLogType());
      if (!storedLogs.category().equals(storedLogsArrayType)) {
        throw corruptedValueException(
            hash,
            "Expected " + storedLogsArrayType.q() + " as first child of its Merkle root, but got "
                + storedLogs.category().q());
      }

      var storedLogArray = (BArray) storedLogs;
      for (var storedLog : storedLogArray.elements(BTuple.class)) {
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
          return new Output((BValue) value, storedLogArray);
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
