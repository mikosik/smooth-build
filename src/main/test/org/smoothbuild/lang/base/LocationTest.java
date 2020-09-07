package org.smoothbuild.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.commandLineLocation;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.Location.location;
import static org.smoothbuild.lang.base.Space.USER;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.nio.file.Path;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class LocationTest {
  @Test
  public void line_returns_value_passed_during_construction() {
    Location location = location(moduleInfo("abc"), 13);
    assertThat(location.line())
        .isEqualTo(13);
  }

  @Test
  public void zero_line_is_forbidden() {
    assertCall(() -> location(moduleInfo("abc"), 0))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void negative_line_is_forbidden() {
    assertCall(() -> location(moduleInfo("abc"), -1))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(internal(), internal());
    tester.addEqualityGroup(commandLineLocation(), commandLineLocation());
    tester.addEqualityGroup(location(moduleInfo("abc"), 7), location(moduleInfo("abc"), 7));
    tester.addEqualityGroup(location(moduleInfo("abc"), 11), location(moduleInfo("abc"), 11));
    tester.addEqualityGroup(location(moduleInfo("def"), 11), location(moduleInfo("def"), 11));

    tester.testEquals();
  }

  @Nested
  class to_string {
    @Test
    public void file() {
      Location location = location(ModuleInfo.moduleInfo(USER, Path.of("abc"), "shortPath"), 2);
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
      Location location = internal();
      assertThat(location.toString())
          .isEqualTo("smooth internal");
    }
  }

  private static ModuleInfo moduleInfo(String name) {
    return ModuleInfo.moduleInfo(USER, Path.of(name), "{SL}/" + name);
  }
}
