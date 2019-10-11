package org.smoothbuild.db.hashed;

import static com.google.common.base.Suppliers.memoize;
import static com.google.common.hash.HashCode.fromBytes;
import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.CodingErrorAction.REPORT;
import static okio.Okio.buffer;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.db.hashed.Hash.hashingSink;
import static org.smoothbuild.io.fs.base.AssertPath.newUnknownPathState;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import org.smoothbuild.io.fs.base.AssertPath;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.io.util.TempManager;

import com.google.common.hash.HashCode;

import okio.BufferedSource;
import okio.ForwardingSink;
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

  public String readString(HashCode hash) throws IOException, DecodingStringException {
    try (BufferedSource source = source(hash)) {
      CharsetDecoder charsetDecoder = CHARSET.newDecoder();
      charsetDecoder.onMalformedInput(REPORT);
      charsetDecoder.onUnmappableCharacter(REPORT);
      return charsetDecoder.decode(wrap(source.readByteArray())).toString();
    } catch (CharacterCodingException e) {
      throw new DecodingStringException(e.getMessage(), e);
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

  public List<HashCode> readHashes(HashCode hash) throws IOException {
    List<HashCode> result = new ArrayList<>();
    try (BufferedSource source = source(hash)) {
      while (!source.exhausted()) {
        result.add(Hash.read(source));
      }
    }
    return result;
  }

  public BufferedSource source(HashCode hash) throws IOException {
    Path path = toPath(hash);
    PathState pathState = fileSystem.pathState(path);
    switch (pathState) {
      case FILE:
        return buffer(fileSystem.source(path));
      case DIR:
        throw new CorruptedHashedDbException(
            "Corrupted HashedDb. " + path + " is a directory not a data file.");
      case NOTHING:
        throw new NoSuchDataException(hash);
      default:
        throw newUnknownPathState(pathState);
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
      // HashingSink.hash() is not idempotent so we need to memoize its result.
      Supplier<HashCode> hashSupplier = memoize(() -> fromBytes(hashing.hash().toByteArray()));
      return newMarshaller(hashing, hashSupplier, tempPath);
    } else {
      return newMarshaller(sink, () -> hash, tempPath);
    }
  }

  private Marshaller newMarshaller(Sink sink, Supplier<HashCode> hashSupplier, Path tempPath) {
    return new Marshaller(moveOnCloseSink(sink, hashSupplier, tempPath), hashSupplier);
  }

  private ForwardingSink moveOnCloseSink(Sink sink, Supplier<HashCode> hashSupplier,
      Path tempPath) {
    return new ForwardingSink(sink) {
      @Override
      public void close() throws IOException {
        super.close();

        Path path = toPath(hashSupplier.get());
        PathState pathState = fileSystem.pathState(path);
        switch (pathState) {
          case NOTHING:
            fileSystem.move(tempPath, path);
          case FILE:
            // nothing to do, we already stored data with such hash so its content must be equal
            return;
          case DIR:
            throw new CorruptedHashedDbException(
                "Corrupted HashedDb. Cannot store data at " + path + " as it is a directory.");
          default:
            throw AssertPath.newUnknownPathState(pathState);
        }
      }
    };
  }

  private Path toPath(HashCode hash) {
    return rootPath.append(Hash.toPath(hash));
  }
}
