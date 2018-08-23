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
import org.smoothbuild.io.fs.base.FileSystemException;
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

  public HashCode writeString(String string) {
    try (Marshaller marshaller = newMarshaller()) {
      marshaller.sink().writeString(string, CHARSET);
      marshaller.close();
      return marshaller.hash();
    } catch (IOException e) {
      throw new HashedDbException("IO error occurred while writing string value.");
    }
  }

  public String readString(HashCode hash) {
    try (Unmarshaller unmarshaller = newUnmarshaller(hash)) {
      return unmarshaller.source().readString(CHARSET);
    } catch (IOException e) {
      throw new HashedDbException("IO error occurred while reading " + hash + " value.");
    }
  }

  public HashCode writeHashes(HashCode... hashes) {
    try (Marshaller marshaller = newMarshaller()) {
      for (HashCode hashCode : hashes) {
        marshaller.sink().write(hashCode.asBytes());
      }
      marshaller.close();
      return marshaller.hash();
    } catch (IOException e) {
      throw new HashedDbException("IO error occurred while writing string value.");
    }
  }

  public List<HashCode> readHashes(HashCode hash) throws NotEnoughBytesException {
    List<HashCode> result = new ArrayList<>();
    try (Unmarshaller unmarshaller = newUnmarshaller(hash)) {
      HashCode elementHash = null;
      while ((elementHash = unmarshaller.tryReadHash()) != null) {
        result.add(elementHash);
      }
    } catch (IOException e) {
      throw new FileSystemException(e);
    }
    return result;
  }

  public Unmarshaller newUnmarshaller(HashCode hash) {
    Path path = toPath(hash);
    if (fileSystem.pathState(path) == PathState.FILE) {
      try {
        return new Unmarshaller(hash, fileSystem.source(path));
      } catch (IOException e) {
        throw new HashedDbException("I/O error.", e);
      }
    } else {
      throw new HashedDbException("Could not find " + hash + " object.");
    }
  }

  public Marshaller newMarshaller() {
    return newMarshaller(null);
  }

  public Marshaller newMarshaller(HashCode hash) {
    Path tempPath = tempManager.tempPath();
    Sink sink;
    try {
      sink = fileSystem.sink(tempPath);
    } catch (IOException e) {
      throw new HashedDbException("I/O error.", e);
    }
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
