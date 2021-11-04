package org.smoothbuild.exec.java;

import static org.smoothbuild.util.io.Okios.copyAllAndClose;

import java.util.concurrent.ConcurrentHashMap;

import org.smoothbuild.db.object.obj.ObjDb;
import org.smoothbuild.db.object.obj.val.Blob;
import org.smoothbuild.db.object.obj.val.BlobBuilder;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.io.fs.space.FileResolver;

public class FileLoader {
  private final FileResolver fileResolver;
  private final ObjDb objDb;
  private final ConcurrentHashMap<FilePath, Blob> fileCache;

  public FileLoader(FileResolver fileResolver, ObjDb objDb) {
    this.fileResolver = fileResolver;
    this.objDb = objDb;
    this.fileCache = new ConcurrentHashMap<>();
  }

  public Blob load(FilePath filePath) {
    Blob result = fileCache.get(filePath);
    if (result == null) {
      BlobBuilder blobBuilder = objDb.blobBuilder();
      blobBuilder.write(sink -> copyAllAndClose(fileResolver.source(filePath), sink));
      result = blobBuilder.build();
      fileCache.put(filePath, result);
    }
    return result;
  }
}
