package org.smoothbuild.common.testing;

import java.io.IOException;
import okio.ByteString;
import okio.Source;
import org.smoothbuild.common.bucket.base.Filesystem;
import org.smoothbuild.common.bucket.base.FullPath;

public class TestingFilesystem {
  public static void createFile(Filesystem filesystem, FullPath path) throws IOException {
    createFile(filesystem, path, "");
  }

  public static void createFile(Filesystem filesystem, FullPath path, String content)
      throws IOException {
    TestingBucket.createFile(filesystem.bucketFor(path.bucketId()), path.path(), content);
  }

  public static void createFile(Filesystem filesystem, FullPath path, Source content)
      throws IOException {
    TestingBucket.createFile(filesystem.bucketFor(path.bucketId()), path.path(), content);
  }

  public static ByteString readFile(Filesystem filesystem, FullPath path) throws IOException {
    return TestingBucket.readFile(filesystem.bucketFor(path.bucketId()), path.path());
  }
}
