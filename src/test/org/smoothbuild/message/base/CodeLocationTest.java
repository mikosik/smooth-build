package org.smoothbuild.message.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import nl.jqno.equalsverifier.EqualsVerifier;

import org.junit.Test;
import org.smoothbuild.message.base.CodeLocation;

public class CodeLocationTest {

  @Test
  public void testGetters() {
    CodeLocation location = codeLocation(13);
    assertThat(location.line()).isEqualTo(13);
  }

  @Test(expected = IllegalArgumentException.class)
  public void zeroIndexesAreForbidden() {
    codeLocation(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void negativeLineIsForbidden() throws Exception {
    codeLocation(-1);
  }

  @Test
  public void equalsAndHashCode() throws Exception {
    EqualsVerifier.forClass(CodeLocation.class).verify();
  }

  @Test
  public void testToString() throws Exception {
    assertThat(codeLocation(2).toString()).isEqualTo("[ line 2 ]");
  }
}
