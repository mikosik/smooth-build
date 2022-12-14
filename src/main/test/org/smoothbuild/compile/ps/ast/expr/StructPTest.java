package org.smoothbuild.compile.ps.ast.expr;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compile.lang.base.location.Locations.internalLocation;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StructPTest {
  @Nested
  class ctorNameToTypeName {
    @Test
    public void ctor_name_is_lower_camelcase_of_type_name() {
      var structP = new StructP("MyType", list(), internalLocation());
      assertThat(structP.constructor().name())
          .isEqualTo("myType");
    }

    @Test
    public void ctor_name_is_lower_camelcase_of_type_name_preserving_underscores() {
      var structP = new StructP("My_Pretty_Type", list(), internalLocation());
      assertThat(structP.constructor().name())
          .isEqualTo("my_Pretty_Type");
    }
  }
}
