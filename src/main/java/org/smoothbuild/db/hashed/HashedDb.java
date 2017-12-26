package org.smoothbuild.db.hashed;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.IOException;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.io.fs.mem.MemoryFileSystem;
import org.smoothbuild.io.util.TempManager;

import com.google.common.hash.HashCode;

public class HashedDb {
  private final FileSystem fileSystem;
  private final Path rootPath;
  private final TempManager tempManager;

  public HashedDb() {
    this(new MemoryFileSystem());
  }

  private HashedDb(FileSystem fileSystem) {
    this(fileSystem, Path.root(), new TempManager(fileSystem));
  }

  public HashedDb(FileSystem fileSystem, Path rootPath, TempManager tempManager) {
    this.fileSystem = fileSystem;
    this.rootPath = rootPath;
    this.tempManager = tempManager;
  }

  public boolean contains(HashCode hash) {
    Path path = toPath(hash);
    return fileSystem.pathState(path) == PathState.FILE;
  }

  public HashCode writeString(String string) {
    Marshaller marshaller = newMarshaller();
    marshaller.write(string.getBytes(CHARSET));
    marshaller.close();
    return marshaller.hash();
  }

  public String readString(HashCode hash) {
    try {
      return inputStreamToString(newUnmarshaller(hash));
    } catch (IOException e) {
      throw new HashedDbException("IO error occurred while reading " + hash + " value.");
    }
  }

  public Unmarshaller newUnmarshaller(HashCode hash) {
    Path path = toPath(hash);
    if (fileSystem.pathState(path) == PathState.FILE) {
      return new Unmarshaller(hash, fileSystem.openInputStream(path));
    } else {
      throw new HashedDbException("Could not find " + hash + " object.");
    }
  }

  public Marshaller newMarshaller() {
    return newMarshaller(null);
  }

  public Marshaller newMarshaller(HashCode hash) {
    return new Marshaller(fileSystem, rootPath, tempManager.tempPath(), hash);
  }

  private Path toPath(HashCode hash) {
    return rootPath.append(Hash.toPath(hash));
  }
}
