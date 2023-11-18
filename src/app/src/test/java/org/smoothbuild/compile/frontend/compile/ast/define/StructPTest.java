package org.smoothbuild.compile.frontend.compile.ast.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Lists.list;
import static org.smoothbuild.compile.frontend.lang.base.location.Locations.internalLocation;

import org.junit.jupiter.api.Test;

public class StructPTest {
  @Test
  public void constructor_name_is_equal_to_struct_name() {
    var structP = new StructP("MyType", list(), internalLocation());
    assertThat(structP.constructor().name())
        .isEqualTo("MyType");
  }
}
