package org.smoothbuild;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.lang.base.Space.USER;

import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.google.common.testing.EqualsTester;

public class ModulePathTest {
  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(unknownLocation(), unknownLocation());
    tester.addEqualityGroup(modulePath("abc", "def"), modulePath("abc", "def"));
    tester.addEqualityGroup(modulePath("abc", "222"), modulePath("abc", "222"));
    tester.addEqualityGroup(modulePath("111", "def"), modulePath("111", "def"));
    tester.addEqualityGroup(modulePath("111", "222"), modulePath("111", "222"));

    tester.testEquals();
  }

  @Test
  public void file_code_location_to_string() {
    ModulePath location = new ModulePath(USER, Paths.get("abc"), "shortPath");
    assertThat(location.toString())
        .isEqualTo("shortPath(abc)");
  }

  private static ModulePath modulePath(String fullPath, String shortPath) {
    return new ModulePath(USER, Paths.get(fullPath), shortPath);
  }
}
