package org.smoothbuild.db.hashed;

import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.util.Streams.inputStreamToString;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.io.util.TempManager;

import com.google.common.hash.HashCode;

public class HashedDb {
  private final FileSystem fileSystem;
  private final Path rootPath;
  private final TempManager tempManager;

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

  public HashCode writeHashes(HashCode... hashes) {
    try (Marshaller marshaller = newMarshaller()) {
      for (HashCode hashCode : hashes) {
        marshaller.writeHash(hashCode);
      }
      marshaller.close();
      return marshaller.hash();
    }
  }

  public List<HashCode> readHashes(HashCode hash) {
    List<HashCode> result = new ArrayList<>();
    try (Unmarshaller unmarshaller = newUnmarshaller(hash)) {
      HashCode elementHash = null;
      while ((elementHash = unmarshaller.tryReadHash()) != null) {
        result.add(elementHash);
      }
    }
    return result;
  }

  public Unmarshaller newUnmarshaller(HashCode hash) {
    Path path = toPath(hash);
    if (fileSystem.pathState(path) == PathState.FILE) {
      return new Unmarshaller(hash, fileSystem.source(path).inputStream());
    } else {
      throw new HashedDbException("Could not find " + hash + " object.");
    }
  }

  public Marshaller newMarshaller() {
    return newMarshaller(null);
  }

  public Marshaller newMarshaller(HashCode hash) {
    Path tempPath = tempManager.tempPath();
    OutputStream outputStream = fileSystem.sink(tempPath).outputStream();
    if (hash == null) {
      HashingOutputStream hashing = new HashingOutputStream(outputStream);
      return newMarshaller(hashing, () -> hashing.hash(), tempPath);
    } else {
      return newMarshaller(outputStream, () -> hash, tempPath);
    }
  }

  private Marshaller newMarshaller(OutputStream outputStream, Supplier<HashCode> hashSupplier,
      Path tempPath) {
    FileStorer storer = new FileStorer(fileSystem, rootPath, tempPath, hashSupplier);
    StoringOutputStream storingOutputStream = new StoringOutputStream(outputStream, storer);
    return new Marshaller(storingOutputStream, hashSupplier);
  }

  private Path toPath(HashCode hash) {
    return rootPath.append(Hash.toPath(hash));
  }
}
