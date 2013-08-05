package org.smoothbuild.fs.base;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.PathUtils.WORKING_DIR;
import static org.smoothbuild.fs.base.PathUtils.isValid;
import static org.smoothbuild.fs.base.PathUtils.parentOf;
import static org.smoothbuild.fs.base.PathUtils.toCanonical;
import static org.smoothbuild.fs.base.PathUtils.validationError;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class PathUtilsTest {

  @Test
  public void correctPath() throws Exception {
    for (String path : listOfCorrectPaths()) {
      assertThat(validationError(path)).isNull();
      assertThat(isValid(path)).isTrue();
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
  public void invalidPathsAreDetected() {
    for (String path : listOfInvalidPaths()) {
      assertThat(validationError(path)).isNotNull();
      assertThat(isValid(path)).isFalse();
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
  public void testToCanonical() {
    assertToCanonical(".", ".");
    assertToCanonical("./", ".");

    assertToCanonical("abc", "abc");
    assertToCanonical("abc/def", "abc/def");
    assertToCanonical("abc/def/ghi", "abc/def/ghi");

    assertToCanonical("abc/", "abc");
    assertToCanonical("abc/def/", "abc/def");
    assertToCanonical("abc/def/ghi/", "abc/def/ghi");

    assertToCanonical("./abc", "abc");
    assertToCanonical("./abc/def", "abc/def");
    assertToCanonical("./abc/def/ghi", "abc/def/ghi");

    assertToCanonical("./abc/", "abc");
    assertToCanonical("./abc/def/", "abc/def");
    assertToCanonical("./abc/def/ghi/", "abc/def/ghi");
  }

  private void assertToCanonical(String input, String expected) {
    assertThat(toCanonical(input)).isEqualTo(expected);
  }

  @Test
  public void parentOfWorkingDirThrowsException() throws Exception {
    try {
      parentOf(WORKING_DIR);
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testParentOf() throws Exception {
    assertParentOf("abc", WORKING_DIR);
    assertParentOf("abc/def", "abc");
    assertParentOf("abc/def/ghi", "abc/def");

    assertParentOf(" ", WORKING_DIR);
  }

  private static void assertParentOf(String input, String expected) {
    assertThat(parentOf(input)).isEqualTo(expected);
  }

  @Test
  public void testToElements() throws Exception {
    assertToElements(WORKING_DIR, ImmutableList.<String> of());

    assertToElements("abc", ImmutableList.of("abc"));
    assertToElements("abc/def", ImmutableList.of("abc", "def"));
    assertToElements("abc/def/ghi", ImmutableList.of("abc", "def", "ghi"));

    assertToElements(" ", ImmutableList.of(" "));
    assertToElements(" / ", ImmutableList.of(" ", " "));
    assertToElements(" / / ", ImmutableList.of(" ", " ", " "));
  }

  private static void assertToElements(String input, List<String> expected) {
    assertThat(PathUtils.toElements(input)).isEqualTo(expected);
  }

  @Test
  public void lastElementOfWorkingDirThrowsException() throws Exception {
    try {
      PathUtils.lastElement(WORKING_DIR);
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
    assertThat(PathUtils.lastElement(input)).isEqualTo(expected);
  }

  @Test
  public void testAppend() throws Exception {
    assertAppend(WORKING_DIR, WORKING_DIR, WORKING_DIR);

    assertAppend("abc", WORKING_DIR, "abc");
    assertAppend("abc/def", WORKING_DIR, "abc/def");
    assertAppend("abc/def/ghi", WORKING_DIR, "abc/def/ghi");

    assertAppend(WORKING_DIR, "abc", "abc");
    assertAppend(WORKING_DIR, "abc/def", "abc/def");
    assertAppend(WORKING_DIR, "abc/def/ghi", "abc/def/ghi");

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
    assertThat(PathUtils.append(path1, path2)).isEqualTo(expected);
  }
}
