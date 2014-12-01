package org.smoothbuild.io.fs.base;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.Path.rootPath;

import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.testing.io.fs.base.PathTesting;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;
import com.google.common.testing.EqualsTester;

public class PathTest {

  @Test
  public void validationErrorReturnsNullForCorrectPath() throws Exception {
    for (String path : PathTesting.listOfCorrectPaths()) {
      assertNull(Path.validationError(path));
    }
  }

  @Test
  public void pathCanBeCreatedForValidValue() throws Exception {
    for (String path : PathTesting.listOfCorrectPaths()) {
      path(path);
    }
  }

  @Test
  public void validationErrorReturnsMessageForInvalidPath() {
    for (String path : PathTesting.listOfInvalidPaths()) {
      assertNotNull(Path.validationError(path));
    }
  }

  @Test
  public void cannotCreatePathWithInvalidValue() {
    for (String path : PathTesting.listOfInvalidPaths()) {
      try {
        path(path);
        fail("exception should be thrown");
      } catch (IllegalArgumentException e) {
        // expected
      }
    }
  }

  @Test
  public void rootPathReturnsTheSameInstanceEachTime() throws Exception {
    assertSame(Path.rootPath(), Path.rootPath());
  }

  @Test
  public void dotPathReturnsRootPathInstance() throws Exception {
    assertSame(Path.rootPath(), Path.path("."));
  }

  @Test
  public void value() {
    assertValue(".", ".");

    assertValue("abc", "abc");
    assertValue("abc/def", "abc/def");
    assertValue("abc/def/ghi", "abc/def/ghi");

    assertValue("./abc", "abc");
    assertValue("./abc/def", "abc/def");
    assertValue("./abc/def/ghi", "abc/def/ghi");
  }

  private static void assertValue(String path, String expected) {
    assertEquals(expected, path(path).value());
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
    assertEquals(path(expected), path(input).parent());
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
    assertEquals(expected, path(path1).append(path(path2)).value());
  }

  @Test
  public void testParts() throws Exception {
    assertParts(".", Arrays.<String> asList());

    assertParts("abc", asList("abc"));
    assertParts("abc/def", asList("abc", "def"));
    assertParts("abc/def/ghi", asList("abc", "def", "ghi"));

    assertParts(" ", asList(" "));
    assertParts(" / ", asList(" ", " "));
    assertParts(" / / ", asList(" ", " ", " "));
  }

  private static void assertParts(String input, List<String> expected) {
    List<String> list = Lists.newArrayList();
    for (Path path : path(input).parts()) {
      list.add(path.value());
    }
    assertEquals(expected, list);
  }

  @Test
  public void lastPartOfRootDirThrowsException() throws Exception {
    try {
      rootPath().lastPart();
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testLastPart() throws Exception {
    assertLastPart(" ", " ");
    assertLastPart(" / ", " ");

    assertLastPart("abc", "abc");
    assertLastPart("abc/def", "def");
    assertLastPart("abc/def/ghi", "ghi");
  }

  private static void assertLastPart(String input, String expected) {
    assertEquals(path(expected), path(input).lastPart());
  }

  @Test
  public void firstPartOfRootDirThrowsException() throws Exception {
    try {
      rootPath().firstPart();
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testFirstPart() throws Exception {
    assertFirstPart(" ", " ");
    assertFirstPart(" / ", " ");

    assertFirstPart("abc", "abc");
    assertFirstPart("abc/def", "abc");
    assertFirstPart("abc/def/ghi", "abc");
  }

  private static void assertFirstPart(String input, String expected) {
    assertEquals(path(expected), path(input).firstPart());
  }

  @Test
  public void testEqualsAndHashCode() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(path("equal/path"), path("equal/path"));
    for (Path path : listOfCorrectNonEqualPaths()) {
      tester.addEqualityGroup(path);
    }

    tester.testEquals();
  }

  @Test
  public void testToString() {
    assertEquals("'abc/def'", path("abc/def").toString());
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
