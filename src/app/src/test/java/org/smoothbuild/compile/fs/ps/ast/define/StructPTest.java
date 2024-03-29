package org.smoothbuild.compile.fs.ps.ast.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compile.fs.lang.base.location.Locations.internalLocation;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;

public class StructPTest {
  @Test
  public void constructor_name_is_equal_to_struct_name() {
    var structP = new StructP("MyType", list(), internalLocation());
    assertThat(structP.constructor().name())
        .isEqualTo("MyType");
  }
}
