package org.smoothbuild.db.hashed;

import static org.smoothbuild.SmoothConstants.CHARSET;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.smoothbuild.io.fs.base.Path;

import com.google.common.hash.Funnels;
import com.google.common.hash.HashCode;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hasher;
import com.google.common.hash.Hashing;
import com.google.common.io.ByteStreams;

import okio.HashingSink;
import okio.Sink;

public class Hash {
  public static Hasher newHasher() {
    return function().newHasher();
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
    try (InputStream inputStream = new FileInputStream(path.toFile())) {
      return stream(inputStream);
    }
  }

  public static HashCode stream(InputStream inputStream) throws IOException {
    Hasher hasher = newHasher();
    ByteStreams.copy(inputStream, Funnels.asOutputStream(hasher));
    return hasher.hash();
  }

  public static int size() {
    return function().bits() / 8;
  }

  public static Path toPath(HashCode hash) {
    return Path.path(hash.toString());
  }

  public static HashingSink hashingSink(Sink sink) {
    return HashingSink.sha1(sink);
  }

  private static HashFunction function() {
    return Hashing.sha1();
  }
}
