package org.smoothbuild.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.nio.file.Paths;

import org.junit.Test;

import com.google.common.testing.EqualsTester;

public class LocationTest {
  private Location location;

  @Test
  public void line_returns_value_passed_during_construction() {
    location = location(Paths.get("abc"), 13);
    assertThat(location.line())
        .isEqualTo(13);
  }

  @Test
  public void zero_line_is_forbidden() {
    assertCall(() -> location(Paths.get("abc"), 0))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void negative_line_is_forbidden() {
    assertCall(() -> location(Paths.get("abc"), -1))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(unknownLocation(), unknownLocation());
    tester.addEqualityGroup(location(Paths.get("abc"), 7), location(Paths.get("abc"), 7));
    tester.addEqualityGroup(location(Paths.get("abc"), 11), location(Paths.get("abc"), 11));
    tester.addEqualityGroup(location(Paths.get("def"), 11), location(Paths.get("def"), 11));

    tester.testEquals();
  }

  @Test
  public void file_code_location_to_string() {
    location = location(Paths.get("abc"), 2);
    assertThat(location.toString())
        .isEqualTo("abc:2");
  }

  @Test
  public void command_line_code_location_to_string() {
    location = unknownLocation();
    assertThat(location.toString())
        .isEqualTo("unknown location");
  }
}
