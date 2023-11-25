package org.smoothbuild.vm.bytecode.hashed;

import static java.lang.String.format;
import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.CodingErrorAction.REPORT;
import static java.util.Arrays.asList;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.filesystem.base.PathS.path;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import okio.BufferedSource;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.concurrent.AtomicBigInteger;
import org.smoothbuild.common.filesystem.base.FileSystem;
import org.smoothbuild.common.filesystem.base.PathS;
import org.smoothbuild.vm.bytecode.hashed.exc.CorruptedHashedDbException;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeBigIntegerException;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeBooleanException;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeByteException;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeHashSeqException;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeStringException;
import org.smoothbuild.vm.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.vm.bytecode.hashed.exc.NoSuchDataException;

/**
 * This class is thread-safe.
 */
public class HashedDb {
  static final PathS TEMP_DIR_PATH = path("tmp");
  private final FileSystem fileSystem;
  private final AtomicBigInteger tempFileCounter = new AtomicBigInteger();

  public HashedDb(FileSystem fileSystem) {
    this.fileSystem = fileSystem;
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
        throw new DecodeBigIntegerException(hash);
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
        default -> throw new DecodeBooleanException(hash);
      };
    } catch (DecodeByteException e) {
      throw new DecodeBooleanException(hash, e);
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
        throw new DecodeByteException(hash);
      }
      byte value = source.readByte();
      if (!source.exhausted()) {
        throw new DecodeByteException(hash);
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
      throw new DecodeStringException(hash, e);
    } catch (IOException e) {
      throw new HashedDbException(hash, e);
    }
  }

  public Hash writeSeq(Hash... hashes) throws HashedDbException {
    return writeSeq(asList(hashes));
  }

  public Hash writeSeq(Iterable<Hash> hashes) throws HashedDbException {
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

  public long readSeqSize(Hash hash) throws HashedDbException {
    var path = dbPathTo(hash);
    var pathState = fileSystem.pathState(path);
    return switch (pathState) {
      case FILE -> readSeqSize(hash, path);
      case DIR -> throw new CorruptedHashedDbException(
          format("Corrupted HashedDb at %s. %s is a directory not a data file.", hash, path.q()));
      case NOTHING -> throw new NoSuchDataException(hash);
    };
  }

  private long readSeqSize(Hash hash, PathS path) throws HashedDbException {
    try {
      var sizeInBytes = fileSystem.size(path);
      long remainder = sizeInBytes % Hash.lengthInBytes();
      if (remainder == 0) {
        return sizeInBytes / Hash.lengthInBytes();
      } else {
        throw new DecodeHashSeqException(hash, remainder);
      }
    } catch (IOException e) {
      throw new HashedDbException(hash, e);
    }
  }

  public List<Hash> readSeq(Hash hash) throws HashedDbException {
    var builder = new ArrayList<Hash>();
    try (BufferedSource source = source(hash)) {
      while (!source.exhausted()) {
        if (source.request(Hash.lengthInBytes())) {
          builder.add(Hash.read(source));
        } else {
          throw new DecodeHashSeqException(hash, source.readByteArray().length);
        }
      }
    } catch (IOException e) {
      throw new HashedDbException(hash, e);
    }
    return list(builder);
  }

  public boolean contains(Hash hash) throws CorruptedHashedDbException {
    var path = dbPathTo(hash);
    var pathState = fileSystem.pathState(path);
    return switch (pathState) {
      case FILE -> true;
      case DIR -> throw new CorruptedHashedDbException(
          "Corrupted HashedDb. " + path.q() + " is a directory not a data file.");
      case NOTHING -> false;
    };
  }

  public BufferedSource source(Hash hash) throws HashedDbException {
    var path = dbPathTo(hash);
    var pathState = fileSystem.pathState(path);
    return switch (pathState) {
      case FILE -> sourceFile(hash, path);
      case DIR -> throw new CorruptedHashedDbException(
          format("Corrupted HashedDb at %s. %s is a directory not a data file.", hash, path.q()));
      case NOTHING -> throw new NoSuchDataException(hash);
    };
  }

  private BufferedSource sourceFile(Hash hash, PathS path) throws HashedDbException {
    try {
      return fileSystem.source(path);
    } catch (IOException e) {
      throw new HashedDbException(hash, e);
    }
  }

  public HashingBufferedSink sink() throws HashedDbException {
    try {
      return new HashingBufferedSink(fileSystem, newTempFileProjectPath());
    } catch (IOException e) {
      throw new HashedDbException(e);
    }
  }

  public static PathS dbPathTo(Hash hash) {
    return path(hash.toHexString());
  }

  private PathS newTempFileProjectPath() {
    return TEMP_DIR_PATH.appendPart(tempFileCounter.incrementAndGet().toString());
  }
}
