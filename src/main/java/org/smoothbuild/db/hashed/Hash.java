package org.smoothbuild.db.hashed;

import static okio.Okio.blackhole;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.IOException;

import org.smoothbuild.io.fs.base.Path;

import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;

import okio.BufferedSource;
import okio.HashingSink;
import okio.HashingSource;
import okio.Sink;
import okio.Source;

public class Hash {
  public static Hasher newHasher() {
    return function().newHasher();
  }

  public static HashCode read(BufferedSource source) throws IOException {
    source.require(size());
    return HashCode.fromBytes(source.readByteArray(size()));
  }

  public static HashCode hashes(HashCode... hashes) {
    Hasher hasher = newHasher();
    for (HashCode hash : hashes) {
      hasher.putBytes(hash.asBytes());
    }
    return hasher.hash();
  }

  public static HashCode string(String string) {
    return function().hashString(string, CHARSET);
  }

  public static HashCode integer(int value) {
    return function().hashInt(value);
  }

  public static HashCode bytes(byte[] bytes) {
    return function().hashBytes(bytes);
  }

  public static HashCode file(java.nio.file.Path path) throws IOException {
    try (Source source = source(path.toFile())) {
      HashingSource hashingSource = hashingSource(source);
      buffer(hashingSource).readAll(blackhole());
      return HashCode.fromBytes(hashingSource.hash().toByteArray());
    }
  }

  public static int size() {
    return function().bits() / 8;
  }

  public static Path toPath(HashCode hash) {
    return Path.path(hash.toString());
  }

  private static HashingSource hashingSource(Source source) {
    return HashingSource.sha1(source);
  }

  public static HashingSink hashingSink(Sink sink) {
    return HashingSink.sha1(sink);
  }

  private static HashFunction function() {
    return Hashing.sha1();
  }
}
