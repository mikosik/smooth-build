package org.smoothbuild.common.bucket.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bucket.base.Alias.alias;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.bucket.mem.MemoryBucket;
import org.smoothbuild.common.collect.Map;

public class BucketResolverTest {
  @Test
  void bucketFor_returns_bucket_with_given_alias() {
    var bucket1 = new MemoryBucket();
    var bucket2 = new MemoryBucket();
    Map<Alias, Bucket> map = map(alias("id1"), bucket1, alias("id2"), bucket2);
    var bucketResolver = new BucketResolver(map);

    assertThat(bucketResolver.bucketFor(alias("id1"))).isEqualTo(bucket1);
  }

  @Test
  void bucketFor_throws_exception_when_bucket_with_given_alias_not_exists() {
    var bucket1 = new MemoryBucket();
    var bucket2 = new MemoryBucket();
    Map<Alias, Bucket> map = map(alias("id1"), bucket1, alias("id2"), bucket2);
    var bucketResolver = new BucketResolver(map);

    assertCall(() -> bucketResolver.bucketFor(alias("id3")))
        .throwsException(IllegalArgumentException.class);
  }
}
