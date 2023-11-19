package org.smoothbuild.vm.bytecode.load;

import static org.smoothbuild.common.collect.Maps.computeIfAbsent;
import static org.smoothbuild.common.filesystem.base.PathState.FILE;
import static org.smoothbuild.common.io.Okios.copyAllAndClose;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentHashMap;
import org.smoothbuild.filesystem.space.FilePath;
import org.smoothbuild.filesystem.space.FileResolver;
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
  private final ConcurrentHashMap<FilePath, BlobB> fileCache;

  @Inject
  public FileLoader(FileResolver fileResolver, BytecodeDb bytecodeDb) {
    this.fileResolver = fileResolver;
    this.bytecodeDb = bytecodeDb;
    this.fileCache = new ConcurrentHashMap<>();
  }

  public BlobB load(FilePath filePath) throws FileNotFoundException {
    return computeIfAbsent(fileCache, filePath, this::loadImpl);
  }

  private BlobB loadImpl(FilePath filePath) throws FileNotFoundException {
    BlobBBuilder blobBuilder = bytecodeDb.blobBuilder();
    if (!fileResolver.pathState(filePath).equals(FILE)) {
      throw new FileNotFoundException(filePath.q());
    }
    blobBuilder.write(sink -> copyAllAndClose(fileResolver.source(filePath), sink));
    return blobBuilder.build();
  }
}
