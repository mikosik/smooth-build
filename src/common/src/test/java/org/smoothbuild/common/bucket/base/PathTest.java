package org.smoothbuild.common.bucket.base;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.testing.EqualsTester;
import com.google.common.truth.Truth;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.common.collect.List;

public class PathTest {
  @ParameterizedTest
  @MethodSource("paths")
  public void path_creation(String value, boolean isValid) {
    if (isValid) {
      Path.path(value);
    } else {
      assertCall(() -> Path.path(value)).throwsException(IllegalPathException.class);
    }
  }

  @ParameterizedTest
  @MethodSource("paths")
  public void fail_if_not_legal_path(String value, boolean isValid) {
    if (!isValid) {
      assertCall(() -> Path.failIfNotLegalPath(value)).throwsException(IllegalPathException.class);
    }
  }

  @Test
  void single_dot_string_path_is_root() {
    Truth.assertThat(Path.path(".").isRoot()).isTrue();
  }

  @Test
  void simple_path_is_not_root() {
    Truth.assertThat(Path.path("file.txt").isRoot()).isFalse();
  }

  @Test
  void parent_of_root_dir_throws_exception() {
    assertCall(() -> Path.root().parent()).throwsException(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("parentArguments")
  public void parent_of_normal_path(Path path, Path expectedParent) {
    assertThat(path.parent()).isEqualTo(expectedParent);
  }

  public static Stream<Arguments> parentArguments() {
    return Stream.of(
        arguments(Path.path("abc"), Path.root()),
        arguments(Path.path(" "), Path.root()),
        arguments(Path.path("abc/def"), Path.path("abc")),
        arguments(Path.path("abc/def/ghi"), Path.path("abc/def")),
        arguments(Path.path("abc/def/ghi/ijk"), Path.path("abc/def/ghi")));
  }

  @ParameterizedTest
  @MethodSource("append_cases")
  public void append(String path, String appendedPath, String expected) {
    Truth.assertThat(Path.path(path).append(Path.path(appendedPath)))
        .isEqualTo(Path.path(expected));
  }

  public static Stream<Arguments> append_cases() {
    return Stream.of(
        arguments(".", ".", "."),
        arguments("abc", ".", "abc"),
        arguments("abc/def", ".", "abc/def"),
        arguments("abc/def/ghi", ".", "abc/def/ghi"),
        arguments(".", "abc", "abc"),
        arguments(".", "abc/def", "abc/def"),
        arguments(".", "abc/def/ghi", "abc/def/ghi"),
        arguments("abc", "xyz", "abc/xyz"),
        arguments("abc", "xyz/uvw", "abc/xyz/uvw"),
        arguments("abc", "xyz/uvw/rst", "abc/xyz/uvw/rst"),
        arguments("abc/def", "xyz", "abc/def/xyz"),
        arguments("abc/def", "xyz/uvw", "abc/def/xyz/uvw"),
        arguments("abc/def", "xyz/uvw/rst", "abc/def/xyz/uvw/rst"),
        arguments("abc/def/ghi", "xyz", "abc/def/ghi/xyz"),
        arguments("abc/def/ghi", "xyz/uvw", "abc/def/ghi/xyz/uvw"),
        arguments("abc/def/ghi", "xyz/uvw/rst", "abc/def/ghi/xyz/uvw/rst"),
        arguments(" ", " ", " / "),
        arguments(" ", " / ", " / / "),
        arguments(" / ", " ", " / / "),
        arguments(" / ", " / ", " / / / "));
  }

  @ParameterizedTest
  @MethodSource("appendPart_cases")
  public void appendPart(String path, String part, String expected) {
    Truth.assertThat(Path.path(path).appendPart(part)).isEqualTo(Path.path(expected));
  }

  public static Stream<Arguments> appendPart_cases() {
    return Stream.of(
        arguments(".", "abc", "abc"),
        arguments("abc", "xyz", "abc/xyz"),
        arguments("abc/def", "xyz", "abc/def/xyz"),
        arguments("abc/def/ghi", "xyz", "abc/def/ghi/xyz"),
        arguments(" ", " ", " / "),
        arguments(" / ", " ", " / / "));
  }

  @ParameterizedTest
  @MethodSource("appendPart_fails_for_cases")
  public void appendPart_fails_for(String path, String part) {
    assertCall(() -> Path.path(path).appendPart(part))
        .throwsException(IllegalArgumentException.class);
  }

  public static Stream<Arguments> appendPart_fails_for_cases() {
    return Stream.of(
        arguments(".", ""),
        arguments(".", "."),
        arguments(".", ".."),
        arguments(".", "abc/def"),
        arguments(".", " / "),
        arguments(".", "abc/def/ghi"),
        arguments(" ", ""),
        arguments(" ", "."),
        arguments(" ", ".."),
        arguments(" ", " / "),
        arguments(" ", "abc/def"),
        arguments("abc", ""),
        arguments("abc", "."),
        arguments("abc", ".."),
        arguments("abc", "xyz/uvw"),
        arguments("abc", " / "),
        arguments("abc", "xyz/uvw/rst"),
        arguments("abc/def", ""),
        arguments("abc/def", "."),
        arguments("abc/def", ".."),
        arguments("abc/def", "xyz/uvw"),
        arguments("abc/def", " / "),
        arguments("abc/def", "xyz/uvw/rst"),
        arguments("abc/def/ghi", ""),
        arguments("abc/def/ghi", "."),
        arguments("abc/def/ghi", ".."),
        arguments("abc/def/ghi", "xyz/uvw"),
        arguments("abc/def/ghi", " / "),
        arguments("abc/def/ghi", "xyz/uvw/rst"),
        arguments(" / ", ""),
        arguments(" / ", "."),
        arguments(" / ", ".."),
        arguments(" / ", " / "),
        arguments(" / ", "abc/def"));
  }

  @ParameterizedTest
  @MethodSource("withExtension_cases")
  public void withExtension(String path, String extension, String expected) {
    Truth.assertThat(Path.path(path).withExtension(extension)).isEqualTo(Path.path(expected));
  }

  public static Stream<Arguments> withExtension_cases() {
    return Stream.of(
        arguments("abc", "csv", "abc.csv"),
        arguments("path/abc", "csv", "path/abc.csv"),
        arguments("long/path/abc", "csv", "long/path/abc.csv"),
        arguments("abc.txt", "csv", "abc.csv"),
        arguments("path/abc.txt", "csv", "path/abc.csv"),
        arguments("long/path/abc.txt", "csv", "long/path/abc.csv"),
        arguments("abc.txt", "", "abc"),
        arguments("path/abc.txt", "", "path/abc"),
        arguments("long/path/abc.txt", "", "long/path/abc"));
  }

  @ParameterizedTest
  @MethodSource("withExtension_fails_for_cases")
  public void withExtension_fails_for(String path, String part) {
    assertCall(() -> Path.path(path).withExtension(part))
        .throwsException(IllegalArgumentException.class);
  }

  public static Stream<Arguments> withExtension_fails_for_cases() {
    return Stream.of(
        arguments(".", ""),
        arguments(".", "csv"),
        arguments(".", "."),
        arguments(".", ".."),
        arguments(".", "/"),
        arguments(".", "xyz/"),
        arguments(".", "/xyz"),
        arguments(".", "xyz/uvw"),
        arguments(".", " / "),
        arguments(".", "xyz/uvw/rst"),
        arguments("abc", "."),
        arguments("abc", ".."),
        arguments("abc", "/"),
        arguments("abc", "xyz/"),
        arguments("abc", "/xyz"),
        arguments("abc", "xyz/uvw"),
        arguments("abc", " / "),
        arguments("abc", "xyz/uvw/rst"),
        arguments("abc/def", "."),
        arguments("abc/def", ".."),
        arguments("abc/def", "/"),
        arguments("abc/def", "xyz/"),
        arguments("abc/def", "/xyz"),
        arguments("abc/def", "xyz/uvw"),
        arguments("abc/def", " / "),
        arguments("abc/def", "xyz/uvw/rst"));
  }

  @ParameterizedTest
  @MethodSource("parts_cases")
  public void parts(String path, List<String> expectedParts) {
    List<Path> actualParts = Path.path(path).parts();
    assertThat(actualParts.map(Path::toString)).isEqualTo(expectedParts);
  }

  public static Stream<Arguments> parts_cases() {
    return Stream.of(
        arguments(".", list()),
        arguments("abc", list("abc")),
        arguments("abc/def", list("abc", "def")),
        arguments("abc/def/ghi", list("abc", "def", "ghi")),
        arguments(" ", list(" ")),
        arguments(" / ", list(" ", " ")),
        arguments(" / / ", list(" ", " ", " ")));
  }

  @Test
  void last_part_of_root_dir_throws_exception() {
    assertCall(() -> Path.root().lastPart()).throwsException(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("lastPart_cases")
  public void lastPart(String path, String expectedLastPart) {
    Truth.assertThat(Path.path(path).lastPart()).isEqualTo(Path.path(expectedLastPart));
  }

  public static Stream<Arguments> lastPart_cases() {
    return Stream.of(
        arguments(" ", " "),
        arguments(" / ", " "),
        arguments("abc", "abc"),
        arguments("abc/def", "def"),
        arguments("abc/def/ghi", "ghi"));
  }

  @Test
  void first_part_of_root_dir_throws_exception() {
    assertCall(() -> Path.root().firstPart()).throwsException(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("firstPart_cases")
  public void firstPart(String path, String expectedfirstPart) {
    Truth.assertThat(Path.path(path).firstPart()).isEqualTo(Path.path(expectedfirstPart));
  }

  public static Stream<Arguments> firstPart_cases() {
    return Stream.of(
        arguments(" ", " "),
        arguments(" / ", " "),
        arguments("abc", "abc"),
        arguments("abc/def", "abc"),
        arguments("abc/def/ghi", "abc"));
  }

  @ParameterizedTest
  @MethodSource("startWith_cases")
  public void startsWith(String path, String head, boolean expected) {
    Truth.assertThat(Path.path(path).startsWith(Path.path(head))).isEqualTo(expected);
  }

  public static Stream<Arguments> startWith_cases() {
    return Stream.of(
        arguments(".", ".", true),
        arguments("abc", ".", true),
        arguments("abc/def", ".", true),
        arguments("abc/def/ghi", ".", true),
        arguments("abc/def/ghi", "abc", true),
        arguments("abc/def/ghi", "abc/def", true),
        arguments("abc/def/ghi", "abc/def/ghi", true),
        arguments("abc/def/ghi", "ab", false),
        arguments("abc/def/ghi", "abc/d", false),
        arguments("abc/def/ghi", "def", false),
        arguments("abc/def/ghi", "ghi", false),
        arguments(".", "abc", false));
  }

  @Test
  void test_equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(Path.path("."));
    tester.addEqualityGroup(Path.path("abc"));
    tester.addEqualityGroup(Path.path("abc/def"), Path.path("abc/def"));
    tester.addEqualityGroup(Path.path("abc/def/ghi"));
    tester.addEqualityGroup(Path.path("abc/def/ghi/ijk"));

    // These paths look really strange but Linux allows creating them.
    // I cannot see any good reason for forbidding them.
    tester.addEqualityGroup(Path.path("..."));
    tester.addEqualityGroup(Path.path(".../abc"));
    tester.addEqualityGroup(Path.path("abc/..."));
    tester.addEqualityGroup(Path.path("abc/.../def"));

    tester.testEquals();
  }

  @ParameterizedTest
  @MethodSource("paths")
  public void test_to_string(String value, boolean isValid) {
    if (isValid) {
      Truth.assertThat(Path.path(value).toString()).isEqualTo(value);
    }
  }

  public static List<Arguments> paths() {
    return list(

        // zero characters long
        arguments("", false),

        // one character long
        arguments("/", false),
        arguments(".", true),
        arguments("a", true),

        // two characters long
        arguments("..", false),
        arguments("./", false),
        arguments(".a", true),
        arguments("/.", false),
        arguments("//", false),
        arguments("/a", false),
        arguments("a.", true),
        arguments("a/", false),
        arguments("aa", true),

        // three characters long
        arguments("...", true),
        arguments("../", false),
        arguments("..a", true),
        arguments("./.", false),
        arguments(".//", false),
        arguments("./a", false),
        arguments(".a.", true),
        arguments(".a/", false),
        arguments(".aa", true),
        arguments("/..", false),
        arguments("/./", false),
        arguments("/.a", false),
        arguments("//.", false),
        arguments("///", false),
        arguments("//a", false),
        arguments("/a.", false),
        arguments("/a/", false),
        arguments("/aa", false),
        arguments("a..", true),
        arguments("a./", false),
        arguments("a.a", true),
        arguments("a/.", false),
        arguments("a//", false),
        arguments("a/a", true),
        arguments("aa.", true),
        arguments("aa/", false),
        arguments("aaa", true),

        //     four characters long
        arguments("....", true),
        arguments(".../", false),
        arguments("...a", true),
        arguments("../.", false),
        arguments("..//", false),
        arguments("../a", false),
        arguments("..a.", true),
        arguments("..a/", false),
        arguments("..aa", true),
        arguments("./..", false),
        arguments("././", false),
        arguments("./.a", false),
        arguments(".//.", false),
        arguments(".///", false),
        arguments(".//a", false),
        arguments("./a.", false),
        arguments("./a/", false),
        arguments("./aa", false),
        arguments(".a..", true),
        arguments(".a./", false),
        arguments(".a.a", true),
        arguments(".a/.", false),
        arguments(".a//", false),
        arguments(".a/a", true),
        arguments(".aa.", true),
        arguments(".aa/", false),
        arguments(".aaa", true),
        arguments("/...", false),
        arguments("/../", false),
        arguments("/..a", false),
        arguments("/./.", false),
        arguments("/.//", false),
        arguments("/./a", false),
        arguments("/.a.", false),
        arguments("/.a/", false),
        arguments("/.aa", false),
        arguments("//./", false),
        arguments("//./", false),
        arguments("//.a", false),
        arguments("///.", false),
        arguments("////", false),
        arguments("///a", false),
        arguments("//a.", false),
        arguments("//a/", false),
        arguments("//aa", false),
        arguments("/a..", false),
        arguments("/a./", false),
        arguments("/a.a", false),
        arguments("/a/.", false),
        arguments("/a//", false),
        arguments("/a/a", false),
        arguments("/aa.", false),
        arguments("/aa/", false),
        arguments("/aaa", false),
        arguments("a...", true),
        arguments("a../", false),
        arguments("a..a", true),
        arguments("a./.", false),
        arguments("a.//", false),
        arguments("a./a", true),
        arguments("a.a.", true),
        arguments("a.a/", false),
        arguments("a.aa", true),
        arguments("a/..", false),
        arguments("a/./", false),
        arguments("a/.a", true),
        arguments("a//.", false),
        arguments("a///", false),
        arguments("a//a", false),
        arguments("a/a.", true),
        arguments("a/a/", false),
        arguments("a/aa", true),
        arguments("aa..", true),
        arguments("aa./", false),
        arguments("aa.a", true),
        arguments("aa/.", false),
        arguments("aa//", false),
        arguments("aa/a", true),
        arguments("aaa.", true),
        arguments("aaa/", false),
        arguments("aaaa", true),

        // rest
        arguments("abc", true),
        arguments("abc/def", true),
        arguments("abc/def/ghi", true),
        arguments("abc/def/ghi/ijk", true),

        // These paths look really strange but Linux allows creating them.
        // I cannot see any good reason for forbidding them.
        arguments(".../abc", true),
        arguments("abc/...", true),
        arguments("abc/.../def", true));
  }
}
