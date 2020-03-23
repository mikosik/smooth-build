package org.smoothbuild.db.hashed;

import static okio.Okio.blackhole;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import okio.BufferedSource;
import okio.ByteString;
import okio.HashingSink;
import okio.HashingSource;
import okio.Sink;
import okio.Source;

public class Hash extends ByteString {
  public Hash(ByteString byteString) {
    this(byteString.toByteArray());
  }

  public Hash(byte[] data) {
    super(data);
  }

  @Override
  public String toString() {
    return hex();
  }

  public static Hash read(BufferedSource source) throws IOException {
    return new Hash(source.readByteArray(hashesSize()));
  }

  private static HashingSource hashingSource(Source source) {
    return HashingSource.sha1(source);
  }

  public static HashingSink hashingSink(Sink sink) {
    return HashingSink.sha1(sink);
  }

  public static Hash of(java.nio.file.Path path) throws IOException {
    try (Source source = source(path.toFile())) {
      HashingSource hashingSource = hashingSource(source);
      buffer(hashingSource).readAll(blackhole());
      return new Hash(hashingSource.hash());
    }
  }

  /*
   * Methods below uses guava HashFunction, HashCode and converts it to Hash.
   * Doing the same with Okio library would require ugly code to catch and swallow IOExceptions.
   */

  public static Hash of(Hash... hashes) {
    Hasher hasher = function().newHasher();
    for (Hash hash : hashes) {
      hasher.putBytes(hash.toByteArray());
    }
    return convert(hasher.hash());
  }

  public static Hash of(String string) {
    return convert(function().hashString(string, CHARSET));
  }

  public static Hash of(int value) {
    return convert(function().hashInt(value));
  }

  public static Hash of(ByteString byteString) {
    return convert(function().hashBytes(byteString.toByteArray()));
  }

  public static Hash decode(String string) {
    return new Hash(ByteString.decodeHex(string));
  }

  private static Hash convert(HashCode hash) {
    return new Hash(hash.asBytes());
  }

  public static int hashesSize() {
    return function().bits() / 8;
  }

  @SuppressWarnings("deprecation")
  private static HashFunction function() {
    return Hashing.sha1();
  }
}
