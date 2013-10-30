package org.smoothbuild.message.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Assert;
import org.junit.Test;

public class CodeLocationTest {

  @Test
  public void testGetters() {
    CodeLocation location = codeLocation(1);
    assertThat(location.line()).isEqualTo(1);
  }

  @Test
  public void zeroIndexesAreAllowed() {
    codeLocation(0);
  }

  @Test
  public void negativeLineIsForbidden() throws Exception {
    try {
      codeLocation(-1);
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
    assertThat(codeLocation(1).toString()).isEqualTo("[2]");
  }
}
