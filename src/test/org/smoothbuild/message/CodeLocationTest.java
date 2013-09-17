package org.smoothbuild.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.CodeLocation.codeLocation;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Assert;
import org.junit.Test;

public class CodeLocationTest {

  @Test
  public void testGetters() {
    CodeLocation location = codeLocation(1, 2, 3);

    assertThat(location.line()).isEqualTo(1);
    assertThat(location.start()).isEqualTo(2);
    assertThat(location.end()).isEqualTo(3);
  }

  @Test
  public void negativeLineIsForbidden() throws Exception {
    try {
      codeLocation(-1, 1, 1);
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void zeroLineIsForbidden() throws Exception {
    try {
      codeLocation(0, 1, 1);
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void negativeStartIsForbidden() throws Exception {
    try {
      codeLocation(1, -1, 1);
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void negativeEndIsForbidden() throws Exception {
    try {
      codeLocation(1, 1, -1);
      Assert.fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void equalsAndHashCode() throws Exception {
    EqualsVerifier.forClass(CodeLocation.class).verify();
  }

  @Test
  public void testToString() throws Exception {
    assertThat(codeLocation(1, 2, 3).toString()).isEqualTo("[1:2-3]");
  }
}
