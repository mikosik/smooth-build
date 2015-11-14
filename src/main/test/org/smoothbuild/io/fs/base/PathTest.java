package org.smoothbuild.io.fs.base;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.testing.io.fs.base.PathTesting;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.testing.EqualsTester;

public class PathTest {

  @Test
  public void validation_error_returns_null_for_correct_path() throws Exception {
    for (String path : PathTesting.listOfCorrectPaths()) {
      assertNull(Path.validationError(path));
    }
  }

  @Test
  public void path_can_be_created_for_valid_value() throws Exception {
    for (String path : PathTesting.listOfCorrectPaths()) {
      path(path);
    }
  }

  @Test
  public void validation_error_returns_message_for_invalid_path() {
    for (String path : PathTesting.listOfInvalidPaths()) {
      assertNotNull(Path.validationError(path));
    }
  }

  @Test
  public void cannot_create_path_with_invalid_value() {
    for (String path : PathTesting.listOfInvalidPaths()) {
      try {
        path(path);
        fail("exception should be thrown for " + path);
      } catch (IllegalArgumentException e) {
        // expected
      }
    }
  }

  @Test
  public void empty_string_path_is_root() throws Exception {
    assertTrue(path("").isRoot());
  }

  @Test
  public void simple_path_is_not_root() throws Exception {
    assertFalse(path("file.txt").isRoot());
  }

  @Test
  public void value() {
    assertValue("", "");

    assertValue("abc", "abc");
    assertValue("abc/def", "abc/def");
    assertValue("abc/def/ghi", "abc/def/ghi");
  }

  private static void assertValue(String path, String expected) {
    assertEquals(expected, path(path).value());
  }

  @Test
  public void parent_of_root_dir_throws_exception() throws Exception {
    try {
      Path.root().parent();
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void test_parent() throws Exception {
    assertParentOf("abc", "");
    assertParentOf("abc/def", "abc");
    assertParentOf("abc/def/ghi", "abc/def");

    assertParentOf(" ", "");
  }

  private static void assertParentOf(String input, String expected) {
    assertEquals(path(expected), path(input).parent());
  }

  @Test
  public void append() {
    assertAppend("", "", "");

    assertAppend("abc", "", "abc");
    assertAppend("abc/def", "", "abc/def");
    assertAppend("abc/def/ghi", "", "abc/def/ghi");

    assertAppend("", "abc", "abc");
    assertAppend("", "abc/def", "abc/def");
    assertAppend("", "abc/def/ghi", "abc/def/ghi");

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
  public void test_parts() throws Exception {
    assertParts("", Arrays.<String> asList());

    assertParts("abc", asList("abc"));
    assertParts("abc/def", asList("abc", "def"));
    assertParts("abc/def/ghi", asList("abc", "def", "ghi"));

    assertParts(" ", asList(" "));
    assertParts(" / ", asList(" ", " "));
    assertParts(" / / ", asList(" ", " ", " "));
  }

  private static void assertParts(String input, List<String> expected) {
    List<String> list = new ArrayList<>();
    for (Path path : path(input).parts()) {
      list.add(path.value());
    }
    assertEquals(expected, list);
  }

  @Test
  public void last_part_of_root_dir_throws_exception() throws Exception {
    try {
      Path.root().lastPart();
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void test_last_part() throws Exception {
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
  public void first_part_of_root_dir_throws_exception() throws Exception {
    try {
      Path.root().firstPart();
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void test_first_part() throws Exception {
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
  public void test_starts_with() throws Exception {
    assertTrue(Path.root().startsWith(Path.root()));
    assertTrue(Path.path("abc").startsWith(Path.root()));
    assertTrue(Path.path("abc/def").startsWith(Path.root()));
    assertTrue(Path.path("abc/def/ghi").startsWith(Path.root()));

    assertTrue(Path.path("abc/def/ghi").startsWith(Path.path("abc")));
    assertTrue(Path.path("abc/def/ghi").startsWith(Path.path("abc/def")));
    assertTrue(Path.path("abc/def/ghi").startsWith(Path.path("abc/def/ghi")));

    assertFalse(Path.path("abc/def/ghi").startsWith(Path.path("ab")));
    assertFalse(Path.path("abc/def/ghi").startsWith(Path.path("abc/d")));
    assertFalse(Path.path("abc/def/ghi").startsWith(Path.path("def")));
    assertFalse(Path.path("abc/def/ghi").startsWith(Path.path("ghi")));

    assertFalse(Path.root().startsWith(Path.path("abc")));
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
    assertEquals("'abc/def'", path("abc/def").toString());
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
