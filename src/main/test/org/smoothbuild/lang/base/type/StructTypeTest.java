package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.TestingLang.field;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.lang.base.type.TestingTypes.FAKE_LOCATION;
import static org.smoothbuild.lang.base.type.Types.array;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.struct;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.Field;

public class StructTypeTest {
  @Test
  public void struct_type_without_fields_can_be_created() {
    struct("Struct", FAKE_LOCATION, list());
  }

  @Test
  public void first_field_type_cannot_be_nothing() {
    assertCall(() -> struct("Struct", FAKE_LOCATION, fields(nothing())))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void first_field_type_cannot_be_nothing_array() {
    assertCall(() -> struct("Struct", FAKE_LOCATION, fields(array(nothing()))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void first_field_type_cannot_be_array() {
    assertCall(() -> struct("Struct", FAKE_LOCATION, fields(array(string()))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void field_can_be_retrieved_by_name() {
    Field field = field(1, 0, string(), "name1");
    StructType struct = struct("Struct", FAKE_LOCATION, List.of(field));
    assertThat(struct.fieldWithName("name1"))
        .isSameInstanceAs(field);
  }

  @Test
  public void contains_field_with_name_returns_true_for_existing_field() {
    StructType struct = struct("Struct", FAKE_LOCATION, List.of(field(1, 0, string(), "name1")));
    assertThat(struct.containsFieldWithName("name1"))
        .isTrue();
  }

  @Test
  public void contains_field_with_name_returns_false_for_not_existing_field() {
    StructType struct = struct("Struct", FAKE_LOCATION, List.of(field(1, 0, string(), "name1")));
    assertThat(struct.containsFieldWithName("name2"))
        .isFalse();
  }

  private static List<Field> fields(Type... types) {
    List<Field> result = new ArrayList<>();
    for (int i = 0; i < types.length; i++) {
      result.add(new Field(i, types[i], "name" + i, internal()));
    }
    return result;
  }
}
