package org.smoothbuild.io.fs.base;

import static com.google.common.truth.Truth.assertThat;
import static java.text.MessageFormat.format;
import static org.quackery.Case.newCase;
import static org.quackery.Suite.suite;
import static org.quackery.report.AssertException.assertEquals;
import static org.quackery.report.AssertException.assertTrue;
import static org.quackery.report.AssertException.fail;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.nio.file.Paths;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.quackery.Case;
import org.quackery.Quackery;
import org.quackery.Suite;
import org.quackery.junit.QuackeryRunner;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.testing.EqualsTester;

@RunWith(QuackeryRunner.class)
public class PathTest {
  @Quackery
  public static Suite path_value_is_validated() {
    return suite("path value is validated")
        .add(suite("path can be created for valid name")
            .addAll(map(listOfCorrectPaths(), PathTest::pathCanBeCreatedForValidName)))
        .add(suite("cannot create path with invalid value")
            .addAll(map(listOfInvalidPaths(), PathTest::cannotCreatePathWithInvalidValue)));
  }

  private static Case pathCanBeCreatedForValidName(String path) {
    return newCase(format("path [{0}]", path), () -> path(path));
  }

  private static Case cannotCreatePathWithInvalidValue(String path) {
    return newCase(format("path [{0}]", path), () -> {
      try {
        path(path);
        fail();
      } catch (IllegalPathException e) {
      }
    });
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

  @Quackery
  public static Suite implements_value() {
    return suite("implements value")
        .add(testValue("", ""))
        .add(testValue("abc", "abc"))
        .add(testValue("abc/def", "abc/def"))
        .add(testValue("abc/def/ghi", "abc/def/ghi"));
  }

  private static Case testValue(String path, String value) {
    return newCase(format("path [{0}] has value [{1}]", path, value),
        () -> assertEquals(path(path).value(), value));
  }

  @Quackery
  public static Suite implements_toJPath() {
    return suite("implements toJPath")
        .add(testToJPath("", Paths.get(".")))
        .add(testToJPath("abc", Paths.get("abc")))
        .add(testToJPath("abc/def", Paths.get("abc/def")))
        .add(testToJPath("abc/def/ghi", Paths.get("abc/def/ghi")));
  }

  private static Case testToJPath(String path, java.nio.file.Path jPath) {
    return newCase(format("path [{0}] converted to JPath equals [{1}]", path, jPath),
        () -> assertEquals(path(path).toJPath(), jPath));
  }

  @Test
  public void parent_of_root_dir_throws_exception() {
    assertCall(() -> Path.root().parent())
        .throwsException(IllegalArgumentException.class);
  }

  @Quackery
  public static Suite implements_parent() {
    return suite("implements parent")
        .add(testParent("abc", ""))
        .add(testParent("abc/def", "abc"))
        .add(testParent("abc/def/ghi", "abc/def"))
        .add(testParent(" ", ""));
  }

  private static Case testParent(String path, String parent) {
    return newCase(format("parent of [{0}] is [{1}]", path, parent),
        () -> assertEquals(path(path).parent(), path(parent)));
  }

  @Quackery
  public static Suite implements_appending() {
    return suite("implements appending")
        .add(testAppending("", "", ""))
        .add(testAppending("abc", "", "abc"))
        .add(testAppending("abc/def", "", "abc/def"))
        .add(testAppending("abc/def/ghi", "", "abc/def/ghi"))
        .add(testAppending("", "abc", "abc"))
        .add(testAppending("", "abc/def", "abc/def"))
        .add(testAppending("", "abc/def/ghi", "abc/def/ghi"))
        .add(testAppending("abc", "xyz", "abc/xyz"))
        .add(testAppending("abc", "xyz/uvw", "abc/xyz/uvw"))
        .add(testAppending("abc", "xyz/uvw/rst", "abc/xyz/uvw/rst"))
        .add(testAppending("abc/def", "xyz", "abc/def/xyz"))
        .add(testAppending("abc/def", "xyz/uvw", "abc/def/xyz/uvw"))
        .add(testAppending("abc/def", "xyz/uvw/rst", "abc/def/xyz/uvw/rst"))
        .add(testAppending("abc/def/ghi", "xyz", "abc/def/ghi/xyz"))
        .add(testAppending("abc/def/ghi", "xyz/uvw", "abc/def/ghi/xyz/uvw"))
        .add(testAppending("abc/def/ghi", "xyz/uvw/rst", "abc/def/ghi/xyz/uvw/rst"))
        .add(testAppending(" ", " ", " / "))
        .add(testAppending(" ", " / ", " / / "))
        .add(testAppending(" / ", " ", " / / "))
        .add(testAppending(" / ", " / ", " / / / "));
  }

  private static Case testAppending(String first, String second, String expected) {
    return newCase(format("appending [{0}] to [{1}] returns [{2}]", first, second, expected),
        () -> {
          String actual = path(first).append(path(second)).value();
          assertEquals(actual, expected);
        });
  }

  @Quackery
  public static Suite implements_parts() {
    return suite("implements parts")
        .add(testParts("", list()))
        .add(testParts("abc", list("abc")))
        .add(testParts("abc/def", list("abc", "def")))
        .add(testParts("abc/def/ghi", list("abc", "def", "ghi")))
        .add(testParts(" ", list(" ")))
        .add(testParts(" / ", list(" ", " ")))
        .add(testParts(" / / ", list(" ", " ", " ")));
  }

  private static Case testParts(String path, List<String> parts) {
    return newCase(format("[{0}] has parts: {1}", path, parts),
        () -> {
          List<String> actualParts = map(path(path).parts(), Path::value);
          assertEquals(actualParts, parts);
        });
  }

  @Test
  public void last_part_of_root_dir_throws_exception() {
    assertCall(() -> Path.root().lastPart())
        .throwsException(IllegalArgumentException.class);
  }

  @Quackery
  public static Suite implements_last_part() {
    return suite("implements lastPart")
        .add(testLastPart(" ", " "))
        .add(testLastPart(" / ", " "))
        .add(testLastPart("abc", "abc"))
        .add(testLastPart("abc/def", "def"))
        .add(testLastPart("abc/def/ghi", "ghi"));
  }

  private static Case testLastPart(String path, String lastPart) {
    return newCase(format("last part of [{0}] is [{1}]", path, lastPart),
        () -> assertEquals(path(path).lastPart(), path(lastPart)));
  }

  @Test
  public void first_part_of_root_dir_throws_exception() {
    assertCall(() -> Path.root().firstPart())
        .throwsException(IllegalArgumentException.class);
  }

  @Quackery
  public static Suite implements_first_part() {
    return suite("implements firstPart")
        .add(testFirstPart(" ", " "))
        .add(testFirstPart(" / ", " "))
        .add(testFirstPart("abc", "abc"))
        .add(testFirstPart("abc/def", "abc"))
        .add(testFirstPart("abc/def/ghi", "abc"));
  }

  private static Case testFirstPart(String path, String firstPart) {
    return newCase(format("first part of [{0}] is [{1}]", path, firstPart),
        () -> assertEquals(path(path).firstPart(), path(firstPart)));
  }

  @Quackery
  public static Suite implements_start_with() {
    return suite("implements startsWith")
        .add(testStartsWith(Path.root(), Path.root()))
        .add(testStartsWith(Path.path("abc"), Path.root()))
        .add(testStartsWith(Path.path("abc/def"), Path.root()))
        .add(testStartsWith(Path.path("abc/def/ghi"), Path.root()))
        .add(testStartsWith(Path.path("abc/def/ghi"), Path.path("abc")))
        .add(testStartsWith(Path.path("abc/def/ghi"), Path.path("abc/def")))
        .add(testStartsWith(Path.path("abc/def/ghi"), Path.path("abc/def/ghi")))
        .add(testNotStartsWith(Path.path("abc/def/ghi"), Path.path("ab")))
        .add(testNotStartsWith(Path.path("abc/def/ghi"), Path.path("abc/d")))
        .add(testNotStartsWith(Path.path("abc/def/ghi"), Path.path("def")))
        .add(testNotStartsWith(Path.path("abc/def/ghi"), Path.path("ghi")))
        .add(testNotStartsWith(Path.root(), Path.path("abc")));
  }

  private static Case testStartsWith(Path path, Path head) {
    return newCase(format("{0} starts with {1}", path, head),
        () -> assertTrue(path.startsWith(head)));
  }

  private static Case testNotStartsWith(Path path, Path notHead) {
    return newCase(format("{0} not starts with {1}", path, notHead),
        () -> assertTrue(!path.startsWith(notHead)));
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

  public static List<String> listOfCorrectPaths() {
    Builder<String> builder = ImmutableList.builder();

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

  public static ImmutableList<String> listOfInvalidPaths() {
    Builder<String> builder = ImmutableList.builder();

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
    Builder<Path> builder = ImmutableList.builder();

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
