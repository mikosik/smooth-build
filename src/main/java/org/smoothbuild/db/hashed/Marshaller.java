package org.smoothbuild.db.hashed;

import static org.smoothbuild.db.hashed.Hash.toPath;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;

import java.io.IOException;
import java.io.OutputStream;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;

import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class Marshaller extends OutputStream {
  private final FileSystem fileSystem;
  private final Path rootPath;
  private final Path tempPath;
  private HashCode hash;
  private final OutputStream outputStream;
  private final Hasher hasher;

  public Marshaller(FileSystem fileSystem, Path rootPath, Path tempPath, HashCode hash) {
    this.fileSystem = fileSystem;
    this.rootPath = rootPath;
    this.tempPath = tempPath;
    this.hash = hash;
    this.hasher = Hash.newHasher();
    this.outputStream = fileSystem.openOutputStream(tempPath);
  }

  public Marshaller writeHash(HashCode hash) {
    write(hash.asBytes());
    return this;
  }

  @Override
  public void write(int b) {
    write(new byte[] { (byte) b });
  }

  @Override
  public void write(byte b[]) {
    write(b, 0, b.length);
  }

  @Override
  public void write(byte bytes[], int off, int len) {
    hasher.putBytes(bytes, off, len);
    try {
      outputStream.write(bytes, off, len);
    } catch (IOException e) {
      throw newHashedDbException(hash, e);
    }
  }

  @Override
  public void close() {
    if (hash == null) {
      hash = hasher.hash();
    }
    try {
      outputStream.close();
    } catch (IOException e) {
      newHashedDbException(hash, e);
    }
    Path path = rootPath.append(toPath(hash));
    if (fileSystem.pathState(path) == NOTHING) {
      fileSystem.move(tempPath, path);
    }
  }

  public HashCode hash() {
    return hash;
  }

  private static HashedDbException newHashedDbException(HashCode hash, Throwable e) {
    return new HashedDbException("IO error occurred while writing " + hash + " object.", e);
  }
}
