package org.smoothbuild.message.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;

import org.junit.Test;

import com.google.common.testing.EqualsTester;

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
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(codeLocation(1));
    tester.addEqualityGroup(codeLocation(7));
    tester.addEqualityGroup(codeLocation(11), codeLocation(11));

    tester.testEquals();
  }

  @Test
  public void testToString() throws Exception {
    assertThat(codeLocation(2).toString()).isEqualTo("[ line 2 ]");
  }
}
