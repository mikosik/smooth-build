package org.smoothbuild.exec.java;

import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.util.io.Okios.copyAllAndClose;

import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BlobHBuilder;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.io.fs.space.FileResolver;

/**
 * This class is thread-safe.
 */
@Singleton
public class FileLoader {
  private final FileResolver fileResolver;
  private final ObjectHDb objectHDb;
  private final ConcurrentHashMap<FilePath, BlobH> fileCache;
  private final ConcurrentHashMap<Hash, FilePath> hashToFilePath;

  @Inject
  public FileLoader(FileResolver fileResolver, ObjectHDb objectHDb) {
    this.fileResolver = fileResolver;
    this.objectHDb = objectHDb;
    this.fileCache = new ConcurrentHashMap<>();
    this.hashToFilePath = new ConcurrentHashMap<>();
  }

  public BlobH load(FilePath filePath) throws FileNotFoundException {
    BlobH result = fileCache.get(filePath);
    if (result == null) {
      BlobHBuilder blobBuilder = objectHDb.blobBuilder();
      if (!fileResolver.pathState(filePath).equals(FILE)) {
        throw new FileNotFoundException();
      }
      blobBuilder.write(sink -> copyAllAndClose(fileResolver.source(filePath), sink));
      result = blobBuilder.build();
      fileCache.put(filePath, result);
      hashToFilePath.put(result.hash(), filePath);
    }
    return result;
  }

  public FilePath filePathOf(Hash hash) {
    return hashToFilePath.get(hash);
  }
}
