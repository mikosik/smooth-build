package org.smoothbuild.lang.object.db;

import static java.nio.ByteBuffer.wrap;
import static java.nio.charset.CodingErrorAction.REPORT;
import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;

import org.smoothbuild.db.hashed.DecodingStringException;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.db.hashed.HashedDb;
import org.smoothbuild.db.hashed.HashingBufferedSink;

import okio.BufferedSource;

public class ValuesDb {
  private final HashedDb hashedDb;

  public ValuesDb(HashedDb hashedDb) {
    this.hashedDb = hashedDb;
  }

  public Hash writeString(String string) throws IOException {
    try (HashingBufferedSink sink = hashedDb.sink()) {
      sink.writeString(string, CHARSET);
      sink.close();
      return sink.hash();
    }
  }

  public String readString(Hash hash) throws IOException, DecodingStringException {
    try (BufferedSource source = hashedDb.source(hash)) {
      CharsetDecoder charsetDecoder = CHARSET.newDecoder();
      charsetDecoder.onMalformedInput(REPORT);
      charsetDecoder.onUnmappableCharacter(REPORT);
      return charsetDecoder.decode(wrap(source.readByteArray())).toString();
    } catch (CharacterCodingException e) {
      throw new DecodingStringException(e.getMessage(), e);
    }
  }

  public Hash writeHashes(Hash... hashes) throws IOException {
    try (HashingBufferedSink sink = hashedDb.sink()) {
      for (Hash hash : hashes) {
        sink.write(hash);
      }
      sink.close();
      return sink.hash();
    }
  }

  public List<Hash> readHashes(Hash hash) throws IOException {
    List<Hash> result = new ArrayList<>();
    try (BufferedSource source = hashedDb.source(hash)) {
      while (!source.exhausted()) {
        result.add(Hash.read(source));
      }
    }
    return result;
  }

  public HashingBufferedSink sink() throws IOException {
    return hashedDb.sink();
  }

  public BufferedSource source(Hash hash) throws IOException {
    return hashedDb.source(hash);
  }
}
