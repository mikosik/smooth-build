package org.smoothbuild.vm.java;

import static org.smoothbuild.io.fs.base.PathState.FILE;
import static org.smoothbuild.util.io.Okios.copyAllAndClose;

import java.io.FileNotFoundException;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.bytecode.obj.ByteDb;
import org.smoothbuild.bytecode.obj.val.BlobB;
import org.smoothbuild.bytecode.obj.val.BlobBBuilder;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.io.fs.space.FilePath;
import org.smoothbuild.io.fs.space.FileResolver;

/**
 * This class is thread-safe.
 */
@Singleton
public class FileLoader {
  private final FileResolver fileResolver;
  private final ByteDb byteDb;
  private final ConcurrentHashMap<FilePath, BlobB> fileCache;
  private final ConcurrentHashMap<Hash, FilePath> hashToFilePath;

  @Inject
  public FileLoader(FileResolver fileResolver, ByteDb byteDb) {
    this.fileResolver = fileResolver;
    this.byteDb = byteDb;
    this.fileCache = new ConcurrentHashMap<>();
    this.hashToFilePath = new ConcurrentHashMap<>();
  }

  public BlobB load(FilePath filePath) throws FileNotFoundException {
    BlobB result = fileCache.get(filePath);
    if (result == null) {
      BlobBBuilder blobBuilder = byteDb.blobBuilder();
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
