package org.smoothbuild.vm.bytecode.load;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import okio.BufferedSource;
import org.smoothbuild.common.filesystem.space.FilePath;
import org.smoothbuild.common.filesystem.space.FileResolver;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.ExprDb;
import org.smoothbuild.vm.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.BlobBBuilder;

/**
 * Stores disk file as BlobB in expression-db.
 * This class is thread-safe.
 */
@Singleton
public class FilePersister {
  private final FileResolver fileResolver;
  private final ExprDb exprDb;
  private final ConcurrentHashMap<FilePath, CachingLoader> fileBlobCache;

  @Inject
  public FilePersister(FileResolver fileResolver, ExprDb exprDb) {
    this.fileResolver = fileResolver;
    this.exprDb = exprDb;
    this.fileBlobCache = new ConcurrentHashMap<>();
  }

  public BlobB persist(FilePath filePath) throws BytecodeException {
    var cachingLoader = fileBlobCache.computeIfAbsent(filePath, CachingLoader::new);
    try {
      return cachingLoader.persist();
    } catch (IOException e) {
      throw new IoBytecodeException(e);
    }
  }

  private class CachingLoader {
    private final FilePath filePath;
    private BlobB blob;

    private CachingLoader(FilePath filePath) {
      this.filePath = filePath;
    }

    public synchronized BlobB persist() throws IOException, BytecodeException {
      if (blob == null) {
        blob = persistImpl();
      }
      return blob;
    }

    private BlobB persistImpl() throws BytecodeException, IOException {
      try (BlobBBuilder blobBuilder = exprDb.blobBuilder()) {
        try (BufferedSource source = fileResolver.source(filePath)) {
          source.readAll(blobBuilder);
        }
        return blobBuilder.build();
      }
    }
  }
}
