package org.smoothbuild.parse.ast;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.parse.ast.StructNode.constructorNameToTypeName;
import static org.smoothbuild.parse.ast.StructNode.typeNameToConstructorName;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class StructNodeTest {
  @Nested
  class constructorNameToTypeName {
    @Test
    public void constructor_name_is_lower_camelcase_of_type_name() {
      StructNode struct = new StructNode("MyType", list(), unknownLocation());
      assertThat(struct.constructor().name())
          .isEqualTo("myType");
    }

    @Test
    public void constructor_name_is_lower_camelcase_of_type_name_preserving_underscores() {
      StructNode struct = new StructNode("My_Pretty_Type", list(), unknownLocation());
      assertThat(struct.constructor().name())
          .isEqualTo("my_Pretty_Type");
    }

    @Test
    public void type_Name_is_upper_camelcase_of_constructor_name() {
      assertThat(constructorNameToTypeName("myType"))
          .isEqualTo("MyType");
    }
  }

  @Nested
  class typeNameToConstructorName {
    @Test
    public void type_Name_is_upper_camelcase_of_constructor_name() {
      assertThat(typeNameToConstructorName("MyType"))
          .isEqualTo("myType");
    }
  }
}
