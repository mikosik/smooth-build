package org.smoothbuild.load;

import static org.smoothbuild.fs.base.PathState.FILE;
import static org.smoothbuild.util.collect.Maps.computeIfAbsent;
import static org.smoothbuild.util.io.Okios.copyAllAndClose;

import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.bytecode.obj.ObjDb;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.BlobBBuilder;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.fs.space.FileResolver;
import org.smoothbuild.util.collect.Maps;

/**
 * This class is thread-safe.
 */
@Singleton
public class FileLoader {
  private final FileResolver fileResolver;
  private final ObjDb objDb;
  private final ConcurrentHashMap<FilePath, BlobB> fileCache;

  @Inject
  public FileLoader(FileResolver fileResolver, ObjDb objDb) {
    this.fileResolver = fileResolver;
    this.objDb = objDb;
    this.fileCache = new ConcurrentHashMap<>();
  }

  public BlobB load(FilePath filePath) throws FileNotFoundException {
    return computeIfAbsent(fileCache, filePath, this::loadImpl);
  }

  private BlobB loadImpl(FilePath filePath) throws FileNotFoundException {
    BlobBBuilder blobBuilder = objDb.blobBuilder();
    if (!fileResolver.pathState(filePath).equals(FILE)) {
      throw new FileNotFoundException(filePath.q());
    }
    blobBuilder.write(sink -> copyAllAndClose(fileResolver.source(filePath), sink));
    return blobBuilder.build();
  }
}
