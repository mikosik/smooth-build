package org.smoothbuild.vm.java;

import static org.smoothbuild.fs.base.PathState.FILE;
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
    BlobB result = fileCache.get(filePath);
    if (result == null) {
      BlobBBuilder blobBuilder = objDb.blobBuilder();
      if (!fileResolver.pathState(filePath).equals(FILE)) {
        throw new FileNotFoundException();
      }
      blobBuilder.write(sink -> copyAllAndClose(fileResolver.source(filePath), sink));
      result = blobBuilder.build();
      fileCache.put(filePath, result);
    }
    return result;
  }
}
