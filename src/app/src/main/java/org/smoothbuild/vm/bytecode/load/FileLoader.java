package org.smoothbuild.vm.bytecode.load;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import okio.BufferedSource;
import org.smoothbuild.filesystem.space.FilePath;
import org.smoothbuild.filesystem.space.FileResolver;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.BytecodeDb;
import org.smoothbuild.vm.bytecode.expr.value.BlobB;
import org.smoothbuild.vm.bytecode.expr.value.BlobBBuilder;

/**
 * This class is thread-safe.
 */
@Singleton
public class FileLoader {
  private final FileResolver fileResolver;
  private final BytecodeDb bytecodeDb;
  private final ConcurrentHashMap<FilePath, CachingLoader> fileCache;

  @Inject
  public FileLoader(FileResolver fileResolver, BytecodeDb bytecodeDb) {
    this.fileResolver = fileResolver;
    this.bytecodeDb = bytecodeDb;
    this.fileCache = new ConcurrentHashMap<>();
  }

  public BlobB load(FilePath filePath) throws IOException, BytecodeException {
    var cachingLoader = fileCache.computeIfAbsent(filePath, CachingLoader::new);
    return cachingLoader.load();
  }

  private class CachingLoader {
    private final FilePath filePath;
    private BlobB blob;

    private CachingLoader(FilePath filePath) {
      this.filePath = filePath;
    }

    public synchronized BlobB load() throws IOException, BytecodeException {
      if (blob == null) {
        blob = loadImpl();
      }
      return blob;
    }

    private BlobB loadImpl() throws BytecodeException, IOException {
      try (BlobBBuilder blobBuilder = bytecodeDb.blobBuilder()) {
        try (BufferedSource source = fileResolver.source(filePath)) {
          source.readAll(blobBuilder);
        }
        return blobBuilder.build();
      }
    }
  }
}
