package org.smoothbuild.common.testing;

import static okio.Okio.buffer;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import okio.ByteString;
import okio.Source;
import org.smoothbuild.common.bucket.base.Bucket;
import org.smoothbuild.common.bucket.base.Path;
import org.smoothbuild.common.bucket.base.PathIterator;
import org.smoothbuild.common.bucket.base.RecursivePathsIterator;

public class TestingBucket {
  public static Map<Path, ByteString> directoryToFileMap(Bucket bucket) throws IOException {
    var result = new HashMap<Path, ByteString>();
    PathIterator pathIterator = RecursivePathsIterator.recursivePathsIterator(bucket, Path.root());
    while (pathIterator.hasNext()) {
      var path = pathIterator.next();
      result.put(path, readFile(bucket, path));
    }
    return result;
  }

  public static void createFile(Bucket bucket, Path path, String content) throws IOException {
    bucket.createDir(path.parent());
    writeFile(bucket, path, content);
  }

  public static void createFile(Bucket bucket, Path path, ByteString content) throws IOException {
    bucket.createDir(path.parent());
    writeFile(bucket, path, content);
  }

  public static void createFile(Bucket bucket, Path path, Source content) throws IOException {
    bucket.createDir(path.parent());
    writeFile(bucket, path, content);
  }

  private static void writeFile(Bucket bucket, Path path, String content) throws IOException {
    writeFile(bucket, path, ByteString.encodeUtf8(content));
  }

  public static void writeFile(Bucket bucket, Path path, ByteString content) throws IOException {
    try (var bufferedSink = buffer(bucket.sink(path))) {
      bufferedSink.write(content);
    }
  }

  public static void writeFile(Bucket bucket, Path path, Source content) throws IOException {
    try (var bufferedSink = buffer(bucket.sink(path))) {
      bufferedSink.writeAll(content);
    }
  }

  public static ByteString readFile(Bucket bucket, Path path) throws IOException {
    try (var source = buffer(bucket.source(path))) {
      return source.readByteString();
    }
  }
}
