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
  private static final BucketId PROJECT = bucketId("project");
  private static final BucketId LIBS = bucketId("libs");

  @Test
  void equals_and_hash_code() {
    String file = "abc.smooth";

    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(fullPath(PROJECT, path(file)), fullPath(PROJECT, path(file)));
    tester.addEqualityGroup(fullPath(PROJECT, path("def")), fullPath(PROJECT, path("def")));
    tester.addEqualityGroup(fullPath(LIBS, path(file)), fullPath(LIBS, path(file)));

    tester.testEquals();
  }

  @Test
  void append_part() {
    var fullPath = fullPath(PROJECT, path("abc"));
    assertThat(fullPath.appendPart("def")).isEqualTo(fullPath(PROJECT, path("abc/def")));
  }

  @Test
  void append() {
    var fullPath = fullPath(PROJECT, path("abc"));
    assertThat(fullPath.append(path("def/ghi"))).isEqualTo(fullPath(PROJECT, path("abc/def/ghi")));
  }

  @Test
  void parent_of_root_dir_throws_exception() {
    var fullPath = fullPath(PROJECT, root());
    assertCall(() -> fullPath.parent()).throwsException(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("parentArguments")
  public void parent_of_normal_path(FullPath path, FullPath expectedParent) {
    assertThat(path.parent()).isEqualTo(expectedParent);
  }

  public static Stream<Arguments> parentArguments() {
    return Stream.of(
        arguments(fullPath(PROJECT, path("abc")), fullPath(PROJECT, root())),
        arguments(fullPath(PROJECT, path(" ")), fullPath(PROJECT, root())),
        arguments(fullPath(PROJECT, path("abc/def")), fullPath(PROJECT, path("abc"))),
        arguments(fullPath(PROJECT, path("abc/def/ghi")), fullPath(PROJECT, path("abc/def"))),
        arguments(
            fullPath(PROJECT, path("abc/def/ghi/ijk")), fullPath(PROJECT, path("abc/def/ghi"))));
  }

  @Test
  void with_extension() {
    var fullPath = fullPath(PROJECT, path("full/path.smooth"));
    assertThat(fullPath.withExtension("jar")).isEqualTo(fullPath(PROJECT, path("full/path.jar")));
  }

  @Test
  void to_string() {
    var fullPath = fullPath(PROJECT, path("abc"));
    assertThat(fullPath.toString()).isEqualTo("{project}/abc");
  }
}
