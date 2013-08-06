package org.smoothbuild.lang.type;

import static nl.jqno.equalsverifier.Warning.NULL_FIELDS;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.fail;
import static org.smoothbuild.lang.type.Path.path;
import static org.smoothbuild.lang.type.Path.rootPath;

import java.util.List;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;

public class PathTest {

  @Test
  public void validationErrorReturnsNullForCorrectPath() throws Exception {
    for (String path : listOfCorrectPaths()) {
      assertThat(Path.validationError(path)).isNull();
    }
  }

  @Test
  public void pathCanBeCreatedForValidValue() throws Exception {
    for (String path : listOfCorrectPaths()) {
      path(path);
    }
  }

  public static List<String> listOfCorrectPaths() {
    Builder<String> builder = ImmutableList.builder();

    builder.add(".");
    builder.add("./");

    builder.add("abc");
    builder.add("abc/def");
    builder.add("abc/def/ghi");
    builder.add("abc/def/ghi/ijk");

    builder.add("abc/");
    builder.add("abc/def/");
    builder.add("abc/def/ghi/");
    builder.add("abc/def/ghi/ijk/");

    builder.add("./abc");
    builder.add("./abc/def");
    builder.add("./abc/def/ghi");
    builder.add("./abc/def/ghi/ijk");

    builder.add("./abc/");
    builder.add("./abc/def/");
    builder.add("./abc/def/ghi/");
    builder.add("./abc/def/ghi/ijk/");

    // These paths look really strange but Linux allows creating them.
    // I cannot see any good reason for forbidding them.
    builder.add("...");
    builder.add(".../abc");
    builder.add("abc/...");
    builder.add("abc/.../def");

    builder.add(".../");
    builder.add(".../abc/");
    builder.add("abc/.../");
    builder.add("abc/.../def/");

    builder.add("./...");
    builder.add("./.../abc");
    builder.add("./abc/...");
    builder.add("./abc/.../def");

    builder.add("./.../");
    builder.add("./.../abc/");
    builder.add("./abc/.../");
    builder.add("./abc/.../def/");

    return builder.build();
  }

  @Test
  public void validationErrorReturnsMessageForInvalidPath() {
    for (String path : listOfInvalidPaths()) {
      assertThat(Path.validationError(path)).isNotNull();
    }
  }

  @Test
  public void cannotCreatePathWithInvalidValue() {
    for (String path : listOfInvalidPaths()) {
      try {
        path(path);
        fail("exception should be thrown");
      } catch (IllegalArgumentException e) {
        // expected
      }
    }
  }

  public static ImmutableList<String> listOfInvalidPaths() {
    Builder<String> builder = ImmutableList.builder();

    builder.add("");

    builder.add("./.");
    builder.add("././");

    builder.add("..");
    builder.add("../");
    builder.add("./../");
    builder.add("../abc");
    builder.add("abc/..");
    builder.add("abc/../def");
    builder.add("../..");

    builder.add("/");
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

  @Test
  public void rootPathReturnsTheSameInstanceEachTime() throws Exception {
    assertThat(Path.rootPath()).isSameAs(Path.rootPath());
  }

  @Test
  public void dotPathReturnsRootPathInstance() throws Exception {
    assertThat(Path.path(".")).isSameAs(Path.rootPath());
  }

  @Test
  public void value() {
    assertValue(".", ".");
    assertValue("./", ".");

    assertValue("abc", "abc");
    assertValue("abc/def", "abc/def");
    assertValue("abc/def/ghi", "abc/def/ghi");

    assertValue("abc/", "abc");
    assertValue("abc/def/", "abc/def");
    assertValue("abc/def/ghi/", "abc/def/ghi");

    assertValue("./abc", "abc");
    assertValue("./abc/def", "abc/def");
    assertValue("./abc/def/ghi", "abc/def/ghi");

    assertValue("./abc/", "abc");
    assertValue("./abc/def/", "abc/def");
    assertValue("./abc/def/ghi/", "abc/def/ghi");
  }

  private static void assertValue(String path, String expected) {
    assertThat(path(path).value()).isEqualTo(expected);
  }

  @Test
  public void endingSlashIsStripped() {
    assertValue("abc/", "abc");
    assertValue("abc/def/", "abc/def");
    assertValue("abc/def/ghi/", "abc/def/ghi");
  }

  @Test
  public void parentOfRootDirThrowsException() throws Exception {
    try {
      Path.rootPath().parent();
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testParent() throws Exception {
    assertParentOf("abc", ".");
    assertParentOf("abc/def", "abc");
    assertParentOf("abc/def/ghi", "abc/def");

    assertParentOf(" ", ".");
  }

  private static void assertParentOf(String input, String expected) {
    assertThat(path(input).parent()).isEqualTo(path(expected));
  }

  @Test
  public void append() {
    assertAppend(".", ".", ".");

    assertAppend("abc", ".", "abc");
    assertAppend("abc/def", ".", "abc/def");
    assertAppend("abc/def/ghi", ".", "abc/def/ghi");

    assertAppend(".", "abc", "abc");
    assertAppend(".", "abc/def", "abc/def");
    assertAppend(".", "abc/def/ghi", "abc/def/ghi");

    assertAppend("abc", "xyz", "abc/xyz");
    assertAppend("abc", "xyz/uvw", "abc/xyz/uvw");
    assertAppend("abc", "xyz/uvw/rst", "abc/xyz/uvw/rst");

    assertAppend("abc/def", "xyz", "abc/def/xyz");
    assertAppend("abc/def", "xyz/uvw", "abc/def/xyz/uvw");
    assertAppend("abc/def", "xyz/uvw/rst", "abc/def/xyz/uvw/rst");

    assertAppend("abc/def/ghi", "xyz", "abc/def/ghi/xyz");
    assertAppend("abc/def/ghi", "xyz/uvw", "abc/def/ghi/xyz/uvw");
    assertAppend("abc/def/ghi", "xyz/uvw/rst", "abc/def/ghi/xyz/uvw/rst");

    assertAppend(" ", " ", " / ");
    assertAppend(" ", " / ", " / / ");
    assertAppend(" / ", " ", " / / ");
    assertAppend(" / ", " / ", " / / / ");
  }

  private static void assertAppend(String path1, String path2, String expected) {
    assertThat(path(path1).append(path(path2)).value()).isEqualTo(expected);
  }

  @Test
  public void testToElements() throws Exception {
    assertToElements(".", ImmutableList.<String> of());

    assertToElements("abc", ImmutableList.of("abc"));
    assertToElements("abc/def", ImmutableList.of("abc", "def"));
    assertToElements("abc/def/ghi", ImmutableList.of("abc", "def", "ghi"));

    assertToElements(" ", ImmutableList.of(" "));
    assertToElements(" / ", ImmutableList.of(" ", " "));
    assertToElements(" / / ", ImmutableList.of(" ", " ", " "));
  }

  private static void assertToElements(String input, List<String> expected) {
    List<String> list = Lists.newArrayList();
    for (Path path : path(input).toElements()) {
      list.add(path.value());
    }
    assertThat(list).isEqualTo(expected);
  }

  @Test
  public void lastElementOfRootDirThrowsException() throws Exception {
    try {
      rootPath().lastElement();
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testLastElement() throws Exception {
    assertLastElement(" ", " ");
    assertLastElement(" / ", " ");

    assertLastElement("abc", "abc");
    assertLastElement("abc/def", "def");
    assertLastElement("abc/def/ghi", "ghi");
  }

  private static void assertLastElement(String input, String expected) {
    assertThat(path(input).lastElement()).isEqualTo(path(expected));
  }

  @Test
  public void testEqualsAndHashCode() {
    EqualsVerifier.forExamples(path("a"), path("b"), listOfCorrectNonEqualPaths().toArray())
        .suppress(NULL_FIELDS).verify();
  }

  @Test
  public void testToString() {
    assertThat(path("abc/def").toString()).isEqualTo("'abc/def'");
  }

  private static List<Path> listOfCorrectNonEqualPaths() {
    Builder<Path> builder = ImmutableList.builder();

    builder.add(path("."));
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
