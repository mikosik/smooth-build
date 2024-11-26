package org.smoothbuild.virtualmachine.evaluate.compute;

import static okio.Okio.buffer;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.containsErrorOrAbove;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.isValidLevel;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.levelAsString;
import static org.smoothbuild.virtualmachine.evaluate.compute.ComputeCacheException.corruptedValueException;
import static org.smoothbuild.virtualmachine.evaluate.step.BOutput.bOutput;

import com.google.common.annotations.VisibleForTesting;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import okio.BufferedSink;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.Path;
import org.smoothbuild.common.filesystem.base.PathState;
import org.smoothbuild.virtualmachine.bytecode.BytecodeFactory;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.bytecode.kind.base.BType;
import org.smoothbuild.virtualmachine.evaluate.step.BOutput;
import org.smoothbuild.virtualmachine.wire.ComputationDb;

/**
 * This class is thread-safe.
 */
@Singleton
public class ComputationCache {
  private final FileSystem<Path> fileSystem;
  private final BExprDb exprDb;
  private final BytecodeFactory bytecodeFactory;

  @Inject
  public ComputationCache(
      @ComputationDb FileSystem<Path> fileSystem, BExprDb exprDb, BytecodeFactory bytecodeFactory) {
    this.fileSystem = fileSystem;
    this.exprDb = exprDb;
    this.bytecodeFactory = bytecodeFactory;
  }

  void initialize() throws IOException {
    fileSystem.createDir(Path.root());
  }

  public synchronized void write(Hash hash, BOutput bOutput) throws IOException {
    try (BufferedSink sink = buffer(fileSystem.sink(toPath(hash)))) {
      var storedLogs = bOutput.storedLogs();
      sink.write(storedLogs.hash().toByteString());
      var value = bOutput.value();
      if (value != null) {
        sink.write(value.hash().toByteString());
      }
    }
  }

  public synchronized boolean contains(Hash hash) throws IOException {
    var path = toPath(hash);
    return switch (stateOf(path)) {
      case FILE -> true;
      case NOTHING -> false;
      case DIR -> throw corruptedValueException(hash, path + " is directory not a file.");
    };
  }

  private PathState stateOf(Path path) throws IOException {
    return fileSystem.pathState(path);
  }

  public synchronized BOutput read(Hash hash, BType type) throws IOException {
    try (var source = buffer(fileSystem.source(toPath(hash)))) {
      var storedLogsHash = Hash.read(source);
      var storedLogs = exprDb.get(storedLogsHash);
      var storedLogsArrayType = bytecodeFactory.arrayType(bytecodeFactory.storedLogType());
      if (!storedLogs.kind().equals(storedLogsArrayType)) {
        throw corruptedValueException(
            hash,
            "Expected " + storedLogsArrayType.q() + " as first child of its Merkle root, but got "
                + storedLogs.kind().q());
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
        return bOutput(null, storedLogArray);
      } else {
        var valueHash = Hash.read(source);
        var value = exprDb.get(valueHash);
        if (!type.equals(value.evaluationType())) {
          throw corruptedValueException(
              hash,
              "Expected value of type " + type.q() + " as second child of its Merkle root, but got "
                  + value.evaluationType().q());
        } else {
          return bOutput((BValue) value, storedLogArray);
        }
      }
    }
  }

  @VisibleForTesting
  Path toPath(Hash hash) {
    return path(hash.toHexString());
  }
}
