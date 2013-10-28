package org.smoothbuild.object;

import static org.smoothbuild.object.HashCodes.toPath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.PathState;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.object.err.NoObjectWithGivenHashError;
import org.smoothbuild.object.err.ReadingHashedObjectFailedError;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;

public class HashedDb {
  public static final Charset STRING_CHARSET = Charsets.UTF_8;

  private final FileSystem dbFileSystem;

  public HashedDb(FileSystem dbFileSystem) {
    this.dbFileSystem = dbFileSystem;
  }

  public boolean contains(HashCode hash) {
    Path path = toPath(hash);
    return dbFileSystem.pathState(path) == PathState.FILE;
  }

  public InputStream openInputStream(HashCode hash) {
    Path path = toPath(hash);
    if (dbFileSystem.pathState(path) == PathState.FILE) {
      return dbFileSystem.openInputStream(path);
    } else {
      throw new ErrorMessageException(new NoObjectWithGivenHashError(hash));
    }
  }

  public HashCode store(byte[] bytes) {
    return store(Hash.bytes(bytes), bytes);
  }

  public HashCode store(HashCode hash, byte[] bytes) {
    Path path = toPath(hash);

    if (dbFileSystem.pathState(path) == PathState.FILE) {
      return hash;
    }

    try (OutputStream outputStream = dbFileSystem.openOutputStream(path)) {
      outputStream.write(bytes);
    } catch (IOException e) {
      throw new ErrorMessageException(new ReadingHashedObjectFailedError(hash, e));
    }

    return hash;
  }
}
