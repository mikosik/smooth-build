package org.smoothbuild.common.bucket.base;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.bucket.base.BucketId.bucketId;
import static org.smoothbuild.common.bucket.base.FullPath.fullPath;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.common.bucket.base.Path.root;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.testing.EqualsTester;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

public class FullPathTest {
  @Test
  void equals_and_hash_code() {
    String file = "abc.smooth";

    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(
        fullPath(bucketId("project"), path(file)), fullPath(bucketId("project"), path(file)));
    tester.addEqualityGroup(
        fullPath(bucketId("project"), path("def")), fullPath(bucketId("project"), path("def")));
    tester.addEqualityGroup(
        fullPath(bucketId("sl"), path(file)), fullPath(bucketId("sl"), path(file)));

    tester.testEquals();
  }

  @Test
  void append_part() {
    var bucketId = bucketId("project");
    var fullPath = fullPath(bucketId, path("abc"));
    assertThat(fullPath.appendPart("def")).isEqualTo(fullPath(bucketId, path("abc/def")));
  }

  @Test
  void append() {
    var bucketId = bucketId("project");
    var fullPath = fullPath(bucketId, path("abc"));
    assertThat(fullPath.append(path("def/ghi"))).isEqualTo(fullPath(bucketId, path("abc/def/ghi")));
  }

  @Test
  void parent_of_root_dir_throws_exception() {
    var fullPath = fullPath(bucketId("project"), root());
    assertCall(() -> fullPath.parent()).throwsException(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("parentArguments")
  public void parent_of_normal_path(FullPath path, FullPath expectedParent) {
    assertThat(path.parent()).isEqualTo(expectedParent);
  }

  public static Stream<Arguments> parentArguments() {
    return Stream.of(
        arguments(
            fullPath(bucketId("bucketId"), path("abc")), fullPath(bucketId("bucketId"), root())),
        arguments(
            fullPath(bucketId("bucketId"), path(" ")), fullPath(bucketId("bucketId"), root())),
        arguments(
            fullPath(bucketId("bucketId"), path("abc/def")),
            fullPath(bucketId("bucketId"), path("abc"))),
        arguments(
            fullPath(bucketId("bucketId"), path("abc/def/ghi")),
            fullPath(bucketId("bucketId"), path("abc/def"))),
        arguments(
            fullPath(bucketId("bucketId"), path("abc/def/ghi/ijk")),
            fullPath(bucketId("bucketId"), path("abc/def/ghi"))));
  }

  @Test
  void with_extension() {
    var fullPath = fullPath(bucketId("project"), path("full/path.smooth"));
    assertThat(fullPath.withExtension("jar"))
        .isEqualTo(fullPath(bucketId("project"), path("full/path.jar")));
  }

  @Test
  void to_string() {
    var fullPath = fullPath(bucketId("project"), path("abc"));
    assertThat(fullPath.toString()).isEqualTo("{project}/abc");
  }
}
