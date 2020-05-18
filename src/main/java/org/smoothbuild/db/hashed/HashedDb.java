package org.smoothbuild.db.hashed;

import static java.lang.String.format;
import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.CodingErrorAction.REPORT;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.io.fs.base.AssertPath.newUnknownPathState;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.io.util.TempManager;

import okio.BufferedSource;

/**
 * This class is thread-safe.
 */
public class HashedDb {
  private final FileSystem fileSystem;
  private final Path rootPath;
  private final TempManager tempManager;

  public HashedDb(FileSystem fileSystem, Path rootPath, TempManager tempManager) {
    this.fileSystem = fileSystem;
    this.rootPath = rootPath;
    this.tempManager = tempManager;
  }


  public Hash writeBoolean(boolean value) throws HashedDbException {
    try (HashingBufferedSink sink = sink()) {
      sink.writeByte(value ? 1 : 0);
      sink.close();
      return sink.hash();
    } catch (IOException e) {
      throw new HashedDbException(e);
    }
  }

  public boolean readBoolean(Hash hash) throws HashedDbException {
    try (BufferedSource source = source(hash)) {
      if (source.exhausted()) {
        throw new DecodingBooleanException(hash);
      }
      byte value = source.readByte();
      if (!source.exhausted()) {
        throw new DecodingBooleanException(hash);
      }
      switch (value) {
        case 0:
          return false;
        case 1:
          return true;
        default:
          throw new DecodingBooleanException(hash);
      }
    } catch (IOException e) {
      throw new HashedDbException(hash, e);
    }
  }

  public Hash writeString(String string) throws HashedDbException {
    try (HashingBufferedSink sink = sink()) {
      sink.writeString(string, CHARSET);
      sink.close();
      return sink.hash();
    } catch (IOException e) {
      throw new HashedDbException(e);
    }
  }

  public String readString(Hash hash) throws HashedDbException {
    try (BufferedSource source = source(hash)) {
      CharsetDecoder charsetDecoder = CHARSET.newDecoder();
      charsetDecoder.onMalformedInput(REPORT);
      charsetDecoder.onUnmappableCharacter(REPORT);
      return charsetDecoder.decode(wrap(source.readByteArray())).toString();
    } catch (CharacterCodingException e) {
      throw new DecodingStringException(hash, e);
    } catch (IOException e) {
      throw new HashedDbException(hash, e);
    }
  }

  public Hash writeHashes(Hash... hashes) throws HashedDbException {
    try (HashingBufferedSink sink = sink()) {
      for (Hash hash : hashes) {
        sink.write(hash);
      }
      sink.close();
      return sink.hash();
    } catch (IOException e) {
      throw new HashedDbException(e);
    }
  }

  public List<Hash> readHashes(Hash hash, int expectedSize) throws HashedDbException {
    List<Hash> hashes = readHashes(hash);
    if (hashes.size() != expectedSize) {
      throw new DecodingHashSequenceException(hash, expectedSize, hashes.size());
    }
    return hashes;
  }

  public List<Hash> readHashes(Hash hash, int minExpectedSize, int maxExpectedSize) throws
      HashedDbException {
    List<Hash> hashes = readHashes(hash);
    if (hashes.size() < minExpectedSize || maxExpectedSize < hashes.size()) {
      throw new DecodingHashSequenceException(
          hash, minExpectedSize, maxExpectedSize, hashes.size());
    }
    return hashes;
  }

  public List<Hash> readHashes(Hash hash) throws HashedDbException {
    List<Hash> result = new ArrayList<>();
    try (BufferedSource source = source(hash)) {
      while (!source.exhausted()) {
        if (source.request(Hash.hashesSize())) {
          result.add(Hash.read(source));
        } else {
          throw new DecodingHashSequenceException(hash);
        }
      }
    } catch (IOException e) {
      throw new HashedDbException(hash, e);
    }
    return result;
  }

  public boolean contains(Hash hash) throws CorruptedHashedDbException {
    Path path = toPath(hash);
    PathState pathState = fileSystem.pathState(path);
    switch (pathState) {
      case FILE:
        return true;
      case DIR:
        throw new CorruptedHashedDbException(
            "Corrupted HashedDb. " + path + " is a directory not a data file.");
      case NOTHING:
        return false;
      default:
        throw newUnknownPathState(pathState);
    }
  }

  public BufferedSource source(Hash hash) throws HashedDbException {
    Path path = toPath(hash);
    PathState pathState = fileSystem.pathState(path);
    switch (pathState) {
      case FILE:
        try {
          return fileSystem.source(path);
        } catch (IOException e) {
          throw new HashedDbException(hash, e);
        }
      case DIR:
        throw new CorruptedHashedDbException(
            format("Corrupted HashedDb at %s. %s is a directory not a data file.", hash, path));
      case NOTHING:
        throw new NoSuchDataException(hash);
      default:
        throw newUnknownPathState(pathState);
    }
  }

  public HashingBufferedSink sink() throws HashedDbException {
    try {
      return new HashingBufferedSink(fileSystem, tempManager.tempPath(), rootPath);
    } catch (IOException e) {
      throw new HashedDbException(e);
    }
  }

  private Path toPath(Hash hash) {
    return dataFullPath(rootPath, hash);
  }

  public static Path dataFullPath(Path hashedDbPath, Hash hash) {
    return hashedDbPath.appendPart(hash.hex());
  }
}
