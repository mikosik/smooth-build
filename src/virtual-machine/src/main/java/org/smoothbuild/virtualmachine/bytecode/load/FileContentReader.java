package org.smoothbuild.virtualmachine.bytecode.load;

import static okio.Okio.buffer;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.BExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlob;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BBlobBuilder;

/**
 * Stores disk file as BlobB in expression-db.
 * This class is thread-safe.
 */
@Singleton
public class FileContentReader {
  private final FileSystem<FullPath> fileSystem;
  private final BExprDb exprDb;
  private final ConcurrentHashMap<FullPath, CachingReader> cache;

  @Inject
  public FileContentReader(FileSystem<FullPath> fileSystem, BExprDb exprDb) {
    this.fileSystem = fileSystem;
    this.exprDb = exprDb;
    this.cache = new ConcurrentHashMap<>();
  }

  public BBlob read(FullPath fullPath) throws BytecodeException, IOException {
    var cachingLoader = cache.computeIfAbsent(fullPath, CachingReader::new);
    return cachingLoader.read();
  }

  private class CachingReader {
    private final FullPath fullPath;
    private BBlob blob;

    private CachingReader(FullPath fullPath) {
      this.fullPath = fullPath;
    }

    public synchronized BBlob read() throws IOException, BytecodeException {
      if (blob == null) {
        blob = readImpl();
      }
      return blob;
    }

    private BBlob readImpl() throws BytecodeException, IOException {
      try (BBlobBuilder blobBuilder = exprDb.newBlobBuilder()) {
        try (var source = buffer(fileSystem.source(fullPath))) {
          source.readAll(blobBuilder);
        }
        return blobBuilder.build();
      }
    }
  }
}
