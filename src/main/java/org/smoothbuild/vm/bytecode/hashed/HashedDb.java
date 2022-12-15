package org.smoothbuild.vm.bytecode.hashed;

import static java.lang.String.format;
import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.CodingErrorAction.REPORT;
import static java.util.Arrays.asList;
import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;

import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.fs.base.PathS;
import org.smoothbuild.fs.base.PathState;
import org.smoothbuild.install.TempManager;
import org.smoothbuild.vm.bytecode.hashed.exc.CorruptedHashedDbExc;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeBigIntegerExc;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeBooleanExc;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeByteExc;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeHashSeqExc;
import org.smoothbuild.vm.bytecode.hashed.exc.DecodeStringExc;
import org.smoothbuild.vm.bytecode.hashed.exc.HashedDbExc;
import org.smoothbuild.vm.bytecode.hashed.exc.NoSuchDataExc;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

import okio.BufferedSource;

/**
 * This class is thread-safe.
 */
public class HashedDb {
  private final FileSystem fileSystem;
  private final PathS rootPath;
  private final TempManager tempManager;

  public HashedDb(FileSystem fileSystem, PathS rootPath, TempManager tempManager) {
    this.fileSystem = fileSystem;
    this.rootPath = rootPath;
    this.tempManager = tempManager;
  }

  public Hash writeBigInteger(BigInteger value) throws HashedDbExc {
    try (HashingBufferedSink sink = sink()) {
      sink.write(value.toByteArray());
      sink.close();
      return sink.hash();
    } catch (IOException e) {
      throw new HashedDbExc(e);
    }
  }

  public BigInteger readBigInteger(Hash hash) throws HashedDbExc {
    try (BufferedSource source = source(hash)) {
      byte[] bytes = source.readByteArray();
      if (bytes.length == 0) {
        throw new DecodeBigIntegerExc(hash);
      }
      return new BigInteger(bytes);
    } catch (IOException e) {
      throw new HashedDbExc(hash, e);
    }
  }

  public Hash writeBoolean(boolean value) throws HashedDbExc {
    return writeByte(value ? (byte) 1 : (byte) 0);
  }

  public boolean readBoolean(Hash hash) throws HashedDbExc {
    try {
      byte value = readByte(hash);
      return switch (value) {
        case 0 -> false;
        case 1 -> true;
        default -> throw new DecodeBooleanExc(hash);
      };
    } catch (DecodeByteExc e) {
      throw new DecodeBooleanExc(hash, e);
    }
  }

  public Hash writeByte(byte value) throws HashedDbExc {
    try (HashingBufferedSink sink = sink()) {
      sink.writeByte(value);
      sink.close();
      return sink.hash();
    } catch (IOException e) {
      throw new HashedDbExc(e);
    }
  }

  public byte readByte(Hash hash) throws HashedDbExc {
    try (BufferedSource source = source(hash)) {
      if (source.exhausted()) {
        throw new DecodeByteExc(hash);
      }
      byte value = source.readByte();
      if (!source.exhausted()) {
        throw new DecodeByteExc(hash);
      }
      return value;
    } catch (IOException e) {
      throw new HashedDbExc(hash, e);
    }
  }

  public Hash writeString(String string) throws HashedDbExc {
    try (HashingBufferedSink sink = sink()) {
      sink.writeString(string, CHARSET);
      sink.close();
      return sink.hash();
    } catch (IOException e) {
      throw new HashedDbExc(e);
    }
  }

  public String readString(Hash hash) throws HashedDbExc {
    try (BufferedSource source = source(hash)) {
      CharsetDecoder charsetDecoder = CHARSET.newDecoder();
      charsetDecoder.onMalformedInput(REPORT);
      charsetDecoder.onUnmappableCharacter(REPORT);
      return charsetDecoder.decode(wrap(source.readByteArray())).toString();
    } catch (CharacterCodingException e) {
      throw new DecodeStringExc(hash, e);
    } catch (IOException e) {
      throw new HashedDbExc(hash, e);
    }
  }

  public Hash writeSeq(Hash... hashes) throws HashedDbExc {
    return writeSeq(asList(hashes));
  }

  public Hash writeSeq(Iterable<Hash> hashes) throws HashedDbExc {
    try (HashingBufferedSink sink = sink()) {
      for (Hash hash : hashes) {
        sink.write(hash.toByteString());
      }
      sink.close();
      return sink.hash();
    } catch (IOException e) {
      throw new HashedDbExc(e);
    }
  }

  public long readSeqSize(Hash hash) throws HashedDbExc {
    PathS path = toPath(hash);
    PathState pathState = fileSystem.pathState(path);
    return switch (pathState) {
      case FILE -> readSeqSize(hash, path);
      case DIR -> throw new CorruptedHashedDbExc(
          format("Corrupted HashedDb at %s. %s is a directory not a data file.", hash, path.q()));
      case NOTHING -> throw new NoSuchDataExc(hash);
    };
  }

  private long readSeqSize(Hash hash, PathS path) throws HashedDbExc {
    try {
      var sizeInBytes = fileSystem.size(path);
      long remainder = sizeInBytes % Hash.lengthInBytes();
      if (remainder == 0) {
        return sizeInBytes / Hash.lengthInBytes();
      } else {
        throw new DecodeHashSeqExc(hash, remainder);
      }
    } catch (IOException e) {
      throw new HashedDbExc(hash, e);
    }
  }

  public ImmutableList<Hash> readSeq(Hash hash) throws HashedDbExc {
    Builder<Hash> builder = ImmutableList.builder();
    try (BufferedSource source = source(hash)) {
      while (!source.exhausted()) {
        if (source.request(Hash.lengthInBytes())) {
          builder.add(Hash.read(source));
        } else {
          throw new DecodeHashSeqExc(hash, source.readByteArray().length);
        }
      }
    } catch (IOException e) {
      throw new HashedDbExc(hash, e);
    }
    return builder.build();
  }

  public boolean contains(Hash hash) throws CorruptedHashedDbExc {
    PathS path = toPath(hash);
    PathState pathState = fileSystem.pathState(path);
    return switch (pathState) {
      case FILE -> true;
      case DIR -> throw new CorruptedHashedDbExc(
          "Corrupted HashedDb. " + path.q() + " is a directory not a data file.");
      case NOTHING -> false;
    };
  }

  public BufferedSource source(Hash hash) throws HashedDbExc {
    PathS path = toPath(hash);
    PathState pathState = fileSystem.pathState(path);
    return switch (pathState) {
      case FILE -> sourceFile(hash, path);
      case DIR -> throw new CorruptedHashedDbExc(
          format("Corrupted HashedDb at %s. %s is a directory not a data file.", hash, path.q()));
      case NOTHING -> throw new NoSuchDataExc(hash);
    };
  }

  private BufferedSource sourceFile(Hash hash, PathS path) throws HashedDbExc {
    try {
      return fileSystem.source(path);
    } catch (IOException e) {
      throw new HashedDbExc(hash, e);
    }
  }

  public HashingBufferedSink sink() throws HashedDbExc {
    try {
      return new HashingBufferedSink(fileSystem, tempManager.tempPath(), rootPath);
    } catch (IOException e) {
      throw new HashedDbExc(e);
    }
  }

  private PathS toPath(Hash hash) {
    return dataFullPath(rootPath, hash);
  }

  public static PathS dataFullPath(PathS hashedDbPath, Hash hash) {
    return hashedDbPath.appendPart(hash.toHexString());
  }
}
