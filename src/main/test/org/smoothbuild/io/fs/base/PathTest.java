package org.smoothbuild.io.fs.base;

import static com.google.common.truth.Truth.assertThat;
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.testing.EqualsTester;

public class PathTest {
  @ParameterizedTest
  @MethodSource("paths")
  public void path_creation(String value, boolean isValid) {
    if (isValid) {
      path(value);
    } else {
      assertCall(() -> path(value))
          .throwsException(IllegalPathException.class);
    }
  }

  @Test
  public void single_dot_string_path_is_root() {
    assertThat(path(".").isRoot())
        .isTrue();
  }

  @Test
  public void simple_path_is_not_root() {
    assertThat(path("file.txt").isRoot())
        .isFalse();
  }

  @Test
  public void parent_of_root_dir_throws_exception() {
    assertCall(() -> Path.root().parent())
        .throwsException(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("parentArguments")
  public void parent_of_normal_path(Path path, Path expectedParent) {
    assertThat(path.parent())
        .isEqualTo(expectedParent);
  }

  public static Stream<Arguments> parentArguments() {
    return Stream.of(
        arguments(path("abc"), Path.root()),
        arguments(path(" "), Path.root()),
        arguments(path("abc/def"), path("abc")),
        arguments(path("abc/def/ghi"), path("abc/def")),
        arguments(path("abc/def/ghi/ijk"), path("abc/def/ghi")));
  }

  @ParameterizedTest
  @MethodSource("appendArguments")
  public void append(String path, String appendedPath, String expected) {
    assertThat(path(path).append(path(appendedPath)))
        .isEqualTo(path(expected));
  }

  public static Stream<Arguments> appendArguments() {
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
  @MethodSource("appendPartArguments")
  public void appendPart(String path, String part, String expected) {
    assertThat(path(path).appendPart(part))
        .isEqualTo(path(expected));
  }

  public static Stream<Arguments> appendPartArguments() {
    return Stream.of(
        arguments(".", "abc", "abc"),
        arguments("abc", "xyz", "abc/xyz"),
        arguments("abc/def", "xyz", "abc/def/xyz"),
        arguments("abc/def/ghi", "xyz", "abc/def/ghi/xyz"),
        arguments(" ", " ", " / "),
        arguments(" / ", " ", " / / ")
    );
  }

  @ParameterizedTest
  @MethodSource("appendPartIllegalArguments")
  public void appendPart_fails_for(String path, String part) {
    assertCall(() -> path(path).appendPart(part))
        .throwsException(IllegalArgumentException.class);
  }

  public static Stream<Arguments> appendPartIllegalArguments() {
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
        arguments(" / ", "abc/def")
    );
  }

  @ParameterizedTest
  @MethodSource("partsArguments")
  public void parts(String path, List<String> expectedParts) {
    List<Path> actualParts = path(path).parts();
    assertThat(map(actualParts, Path::toString))
        .isEqualTo(expectedParts);
  }

  public static Stream<Arguments> partsArguments() {
    return Stream.of(
        arguments(".", list()),
        arguments("abc", list("abc")),
        arguments("abc/def", list("abc", "def")),
        arguments("abc/def/ghi", list("abc", "def", "ghi")),
        arguments(" ", list(" ")),
        arguments(" / ", list(" ", " ")),
        arguments(" / / ", list(" ", " ", " "))
    );
  }

  @Test
  public void last_part_of_root_dir_throws_exception() {
    assertCall(() -> Path.root().lastPart())
        .throwsException(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("lastPartArguments")
  public void lastPart(String path, String expectedLastPart) {
    assertThat(path(path).lastPart())
        .isEqualTo(path(expectedLastPart));
  }

  public static Stream<Arguments> lastPartArguments() {
    return Stream.of(
        arguments(" ", " "),
        arguments(" / ", " "),
        arguments("abc", "abc"),
        arguments("abc/def", "def"),
        arguments("abc/def/ghi", "ghi"));
  }

  @Test
  public void first_part_of_root_dir_throws_exception() {
    assertCall(() -> Path.root().firstPart())
        .throwsException(IllegalArgumentException.class);
  }

  @ParameterizedTest
  @MethodSource("firstPartArguments")
  public void firstPart(String path, String expectedfirstPart) {
    assertThat(path(path).firstPart())
        .isEqualTo(path(expectedfirstPart));
  }

  public static Stream<Arguments> firstPartArguments() {
    return Stream.of(
        arguments(" ", " "),
        arguments(" / ", " "),
        arguments("abc", "abc"),
        arguments("abc/def", "abc"),
        arguments("abc/def/ghi", "abc"));
  }

  @ParameterizedTest
  @MethodSource("startWithArguments")
  public void startsWith(String path, String head, boolean expected) {
    assertThat(path(path).startsWith(path(head)))
        .isEqualTo(expected);
  }

  public static Stream<Arguments> startWithArguments() {
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
  public void test_equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(path("."));
    tester.addEqualityGroup(path("abc"));
    tester.addEqualityGroup(path("abc/def"), path("abc/def"));
    tester.addEqualityGroup(path("abc/def/ghi"));
    tester.addEqualityGroup(path("abc/def/ghi/ijk"));

    // These paths look really strange but Linux allows creating them.
    // I cannot see any good reason for forbidding them.
    tester.addEqualityGroup(path("..."));
    tester.addEqualityGroup(path(".../abc"));
    tester.addEqualityGroup(path("abc/..."));
    tester.addEqualityGroup(path("abc/.../def"));

    tester.testEquals();
  }

  @ParameterizedTest
  @MethodSource("paths")
  public void test_to_string(String value, boolean isValid) {
    if (isValid) {
      assertThat(path(value).toString())
          .isEqualTo(value);
    }
  }

  public static List<Arguments> validPaths() {
    return paths().stream()
        .filter(a -> a.get()[1].equals(Boolean.TRUE))
        .collect(toList());
  }

  public static List<Arguments> paths() {
    return List.of(

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
        arguments("abc/.../def", true)
    );
  }
}
