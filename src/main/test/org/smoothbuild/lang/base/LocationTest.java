package org.smoothbuild.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.commandLineLocation;
import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.lang.base.Space.USER;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.nio.file.Paths;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

@SuppressWarnings("ClassCanBeStatic")
public class LocationTest {
  @Test
  public void line_returns_value_passed_during_construction() {
    Location location = location(modulePath("abc"), 13);
    assertThat(location.line())
        .isEqualTo(13);
  }

  @Test
  public void zero_line_is_forbidden() {
    assertCall(() -> location(modulePath("abc"), 0))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void negative_line_is_forbidden() {
    assertCall(() -> location(modulePath("abc"), -1))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(unknownLocation(), unknownLocation());
    tester.addEqualityGroup(commandLineLocation(), commandLineLocation());
    tester.addEqualityGroup(location(modulePath("abc"), 7), location(modulePath("abc"), 7));
    tester.addEqualityGroup(location(modulePath("abc"), 11), location(modulePath("abc"), 11));
    tester.addEqualityGroup(location(modulePath("def"), 11), location(modulePath("def"), 11));

    tester.testEquals();
  }

  @Nested
  class to_string {
    @Test
    public void file() {
      Location location = location(new ModulePath(USER, Paths.get("abc"), "shortPath"), 2);
      assertThat(location.toString())
          .isEqualTo("shortPath:2");
    }

    @Test
    public void command_line() {
      assertThat(commandLineLocation().toString())
          .isEqualTo("command line");
    }

    @Test
    public void unknown() {
      Location location = unknownLocation();
      assertThat(location.toString())
          .isEqualTo("unknown location");
    }
  }

  @Nested
  class to_padded_string {
    @Test
    public void file() {
      Location location = location(new ModulePath(USER, Paths.get("abc"), "shortPath"), 2);
      assertThat(location.toPaddedString())
          .isEqualTo("shortPath                    2");
    }

    @Test
    public void command_line() {
      assertThat(commandLineLocation().toPaddedString())
          .isEqualTo("command line                  ");
    }

    @Test
    public void unknown() {
      Location location = unknownLocation();
      assertThat(location.toPaddedString())
          .isEqualTo("unknown location              ");
    }
  }

  private static ModulePath modulePath(String name) {
    return new ModulePath(USER, Paths.get(name), "{SL}/" + name);
  }
}
