package org.smoothbuild.common.testing;

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
  public static Map<Path, ByteString> directoryToFileMap(Bucket bucket, Path dir)
      throws IOException {
    var result = new HashMap<Path, ByteString>();
    PathIterator pathIterator = RecursivePathsIterator.recursivePathsIterator(bucket, dir);
    while (pathIterator.hasNext()) {
      var path = pathIterator.next();
      result.put(path, readFile(bucket, dir.append(path)));
    }
    return result;
  }

  public static void writeFile(Bucket bucket, Path path, String content) throws IOException {
    writeFile(bucket, path, ByteString.encodeUtf8(content));
  }

  public static void writeFile(Bucket bucket, Path path, ByteString content) throws IOException {
    try (var bufferedSink = bucket.sink(path)) {
      bufferedSink.write(content);
    }
  }

  public static void writeFile(Bucket bucket, Path path, Source content) throws IOException {
    try (var bufferedSink = bucket.sink(path)) {
      bufferedSink.writeAll(content);
    }
  }

  public static ByteString readFile(Bucket bucket, Path path) throws IOException {
    try (var source = bucket.source(path)) {
      return source.readByteString();
    }
  }
}
