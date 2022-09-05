package org.smoothbuild.compile.ps.ast;

import static org.smoothbuild.compile.lang.base.Loc.internal;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

public class StructPTest {
  @Nested
  class ctorNameToTypeName {
    @Test
    public void ctor_name_is_lower_camelcase_of_type_name() {
      StructP struct = new StructP("MyType", list(), internal());
      Truth.assertThat(struct.ctor().name())
          .isEqualTo("myType");
    }

    @Test
    public void ctor_name_is_lower_camelcase_of_type_name_preserving_underscores() {
      StructP struct = new StructP("My_Pretty_Type", list(), internal());
      Truth.assertThat(struct.ctor().name())
          .isEqualTo("my_Pretty_Type");
    }
  }
}
