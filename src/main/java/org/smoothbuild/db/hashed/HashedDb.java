package org.smoothbuild.db.hashed;

import static java.lang.String.format;
import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.CodingErrorAction.REPORT;
import static java.util.Arrays.asList;
import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;

import org.smoothbuild.install.TempManager;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

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

  public Hash writeBigInteger(BigInteger value) throws HashedDbException {
    try (HashingBufferedSink sink = sink()) {
      sink.write(value.toByteArray());
      sink.close();
      return sink.hash();
    } catch (IOException e) {
      throw new HashedDbException(e);
    }
  }

  public BigInteger readBigInteger(Hash hash) throws HashedDbException {
    try (BufferedSource source = source(hash)) {
      byte[] bytes = source.readByteArray();
      if (bytes.length == 0) {
        throw new DecodingBigIntegerException(hash);
      }
      return new BigInteger(bytes);
    } catch (IOException e) {
      throw new HashedDbException(hash, e);
    }
  }

  public Hash writeBoolean(boolean value) throws HashedDbException {
    return writeByte(value ? (byte) 1 : (byte) 0);
  }

  public boolean readBoolean(Hash hash) throws HashedDbException {
    try {
      byte value = readByte(hash);
      return switch (value) {
        case 0 -> false;
        case 1 -> true;
        default -> throw new DecodingBooleanException(hash);
      };
    } catch (DecodingByteException e) {
      throw new DecodingBooleanException(hash, e);
    }
  }

  public Hash writeByte(byte value) throws HashedDbException {
    try (HashingBufferedSink sink = sink()) {
      sink.writeByte(value);
      sink.close();
      return sink.hash();
    } catch (IOException e) {
      throw new HashedDbException(e);
    }
  }

  public byte readByte(Hash hash) throws HashedDbException {
    try (BufferedSource source = source(hash)) {
      if (source.exhausted()) {
        throw new DecodingByteException(hash);
      }
      byte value = source.readByte();
      if (!source.exhausted()) {
        throw new DecodingByteException(hash);
      }
      return value;
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

  public Hash writeSequence(Hash... hashes) throws HashedDbException {
    return writeSequence(asList(hashes));
  }

  public Hash writeSequence(Iterable<Hash> hashes) throws HashedDbException {
    try (HashingBufferedSink sink = sink()) {
      for (Hash hash : hashes) {
        sink.write(hash.toByteString());
      }
      sink.close();
      return sink.hash();
    } catch (IOException e) {
      throw new HashedDbException(e);
    }
  }

  public ImmutableList<Hash> readSequence(Hash hash) throws HashedDbException {
    Builder<Hash> builder = ImmutableList.builder();
    try (BufferedSource source = source(hash)) {
      while (!source.exhausted()) {
        if (source.request(Hash.lengthInBytes())) {
          builder.add(Hash.read(source));
        } else {
          throw new DecodeHashSequenceException(hash, source.readByteArray().length);
        }
      }
    } catch (IOException e) {
      throw new HashedDbException(hash, e);
    }
    return builder.build();
  }

  public boolean contains(Hash hash) throws CorruptedHashedDbException {
    Path path = toPath(hash);
    PathState pathState = fileSystem.pathState(path);
    return switch (pathState) {
      case FILE -> true;
      case DIR -> throw new CorruptedHashedDbException(
          "Corrupted HashedDb. " + path.q() + " is a directory not a data file.");
      case NOTHING -> false;
    };
  }

  public BufferedSource source(Hash hash) throws HashedDbException {
    Path path = toPath(hash);
    PathState pathState = fileSystem.pathState(path);
    return switch (pathState) {
      case FILE -> sourceFile(hash, path);
      case DIR -> throw new CorruptedHashedDbException(
          format("Corrupted HashedDb at %s. %s is a directory not a data file.", hash, path.q()));
      case NOTHING -> throw new NoSuchDataException(hash);
    };
  }

  private BufferedSource sourceFile(Hash hash, Path path) throws HashedDbException {
    try {
      return fileSystem.source(path);
    } catch (IOException e) {
      throw new HashedDbException(hash, e);
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
    return hashedDbPath.appendPart(hash.toHexString());
  }
}
