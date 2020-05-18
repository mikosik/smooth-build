package org.smoothbuild.io.fs.base;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.params.provider.Arguments.arguments;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.google.common.collect.ImmutableList;
import com.google.common.testing.EqualsTester;

public class PathTest {
  @ParameterizedTest
  @MethodSource("validPaths")
  public void path_with_valid_value_can_be_created(String value) {
    path(value);
  }

  @ParameterizedTest
  @MethodSource("invalidPaths")
  public void path_with_invalid_value_cannot_be_created(String value) {
    assertCall(() -> path(value))
        .throwsException(IllegalPathException.class);
  }

  @Test
  public void empty_string_path_is_root() {
    assertThat(path("").isRoot())
        .isTrue();
  }

  @Test
  public void simple_path_is_not_root() {
    assertThat(path("file.txt").isRoot())
        .isFalse();
  }

  @ParameterizedTest
  @MethodSource("validPaths")
  public void value(String value) {
    assertThat(path(value).value())
        .isEqualTo(value);
  }

  @ParameterizedTest
  @MethodSource("validPaths")
  public void toJPath(String value) {
    if (value.equals("")) {
      assertThat((Object) path(value).toJPath())
          .isEqualTo(Paths.get("."));
    } else {
      assertThat((Object) path(value).toJPath())
          .isEqualTo(Paths.get(value));
    }
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
        arguments("", "", ""),
        arguments("abc", "", "abc"),
        arguments("abc/def", "", "abc/def"),
        arguments("abc/def/ghi", "", "abc/def/ghi"),
        arguments("", "abc", "abc"),
        arguments("", "abc/def", "abc/def"),
        arguments("", "abc/def/ghi", "abc/def/ghi"),
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
        arguments("", "abc", "abc"),
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
        arguments("", "", ""),
        arguments("", "abc/def"),
        arguments("", " / "),
        arguments("", "abc/def/ghi"),
        arguments(" ", ""),
        arguments(" ", " / "),
        arguments(" ", "abc/def"),
        arguments("abc", "", "abc"),
        arguments("abc", "xyz/uvw"),
        arguments("abc", " / "),
        arguments("abc", "xyz/uvw/rst"),
        arguments("abc/def", "", "abc/def"),
        arguments("abc/def", "xyz/uvw"),
        arguments("abc/def", " / "),
        arguments("abc/def", "xyz/uvw/rst"),
        arguments("abc/def/ghi", "", "abc/def/ghi"),
        arguments("abc/def/ghi", "xyz/uvw"),
        arguments("abc/def/ghi", " / "),
        arguments("abc/def/ghi", "xyz/uvw/rst"),
        arguments(" / ", ""),
        arguments(" / ", " / "),
        arguments(" / ", "abc/def")
    );
  }

  @ParameterizedTest
  @MethodSource("partsArguments")
  public void parts(String path, List<String> expectedParts) {
    List<Path> actualParts = path(path).parts();
    assertThat(map(actualParts, Path::value))
        .isEqualTo(expectedParts);
  }

  public static Stream<Arguments> partsArguments() {
    return Stream.of(
        arguments("", list()),
        arguments("abc", list("abc")),
        arguments("abc/def", list("abc", "def")),
        arguments("abc/def/ghi", list("abc", "def", "ghi")),
        arguments(" ", list(" ")),
        arguments(" / ", list(" ", " ")),
        arguments(" / / ", list(" ", " ", " ")));
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
  public void startsWith(Path path, Path head, boolean expected) {
    assertThat(path.startsWith(head))
        .isEqualTo(expected);
  }

  public static Stream<Arguments> startWithArguments() {
    return Stream.of(
        arguments(Path.root(), Path.root(), true),
        arguments(path("abc"), Path.root(), true),
        arguments(path("abc/def"), Path.root(), true),
        arguments(path("abc/def/ghi"), Path.root(), true),
        arguments(path("abc/def/ghi"), path("abc"), true),
        arguments(path("abc/def/ghi"), path("abc/def"), true),
        arguments(path("abc/def/ghi"), path("abc/def/ghi"), true),
        arguments(path("abc/def/ghi"), path("ab"), false),
        arguments(path("abc/def/ghi"), path("abc/d"), false),
        arguments(path("abc/def/ghi"), path("def"), false),
        arguments(path("abc/def/ghi"), path("ghi"), false),
        arguments(Path.root(), path("abc"), false));
  }

  @Test
  public void test_equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(path("equal/path"), path("equal/path"));
    for (Path path : listOfCorrectNonEqualPaths()) {
      tester.addEqualityGroup(path);
    }

    tester.testEquals();
  }

  @Test
  public void test_to_string() {
    assertThat(path("abc/def").toString())
        .isEqualTo("'abc/def'");
  }

  public static List<String> validPaths() {
    ImmutableList.Builder<String> builder = ImmutableList.builder();

    builder.add("");

    builder.add("abc");
    builder.add("abc/def");
    builder.add("abc/def/ghi");
    builder.add("abc/def/ghi/ijk");

    // These paths look really strange but Linux allows creating them.
    // I cannot see any good reason for forbidding them.
    builder.add("...");
    builder.add(".../abc");
    builder.add("abc/...");
    builder.add("abc/.../def");

    return builder.build();
  }

  public static ImmutableList<String> invalidPaths() {
    ImmutableList.Builder<String> builder = ImmutableList.builder();

    builder.add("/");
    builder.add(".");

    builder.add("./");
    builder.add("./.");
    builder.add("././");

    builder.add("abc/");
    builder.add("abc/def/");
    builder.add("abc/def/ghi/");

    builder.add("./abc");
    builder.add("./abc/def");
    builder.add("./abc/def/ghi");

    builder.add("..");
    builder.add("../");
    builder.add("./../");
    builder.add("../abc");
    builder.add("abc/..");
    builder.add("abc/../def");
    builder.add("../..");

    builder.add("//");
    builder.add("///");

    builder.add("/abc");
    builder.add("//abc");
    builder.add("///abc");

    builder.add("abc//");
    builder.add("abc///");

    builder.add("abc//def");
    builder.add("abc///def");

    return builder.build();
  }

  private static List<Path> listOfCorrectNonEqualPaths() {
    ImmutableList.Builder<Path> builder = ImmutableList.builder();

    builder.add(path(""));
    builder.add(path("abc"));
    builder.add(path("abc/def"));
    builder.add(path("abc/def/ghi"));
    builder.add(path("abc/def/ghi/ijk"));

    // These paths look really strange but Linux allows creating them.
    // I cannot see any good reason for forbidding them.
    builder.add(path("..."));
    builder.add(path(".../abc"));
    builder.add(path("abc/..."));
    builder.add(path("abc/.../def"));

    return builder.build();
  }
}
