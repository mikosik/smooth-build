package org.smoothbuild.virtualmachine.bytecode.load;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import okio.BufferedSource;
import org.smoothbuild.common.filesystem.base.FileResolver;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.ExprDb;
import org.smoothbuild.virtualmachine.bytecode.expr.exc.IoBytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.BlobBBuilder;

/**
 * Stores disk file as BlobB in expression-db.
 * This class is thread-safe.
 */
@Singleton
public class FilePersister {
  private final FileResolver fileResolver;
  private final ExprDb exprDb;
  private final ConcurrentHashMap<FullPath, CachingLoader> fileBlobCache;

  @Inject
  public FilePersister(FileResolver fileResolver, ExprDb exprDb) {
    this.fileResolver = fileResolver;
    this.exprDb = exprDb;
    this.fileBlobCache = new ConcurrentHashMap<>();
  }

  public BlobB persist(FullPath fullPath) throws BytecodeException {
    var cachingLoader = fileBlobCache.computeIfAbsent(fullPath, CachingLoader::new);
    try {
      return cachingLoader.persist();
    } catch (IOException e) {
      throw new IoBytecodeException(e);
    }
  }

  private class CachingLoader {
    private final FullPath fullPath;
    private BlobB blob;

    private CachingLoader(FullPath fullPath) {
      this.fullPath = fullPath;
    }

    public synchronized BlobB persist() throws IOException, BytecodeException {
      if (blob == null) {
        blob = persistImpl();
      }
      return blob;
    }

    private BlobB persistImpl() throws BytecodeException, IOException {
      try (BlobBBuilder blobBuilder = exprDb.blobBuilder()) {
        try (BufferedSource source = fileResolver.source(fullPath)) {
          source.readAll(blobBuilder);
        }
        return blobBuilder.build();
      }
    }
  }
}
