package org.smoothbuild;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.Space.USER;

import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.ModuleInfo;

import com.google.common.testing.EqualsTester;

public class ModuleInfoTest {
  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(internal(), internal());
    tester.addEqualityGroup(moduleInfo("abc", "def"), moduleInfo("abc", "def"));
    tester.addEqualityGroup(moduleInfo("abc", "222"), moduleInfo("abc", "222"));
    tester.addEqualityGroup(moduleInfo("111", "def"), moduleInfo("111", "def"));
    tester.addEqualityGroup(moduleInfo("111", "222"), moduleInfo("111", "222"));

    tester.testEquals();
  }

  @Test
  void name() {
    ModuleInfo moduleInfo = moduleInfo("full/module.smooth", "shortPath");
    assertThat(moduleInfo.name())
        .isEqualTo("module");
  }

  @Test
  void smooth_path() {
    ModuleInfo moduleInfo = moduleInfo("full/path.smooth", "shortPath");
    assertThat((Object) moduleInfo.smooth().path())
        .isEqualTo(Path.of("full/path.smooth"));
  }

  @Test
  void smooth_shorted() {
    ModuleInfo moduleInfo = moduleInfo("full/path.smooth", "shortPath");
    assertThat((Object) moduleInfo.smooth().shorted())
        .isEqualTo("shortPath");
  }

  @Test
  void native_path() {
    ModuleInfo moduleInfo = moduleInfo("full/path.smooth", "shortPath");
    assertThat((Object) moduleInfo.nativ().path())
        .isEqualTo(Path.of("full/path.jar"));
  }

  @Test
  void native_shorted() {
    ModuleInfo moduleInfo = moduleInfo("full/path.smooth", "shortPath");
    assertThat((Object) moduleInfo.nativ().shorted())
        .isEqualTo("shortPath.jar");
  }

  @Test
  public void file_code_location_to_string() {
    ModuleInfo location = ModuleInfo.moduleInfo(USER, Path.of("abc"), "shortPath");
    assertThat(location.toString())
        .isEqualTo("shortPath(abc)");
  }

  private static ModuleInfo moduleInfo(String fullPath, String shortPath) {
    return ModuleInfo.moduleInfo(USER, Path.of(fullPath), shortPath);
  }
}
