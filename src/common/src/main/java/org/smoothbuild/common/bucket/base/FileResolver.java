package org.smoothbuild.common.bucket.base;

import static okio.Okio.buffer;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import java.io.IOException;
import java.nio.charset.Charset;
import okio.Source;
import org.smoothbuild.common.collect.Map;

/**
 * This class is thread-safe.
 */
@Singleton
public class FileResolver {
  private final Map<BucketId, Bucket> buckets;

  @Inject
  public FileResolver(Map<BucketId, Bucket> buckets) {
    this.buckets = buckets;
  }

  public String contentOf(FullPath fullPath, Charset charset) throws IOException {
    try (var source = buffer(source(fullPath))) {
      return source.readString(charset);
    }
  }

  public Source source(FullPath fullPath) throws IOException {
    return bucketFor(fullPath).source(fullPath.path());
  }

  public PathState pathState(FullPath fullPath) {
    return bucketFor(fullPath).pathState(fullPath.path());
  }

  private Bucket bucketFor(FullPath fullPath) {
    return bucketFor(fullPath.bucketId());
  }

  private Bucket bucketFor(BucketId bucketId) {
    Bucket bucket = buckets.get(bucketId);
    if (bucket == null) {
      throw new IllegalArgumentException("Unknown bucket id " + bucketId + ".");
    }
    return bucket;
  }
}
