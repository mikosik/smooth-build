package org.smoothbuild.exec.java;

import static org.smoothbuild.util.io.Okios.copyAllAndClose;

import java.util.concurrent.ConcurrentHashMap;

import org.smoothbuild.db.object.obj.ObjectHDb;
import org.smoothbuild.db.object.obj.val.BlobH;
import org.smoothbuild.db.object.obj.val.BlobHBuilder;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.io.fs.space.FileResolver;

public class FileLoader {
  private final FileResolver fileResolver;
  private final ObjectHDb objectHDb;
  private final ConcurrentHashMap<FilePath, BlobH> fileCache;

  public FileLoader(FileResolver fileResolver, ObjectHDb objectHDb) {
    this.fileResolver = fileResolver;
    this.objectHDb = objectHDb;
    this.fileCache = new ConcurrentHashMap<>();
  }

  public BlobH load(FilePath filePath) {
    BlobH result = fileCache.get(filePath);
    if (result == null) {
      BlobHBuilder blobBuilder = objectHDb.blobBuilder();
      blobBuilder.write(sink -> copyAllAndClose(fileResolver.source(filePath), sink));
      result = blobBuilder.build();
      fileCache.put(filePath, result);
    }
    return result;
  }
}
