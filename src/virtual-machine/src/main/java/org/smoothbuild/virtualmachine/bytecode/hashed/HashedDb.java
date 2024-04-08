package org.smoothbuild.virtualmachine.bytecode.hashed;

import static java.lang.String.format;
import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.CodingErrorAction.REPORT;
import static java.util.Arrays.asList;
import static okio.Okio.buffer;
import static org.smoothbuild.common.Constants.CHARSET;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.log.base.Try.failure;
import static org.smoothbuild.common.log.base.Try.success;

import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import okio.BufferedSink;
import okio.Source;
import org.smoothbuild.common.base.Hash;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.concurrent.AtomicBigInteger;
import org.smoothbuild.common.function.Consumer1;
import org.smoothbuild.common.init.Initializable;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.CorruptedHashedDbException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeBigIntegerException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeBooleanException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeByteException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeHashChainException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.DecodeStringException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.HashedDbException;
import org.smoothbuild.virtualmachine.bytecode.hashed.exc.NoSuchDataException;

/**
 * This class is thread-safe.
 */
public class HashedDb implements Initializable {
  static final Path TEMP_DIR_PATH = path("tmp");
  private final Bucket bucket;
  private final AtomicBigInteger tempFileCounter = new AtomicBigInteger();

  public HashedDb(Bucket bucket) {
    this.bucket = bucket;
  }

  @Override
  public Try<Void> initialize() {
    try {
      bucket.createDir(TEMP_DIR_PATH);
      return success(null);
    } catch (IOException e) {
      return failure(fatal(e));
    }
  }

  public Hash writeBigInteger(BigInteger value) throws HashedDbException {
    return writeData(bufferedSink -> bufferedSink.write(value.toByteArray()));
  }

  public BigInteger readBigInteger(Hash hash) throws HashedDbException {
    try (var source = buffer(source(hash))) {
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
    return writeData(bufferedSink -> bufferedSink.writeByte(value));
  }

  public byte readByte(Hash hash) throws HashedDbException {
    try (var source = buffer(source(hash))) {
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
    return writeData(bufferedSink -> bufferedSink.writeString(string, CHARSET));
  }

  public String readString(Hash hash) throws HashedDbException {
    try (var source = buffer(source(hash))) {
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

  public Hash writeHashChain(Hash... hashes) throws HashedDbException {
    return writeHashChain(asList(hashes));
  }

  public Hash writeHashChain(Iterable<Hash> hashes) throws HashedDbException {
    return writeData(bufferedSink -> {
      for (Hash hash : hashes) {
        bufferedSink.write(hash.toByteString());
      }
    });
  }

  public Hash writeData(Consumer1<BufferedSink, IOException> writer) throws HashedDbException {
    try (HashingSink hashingSink = sink()) {
      try (BufferedSink bufferedSink = buffer(hashingSink)) {
        writer.accept(bufferedSink);
      }
      return hashingSink.hash();
    } catch (IOException e) {
      throw new HashedDbException(e);
    }
  }

  public long readHashChainSize(Hash hash) throws HashedDbException {
    var path = dbPathTo(hash);
    var pathState = bucket.pathState(path);
    return switch (pathState) {
      case FILE -> readHashChainSize(hash, path);
      case DIR -> throw new CorruptedHashedDbException(
          format("Corrupted HashedDb at %s. %s is a directory not a data file.", hash, path.q()));
      case NOTHING -> throw new NoSuchDataException(hash);
    };
  }

  private long readHashChainSize(Hash hash, Path path) throws HashedDbException {
    try {
      var sizeInBytes = bucket.size(path);
      long remainder = sizeInBytes % Hash.lengthInBytes();
      if (remainder == 0) {
        return sizeInBytes / Hash.lengthInBytes();
      } else {
        throw new DecodeHashChainException(hash, remainder);
      }
    } catch (IOException e) {
      throw new HashedDbException(hash, e);
    }
  }

  public List<Hash> readHashChain(Hash hash) throws HashedDbException {
    var builder = new ArrayList<Hash>();
    try (var source = buffer(source(hash))) {
      while (!source.exhausted()) {
        if (source.request(Hash.lengthInBytes())) {
          builder.add(Hash.read(source));
        } else {
          throw new DecodeHashChainException(hash, source.readByteArray().length);
        }
      }
    } catch (IOException e) {
      throw new HashedDbException(hash, e);
    }
    return listOfAll(builder);
  }

  public boolean contains(Hash hash) throws CorruptedHashedDbException {
    var path = dbPathTo(hash);
    var pathState = bucket.pathState(path);
    return switch (pathState) {
      case FILE -> true;
      case DIR -> throw new CorruptedHashedDbException(
          "Corrupted HashedDb. " + path.q() + " is a directory not a data file.");
      case NOTHING -> false;
    };
  }

  public Source source(Hash hash) throws HashedDbException {
    var path = dbPathTo(hash);
    var pathState = bucket.pathState(path);
    return switch (pathState) {
      case FILE -> sourceFile(hash, path);
      case DIR -> throw new CorruptedHashedDbException(
          format("Corrupted HashedDb at %s. %s is a directory not a data file.", hash, path.q()));
      case NOTHING -> throw new NoSuchDataException(hash);
    };
  }

  private Source sourceFile(Hash hash, Path path) throws HashedDbException {
    try {
      return bucket.source(path);
    } catch (IOException e) {
      throw new HashedDbException(hash, e);
    }
  }

  public HashingSink sink() throws HashedDbException {
    try {
      return new HashingSink(bucket, newTempFileProjectPath());
    } catch (IOException e) {
      throw new HashedDbException(e);
    }
  }

  public static Path dbPathTo(Hash hash) {
    return path(hash.toHexString());
  }

  private Path newTempFileProjectPath() {
    return TEMP_DIR_PATH.appendPart(tempFileCounter.incrementAndGet().toString());
  }
}
