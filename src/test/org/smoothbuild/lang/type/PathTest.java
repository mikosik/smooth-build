package org.smoothbuild.lang.type;

import static nl.jqno.equalsverifier.Warning.NULL_FIELDS;
import static org.fest.assertions.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.PathUtils.WORKING_DIR;
import static org.smoothbuild.lang.type.Path.path;

import java.util.List;

import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.fs.base.PathUtilsTest;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class PathTest {

  @Test
  public void value() {
    assertValue("abc", "abc");
    assertValue("abc/def", "abc/def");
    assertValue("abc/def/ghi", "abc/def/ghi");
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
  public void parentOfWorkingDirThrowsException() throws Exception {
    try {
      path(WORKING_DIR).parent();
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testParent() throws Exception {
    assertParentOf("abc", WORKING_DIR);
    assertParentOf("abc/def", "abc");
    assertParentOf("abc/def/ghi", "abc/def");

    assertParentOf(" ", WORKING_DIR);
  }

  private static void assertParentOf(String input, String expected) {
    assertThat(path(input).parent().value()).isEqualTo(expected);
  }

  @Test
  public void append() {
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
    assertThat(path(path1).append(path(path2)).value()).isEqualTo(expected);
  }

  @Test
  public void correctPath() {
    for (String path : PathUtilsTest.listOfCorrectPaths()) {
      // make sure path can be created without throwing exception
      path(path);
    }
  }

  @Test
  public void invalidPathDetection() {
    for (String path : PathUtilsTest.listOfInvalidPaths()) {
      try {
        path(path);
        Assert.fail("exception expected");
      } catch (IllegalArgumentException e) {
        // expected
      }
    }
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
