package org.smoothbuild.lang.base;

import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.nio.file.Paths;

import org.junit.Test;

import com.google.common.testing.EqualsTester;

public class LocationTest {
  private Location location;

  @Test
  public void line_returns_value_passed_during_construction() {
    given(location = location(Paths.get("abc"), 13));
    when(location.line());
    thenReturned(13);
  }

  public void zero_line_is_forbidden() {
    when(() -> location(Paths.get("abc"), 0));
    thenThrown(IllegalArgumentException.class);
  }

  public void negative_line_is_forbidden() throws Exception {
    when(() -> location(Paths.get("abc"), -1));
    thenThrown(IllegalArgumentException.class);
  }

  public void null_file_is_forbidden() throws Exception {
    when(() -> location(null, 1));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void equals_and_hash_code() throws Exception {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(unknownLocation(), unknownLocation());
    tester.addEqualityGroup(location(Paths.get("abc"), 7), location(Paths.get("abc"), 7));
    tester.addEqualityGroup(location(Paths.get("abc"), 11), location(Paths.get("abc"), 11));
    tester.addEqualityGroup(location(Paths.get("def"), 11), location(Paths.get("def"), 11));

    tester.testEquals();
  }

  @Test
  public void file_code_location_to_string() throws Exception {
    given(location = location(Paths.get("abc"), 2));
    when(location.toString());
    thenReturned("abc:2");
  }

  @Test
  public void command_line_code_location_to_string() throws Exception {
    given(location = unknownLocation());
    when(location.toString());
    thenReturned("unknown location");
  }
}
