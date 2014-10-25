package org.smoothbuild.message.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.smoothbuild.message.base.CodeLocation.commandLine;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

import com.google.common.testing.EqualsTester;

public class CodeLocationTest {
  private CodeLocation codeLocation;

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
  public void file_code_location_to_string() throws Exception {
    given(codeLocation = codeLocation(2));
    when(codeLocation.toString());
    thenReturned("[ line 2 ]");
  }

  @Test
  public void command_line_code_location_to_string() throws Exception {
    given(codeLocation = commandLine());
    when(codeLocation.toString());
    thenReturned("[ cmd line ]");
  }
}
