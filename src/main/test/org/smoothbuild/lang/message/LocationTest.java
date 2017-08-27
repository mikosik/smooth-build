package org.smoothbuild.lang.message;

import static org.smoothbuild.lang.message.Location.commandLine;
import static org.smoothbuild.lang.message.Location.location;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

import com.google.common.testing.EqualsTester;

public class LocationTest {
  private Location location;

  @Test
  public void line_returns_value_passed_during_construction() {
    given(location = location(13));
    when(location.line());
    thenReturned(13);
  }

  @Test(expected = IllegalArgumentException.class)
  public void zero_line_is_forbidden() {
    location(0);
  }

  @Test(expected = IllegalArgumentException.class)
  public void negative_line_is_forbidden() throws Exception {
    location(-1);
  }

  @Test
  public void equals_and_hash_code() throws Exception {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(location(1));
    tester.addEqualityGroup(location(7));
    tester.addEqualityGroup(location(11), location(11));

    tester.testEquals();
  }

  @Test
  public void file_code_location_to_string() throws Exception {
    given(location = location(2));
    when(location.toString());
    thenReturned("[ line 2 ]");
  }

  @Test
  public void command_line_code_location_to_string() throws Exception {
    given(location = commandLine());
    when(location.toString());
    thenReturned("[ cmd line ]");
  }
}
