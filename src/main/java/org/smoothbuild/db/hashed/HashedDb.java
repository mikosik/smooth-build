package org.smoothbuild.db.hashed;

import static com.google.common.base.Suppliers.memoize;
import static com.google.common.hash.HashCode.fromBytes;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.db.hashed.Hash.hashingSink;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.io.util.TempManager;

import com.google.common.hash.HashCode;

import okio.HashingSink;
import okio.Sink;

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

  public HashCode writeString(String string) throws IOException {
    try (Marshaller marshaller = newMarshaller()) {
      marshaller.sink().writeString(string, CHARSET);
      marshaller.close();
      return marshaller.hash();
    }
  }

  public String readString(HashCode hash) throws IOException {
    try (Unmarshaller unmarshaller = newUnmarshaller(hash)) {
      return unmarshaller.source().readString(CHARSET);
    }
  }

  public HashCode writeHashes(HashCode... hashes) throws IOException {
    try (Marshaller marshaller = newMarshaller()) {
      for (HashCode hashCode : hashes) {
        marshaller.sink().write(hashCode.asBytes());
      }
      marshaller.close();
      return marshaller.hash();
    }
  }

  public List<HashCode> readHashes(HashCode hash) throws NotEnoughBytesException, IOException {
    List<HashCode> result = new ArrayList<>();
    try (Unmarshaller unmarshaller = newUnmarshaller(hash)) {
      HashCode elementHash = null;
      while ((elementHash = unmarshaller.tryReadHash()) != null) {
        result.add(elementHash);
      }
    }
    return result;
  }

  public Unmarshaller newUnmarshaller(HashCode hash) throws IOException {
    Path path = toPath(hash);
    if (fileSystem.pathState(path) == PathState.FILE) {
      return new Unmarshaller(fileSystem.source(path));
    } else {
      throw new IOException("Could not find " + hash + " object.");
    }
  }

  public Marshaller newMarshaller() throws IOException {
    return newMarshaller(null);
  }

  public Marshaller newMarshaller(HashCode hash) throws IOException {
    Path tempPath = tempManager.tempPath();
    Sink sink = fileSystem.sink(tempPath);
    if (hash == null) {
      HashingSink hashing = hashingSink(sink);
      // HashingSink.hash() is idempotent so we need to memoize its result.
      Supplier<HashCode> hashSupplier = memoize(() -> fromBytes(hashing.hash().toByteArray()));
      return newMarshaller(hashing, hashSupplier, tempPath);
    } else {
      return newMarshaller(sink, () -> hash, tempPath);
    }
  }

  private Marshaller newMarshaller(Sink sink, Supplier<HashCode> hashSupplier,
      Path tempPath) {
    FileStorer storer = new FileStorer(fileSystem, rootPath, tempPath, hashSupplier);
    StoringSink storingSink = new StoringSink(sink, storer);
    return new Marshaller(storingSink, hashSupplier);
  }

  private Path toPath(HashCode hash) {
    return rootPath.append(Hash.toPath(hash));
  }
}
