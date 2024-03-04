package org.smoothbuild.compilerfrontend.lang.base.location;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compilerfrontend.lang.base.location.Locations.commandLineLocation;
import static org.smoothbuild.compilerfrontend.lang.base.location.Locations.fileLocation;
import static org.smoothbuild.compilerfrontend.lang.base.location.Locations.internalLocation;
import static org.smoothbuild.compilerfrontend.lang.base.location.Locations.unknownLocation;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestingExpressionS;

public class LocationTest extends TestingExpressionS {
  @Nested
  class _source_location {
    @Test
    public void line_returns_value_passed_during_construction() {
      var location = fileLocation(fullPath("abc"), 13);
      assertThat(location.line()).isEqualTo(13);
    }

    @Test
    public void zero_line_is_forbidden() {
      assertCall(() -> fileLocation(fullPath("abc"), 0))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void negative_line_is_forbidden() {
      assertCall(() -> fileLocation(fullPath("abc"), -1))
          .throwsException(IllegalArgumentException.class);
    }

    @Test
    public void to_string() {
      var location = fileLocation(fullPath("abc"), 2);
      assertThat(location.toString()).isEqualTo("{prj}/abc:2");
    }
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(unknownLocation(), unknownLocation());
    tester.addEqualityGroup(internalLocation(), internalLocation());
    tester.addEqualityGroup(commandLineLocation(), commandLineLocation());
    tester.addEqualityGroup(fileLocation(fullPath("abc"), 7), fileLocation(fullPath("abc"), 7));
    tester.addEqualityGroup(fileLocation(fullPath("abc"), 11), fileLocation(fullPath("abc"), 11));
    tester.addEqualityGroup(fileLocation(fullPath("def"), 11), fileLocation(fullPath("def"), 11));
    tester.testEquals();
  }

  @Nested
  class command_line {
    @Test
    public void to_string() {
      assertThat(commandLineLocation().toString()).isEqualTo("command line");
    }
  }

  @Nested
  class internal_location {
    @Test
    public void to_string() {
      var location = internalLocation();
      assertThat(location.toString()).isEqualTo("internal");
    }
  }

  @Nested
  class _unknown_location {
    @Test
    public void to_string() {
      var location = unknownLocation();
      assertThat(location.toString()).isEqualTo("???");
    }
  }
}
