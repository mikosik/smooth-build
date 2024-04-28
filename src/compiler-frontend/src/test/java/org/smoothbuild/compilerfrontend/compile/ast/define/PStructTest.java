package org.smoothbuild.compilerfrontend.compile.ast.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.compilerfrontend.lang.base.location.Locations.internalLocation;

import org.junit.jupiter.api.Test;

public class PStructTest {
  @Test
  void constructor_name_is_equal_to_struct_name() {
    var structP = new PStruct("MyType", list(), internalLocation());
    assertThat(structP.constructor().name()).isEqualTo("MyType");
  }
}
