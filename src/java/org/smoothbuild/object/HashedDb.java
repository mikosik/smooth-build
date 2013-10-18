package org.smoothbuild.object;

import static org.smoothbuild.command.SmoothContants.OBJECTS_DIR;
import static org.smoothbuild.object.HashCodes.toPath;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.fs.base.PathState;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.fs.base.exc.FileSystemError;
import org.smoothbuild.hash.Hash;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.object.err.NoObjectWithGivenHashError;

import com.google.common.base.Charsets;
import com.google.common.hash.HashCode;

@Singleton
public class HashedDb {
  public static final Charset STRING_CHARSET = Charsets.UTF_8;

  private final FileSystem objectsFileSystem;

  @Inject
  public HashedDb(FileSystem fileSystem) {
    this.objectsFileSystem = new SubFileSystem(fileSystem, OBJECTS_DIR);
  }

  public InputStream openInputStream(HashCode hash) {
    Path path = toPath(hash);
    if (objectsFileSystem.pathState(path) == PathState.FILE) {
      return objectsFileSystem.openInputStream(path);
    } else {
      throw new ErrorMessageException(new NoObjectWithGivenHashError(hash));
    }
  }

  public HashCode store(byte[] bytes) {
    HashCode hash = Hash.bytes(bytes);
    Path path = toPath(hash);
    if (objectsFileSystem.pathState(path) != PathState.FILE) {
      store(path, bytes);
    }
    return hash;
  }

  private void store(Path path, byte[] bytes) {
    try (OutputStream outputStream = objectsFileSystem.openOutputStream(path)) {
      outputStream.write(bytes);
    } catch (IOException e) {
      throw new ErrorMessageException(new FileSystemError(e));
    }
  }
}
