package org.smoothbuild.common.bucket.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bucket.base.BucketId.bucketId;
import static org.smoothbuild.common.collect.Map.map;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.common.bucket.mem.MemoryBucket;
import org.smoothbuild.common.collect.Map;

public class BucketResolverTest {
  @Test
  void bucketFor_returns_bucket_with_given_id() {
    var bucket1 = new MemoryBucket();
    var bucket2 = new MemoryBucket();
    Map<BucketId, Bucket> map = map(bucketId("id1"), bucket1, bucketId("id2"), bucket2);
    var bucketResolver = new BucketResolver(map);

    assertThat(bucketResolver.bucketFor(bucketId("id1"))).isEqualTo(bucket1);
  }

  @Test
  void bucketFor_throws_exception_when_bucket_with_given_id_does_not_exist() {
    var bucket1 = new MemoryBucket();
    var bucket2 = new MemoryBucket();
    Map<BucketId, Bucket> map = map(bucketId("id1"), bucket1, bucketId("id2"), bucket2);
    var bucketResolver = new BucketResolver(map);

    assertCall(() -> bucketResolver.bucketFor(bucketId("id3")))
        .throwsException(IllegalArgumentException.class);
  }
}
