package org.smoothbuild.common.log.location;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.filesystem.base.FullPath.fullPath;
import static org.smoothbuild.common.filesystem.base.Path.path;
import static org.smoothbuild.common.log.location.Locations.commandLineLocation;
import static org.smoothbuild.common.log.location.Locations.fileLocation;
import static org.smoothbuild.common.log.location.Locations.internalLocation;
import static org.smoothbuild.common.log.location.Locations.unknownLocation;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.testing.CommonTestContext;

public class LocationTest extends CommonTestContext {
  @Nested
  class _source_location {
    @Test
    void line_returns_value_passed_during_construction() {
      var location = fileLocation(fPath(), 13);
      assertThat(location.line()).isEqualTo(13);
    }

    @Test
    void zero_line_is_forbidden() {
      assertCall(() -> fileLocation(fPath(), 0)).throwsException(IllegalArgumentException.class);
    }

    @Test
    void negative_line_is_forbidden() {
      assertCall(() -> fileLocation(fPath(), -1)).throwsException(IllegalArgumentException.class);
    }

    @Test
    void to_string() {
      var location = fileLocation(fPath(), 2);
      assertThat(location.toString()).isEqualTo("{t-alias}/module.smooth:2");
    }
  }

  @Test
  void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(unknownLocation(), unknownLocation());
    tester.addEqualityGroup(internalLocation(), internalLocation());
    tester.addEqualityGroup(commandLineLocation(), commandLineLocation());
    tester.addEqualityGroup(fileLocation(fPath(), 7), fileLocation(fPath(), 7));
    tester.addEqualityGroup(fileLocation(fPath(), 11), fileLocation(fPath(), 11));
    tester.addEqualityGroup(fileLocation(fPath("def"), 11), fileLocation(fPath("def"), 11));
    tester.testEquals();
  }

  @Nested
  class command_line {
    @Test
    void to_string() {
      assertThat(commandLineLocation().toString()).isEqualTo("command line");
    }
  }

  @Nested
  class internal_location {
    @Test
    void to_string() {
      var location = internalLocation();
      assertThat(location.toString()).isEqualTo("internal");
    }
  }

  @Nested
  class _unknown_location {
    @Test
    void to_string() {
      var location = unknownLocation();
      assertThat(location.toString()).isEqualTo("???");
    }
  }

  private FullPath fPath() {
    return fPath("module.smooth");
  }

  private FullPath fPath(String path) {
    return fullPath(alias(), path(path));
  }
}
