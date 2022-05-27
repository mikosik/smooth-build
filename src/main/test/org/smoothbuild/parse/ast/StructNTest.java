package org.smoothbuild.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Loc.internal;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StructNTest {
  @Nested
  class ctorNameToTypeName {
    @Test
    public void ctor_name_is_lower_camelcase_of_type_name() {
      StructN struct = new StructN("MyType", list(), internal());
      assertThat(struct.ctor().name())
          .isEqualTo("myType");
    }

    @Test
    public void ctor_name_is_lower_camelcase_of_type_name_preserving_underscores() {
      StructN struct = new StructN("My_Pretty_Type", list(), internal());
      assertThat(struct.ctor().name())
          .isEqualTo("my_Pretty_Type");
    }
  }
}
