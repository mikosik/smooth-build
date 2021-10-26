package org.smoothbuild.lang.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.define.Location.internal;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StructNodeTest {
  @Nested
  class constructorNameToTypeName {
    @Test
    public void constructor_name_is_lower_camelcase_of_type_name() {
      StructNode struct = new StructNode("MyType", list(), internal());
      assertThat(struct.constructor().name())
          .isEqualTo("myType");
    }

    @Test
    public void constructor_name_is_lower_camelcase_of_type_name_preserving_underscores() {
      StructNode struct = new StructNode("My_Pretty_Type", list(), internal());
      assertThat(struct.constructor().name())
          .isEqualTo("my_Pretty_Type");
    }
  }
}
