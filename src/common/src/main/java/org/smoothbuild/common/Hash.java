package org.smoothbuild.common;

import static okio.Okio.blackhole;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.common.Constants.CHARSET;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import okio.BufferedSource;
import okio.ByteString;
import okio.HashingSink;
import okio.HashingSource;
import okio.Sink;
import okio.Source;

public class Hash {
  private final ByteString byteString;

  public Hash(ByteString byteString) {
    this.byteString = byteString;
  }

  public ByteString toByteString() {
    return byteString;
  }

  @Override
  public String toString() {
    return toHexString();
  }

  public String toHexString() {
    return byteString.hex();
  }

  public static Hash read(BufferedSource source) throws IOException {
    return new Hash(source.readByteString(lengthInBytes()));
  }

  private static HashingSource hashingSource(Source source) {
    return HashingSource.sha256(source);
  }

  public static HashingSink hashingSink(Sink sink) {
    return HashingSink.sha256(sink);
  }

  public static Hash of(Path path) throws IOException {
    return Hash.of(source(path.toFile()));
  }

  public static Hash of(Source source) throws IOException {
    try (source) {
      HashingSource hashingSource = hashingSource(source);
      buffer(hashingSource).readAll(blackhole());
      return new Hash(hashingSource.hash());
    }
  }

  /*
   * Methods below uses guava HashFunction, HashCode and converts it to Hash.
   * Doing the same with Okio library would require ugly code to catch and swallow IOExceptions.
   */

  public static Hash of(List<Hash> hashes) {
    Hasher hasher = func().newHasher();
    for (Hash hash : hashes) {
      hasher.putBytes(hash.byteString.toByteArray());
    }
    return convert(hasher.hash());
  }

  public static Hash of(String string) {
    return convert(func().hashString(string, CHARSET));
  }

  public static Hash of(int value) {
    return convert(func().hashInt(value));
  }

  public static Hash of(ByteString byteString) {
    return convert(func().hashBytes(byteString.toByteArray()));
  }

  public static Hash decode(String string) {
    return new Hash(ByteString.decodeHex(string));
  }

  private static Hash convert(HashCode hash) {
    return new Hash(ByteString.of(hash.asBytes()));
  }

  public static int lengthInBytes() {
    return func().bits() / 8;
  }

  private static HashFunction func() {
    return Hashing.sha256();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    return o instanceof Hash that && this.byteString.equals(that.byteString);
  }

  @Override
  public int hashCode() {
    return byteString.hashCode();
  }
}
