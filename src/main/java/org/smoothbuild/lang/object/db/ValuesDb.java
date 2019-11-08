package org.smoothbuild.lang.object.db;

import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.CodingErrorAction.REPORT;
import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashedDbException;
import org.smoothbuild.db.hashed.HashingBufferedSink;

import okio.BufferedSource;

public class ValuesDb {
  private final HashedDb hashedDb;

  public ValuesDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  public Hash writeBoolean(boolean value) throws ValuesDbException {
    try (HashingBufferedSink sink = sink()) {
      sink.writeByte(value ? 1 : 0);
      sink.close();
      return sink.hash();
    } catch (IOException e) {
      throw new ValuesDbException(e);
    }
  }

  public boolean readBoolean(Hash hash) throws ValuesDbException {
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
      throw new ValuesDbException(hash, e);
    }
  }

  public Hash writeString(String string) throws ValuesDbException {
    try (HashingBufferedSink sink = hashedDb.sink()) {
      sink.writeString(string, CHARSET);
      sink.close();
      return sink.hash();
    } catch (HashedDbException | IOException e) {
      throw new ValuesDbException(e);
    }
  }

  public String readString(Hash hash) throws ValuesDbException {
    try (BufferedSource source = hashedDb.source(hash)) {
      CharsetDecoder charsetDecoder = CHARSET.newDecoder();
      charsetDecoder.onMalformedInput(REPORT);
      charsetDecoder.onUnmappableCharacter(REPORT);
      return charsetDecoder.decode(wrap(source.readByteArray())).toString();
    } catch (CharacterCodingException e) {
      throw new DecodingStringException(hash, e);
    } catch (HashedDbException | IOException e) {
      throw new ValuesDbException(hash, e);
    }
  }

  public Hash writeHashes(Hash... hashes) throws ValuesDbException {
    try (HashingBufferedSink sink = hashedDb.sink()) {
      for (Hash hash : hashes) {
        sink.write(hash);
      }
      sink.close();
      return sink.hash();
    } catch (HashedDbException | IOException e) {
      throw new ValuesDbException(e);
    }
  }

  public List<Hash> readHashes(Hash hash, int expectedSize) throws ValuesDbException {
    List<Hash> hashes = readHashes(hash);
    if (hashes.size() != expectedSize) {
      throw new DecodingHashSequenceException(hash, expectedSize, hashes.size());
    }
    return hashes;
  }

  public List<Hash> readHashes(Hash hash, int minExpectedSize, int maxExpectedSize) throws
      ValuesDbException {
    List<Hash> hashes = readHashes(hash);
    if (hashes.size() < minExpectedSize || maxExpectedSize < hashes.size()) {
      throw new DecodingHashSequenceException(
          hash, minExpectedSize, maxExpectedSize, hashes.size());
    }
    return hashes;
  }

  public List<Hash> readHashes(Hash hash) throws ValuesDbException {
    List<Hash> result = new ArrayList<>();
    try (BufferedSource source = hashedDb.source(hash)) {
      while (!source.exhausted()) {
        if (source.request(Hash.hashesSize())) {
          result.add(Hash.read(source));
        } else {
          throw new DecodingHashSequenceException(hash);
        }
      }
    } catch (HashedDbException | IOException e) {
      throw new ValuesDbException(hash, e);
    }
    return result;
  }

  public HashingBufferedSink sink() throws ValuesDbException {
    try {
      return hashedDb.sink();
    } catch (HashedDbException e) {
      throw new ValuesDbException(e);
    }
  }

  public BufferedSource source(Hash hash) throws ValuesDbException {
    try {
      return hashedDb.source(hash);
    } catch (HashedDbException e) {
      throw new ValuesDbException(hash, e);
    }
  }
}
