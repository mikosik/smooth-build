package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.fs.base.PathS.path;
import static org.smoothbuild.fs.space.Space.PRJ;
import static org.smoothbuild.lang.base.define.Loc.commandLineLoc;
import static org.smoothbuild.lang.base.define.Loc.internal;
import static org.smoothbuild.lang.base.define.Loc.loc;
import static org.smoothbuild.lang.base.define.Loc.unknown;
import static org.smoothbuild.testing.TestingContext.filePath;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.fs.space.FilePath;

import com.google.common.testing.EqualsTester;

public class LocTest {
  @Test
  public void line_returns_value_passed_during_construction() {
    Loc loc = loc(fLoc("abc"), 13);
    assertThat(loc.line())
        .isEqualTo(13);
  }

  @Test
  public void zero_line_is_forbidden() {
    assertCall(() -> loc(fLoc("abc"), 0))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void negative_line_is_forbidden() {
    assertCall(() -> loc(fLoc("abc"), -1))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(internal(), internal());
    tester.addEqualityGroup(commandLineLoc(), commandLineLoc());
    tester.addEqualityGroup(loc(fLoc("abc"), 7), loc(fLoc("abc"), 7));
    tester.addEqualityGroup(loc(fLoc("abc"), 11), loc(fLoc("abc"), 11));
    tester.addEqualityGroup(loc(fLoc("def"), 11), loc(fLoc("def"), 11));

    tester.testEquals();
  }

  @Nested
  class to_string {
    @Test
    public void file() {
      Loc loc = loc(filePath("abc"), 2);
      assertThat(loc.toString())
          .isEqualTo("abc:2");
    }

    @Test
    public void command_line() {
      assertThat(commandLineLoc().toString())
          .isEqualTo("command line");
    }

    @Test
    public void internal_loc() {
      Loc loc = internal();
      assertThat(loc.toString())
          .isEqualTo("internal");
    }

    @Test
    public void unknown_loc() {
      Loc loc = unknown();
      assertThat(loc.toString())
          .isEqualTo("unknown");
    }
  }

  private static FilePath fLoc(String name) {
    return FilePath.filePath(PRJ, path(name));
  }
}
