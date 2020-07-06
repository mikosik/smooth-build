package org.smoothbuild.lang.base.type;

import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.lang.base.type.Types.array;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.struct;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class StructTypeTest {
  @Test
  public void struct_type_without_fields_can_be_created() {
    struct("Struct", list());
  }

  @Test
  public void first_field_type_cannot_be_nothing() {
    assertCall(() -> struct("Struct", fields(nothing())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void first_field_type_cannot_be_nothing_array() {
    assertCall(() -> struct("Struct", fields(array(nothing()))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void first_field_type_cannot_be_array() {
    assertCall(() -> struct("Struct", fields(array(string()))))
        .throwsException(IllegalArgumentException.class);
  }

  private static List<Field> fields(ConcreteType... types) {
    List<Field> result = new ArrayList<>();
    for (int i = 0; i < types.length; i++) {
      result.add(new Field(types[i], "name" + i, unknownLocation()));
    }
    return result;
  }
}
