package org.smoothbuild.problem;

import static org.fest.assertions.api.Assertions.assertThat;

import org.junit.Assert;
import org.junit.Test;

public class SourceLocationTest {

  @Test
  public void testGetters() {
    SourceLocation location = new SourceLocation(1, 2, 3);

    assertThat(location.line()).isEqualTo(1);
    assertThat(location.startPosition()).isEqualTo(2);
    assertThat(location.endPosition()).isEqualTo(3);
  }

  @Test
  public void negativeLineIsForbidden() throws Exception {
    try {
      new SourceLocation(-1, 1, 1);
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void zeroLineIsForbidden() throws Exception {
    try {
      new SourceLocation(0, 1, 1);
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void negativeStartPositionIsForbidden() throws Exception {
    try {
      new SourceLocation(1, -1, 1);
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void negativeEndPositionIsForbidden() throws Exception {
    try {
      new SourceLocation(1, 1, -1);
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void testToString() throws Exception {
    assertThat(new SourceLocation(1, 2, 3).toString()).isEqualTo("[1:2-3]");
  }
}
