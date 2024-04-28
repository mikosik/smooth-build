package org.smoothbuild.common.bucket.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bucket.base.BucketId.bucketId;
import static org.smoothbuild.common.bucket.base.Path.path;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

public class FullPathTest {
  @Test
  void equals_and_hash_code() {
    String file = "abc.smooth";

    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        new FullPath(bucketId("project"), path(file)),
        new FullPath(bucketId("project"), path(file)));
    tester.addEqualityGroup(
        new FullPath(bucketId("project"), path("def")),
        new FullPath(bucketId("project"), path("def")));
    tester.addEqualityGroup(
        new FullPath(bucketId("sl"), path(file)), new FullPath(bucketId("sl"), path(file)));

    tester.testEquals();
  }

  @Test
  void prefixed_path() {
    var fullPath = new FullPath(bucketId("project"), path("full/path.smooth"));
    assertThat((Object) fullPath.toString()).isEqualTo("{project}/full/path.smooth");
  }

  @Test
  void with_extension() {
    var fullPath = new FullPath(bucketId("project"), path("full/path.smooth"));
    assertThat(fullPath.withExtension("jar"))
        .isEqualTo(new FullPath(bucketId("project"), path("full/path.jar")));
  }

  @Test
  void to_string() {
    var fullPath = new FullPath(bucketId("project"), path("abc"));
    assertThat(fullPath.toString()).isEqualTo("{project}/abc");
  }
}
