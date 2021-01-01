package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.TestingLang.field;
import static org.smoothbuild.lang.base.type.TestingTypes.a;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.struct;
import static org.smoothbuild.util.Lists.list;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.Item;

public class StructTypeTest {
  @Test
  public void struct_type_without_fields_can_be_created() {
    struct("Struct", list());
  }

  @Test
  public void first_field_type_can_be_nothing() {
    struct("Struct", fields(nothing()));
  }

  @Test
  public void first_field_type_can_be_nothing_array() {
    struct("Struct", fields(a(nothing())));
  }

  @Test
  public void field_can_be_retrieved_by_name() {
    Item field = field(1, string(), "name1");
    StructType struct = struct("Struct", List.of(field));
    assertThat(struct.fieldWithName("name1"))
        .isSameInstanceAs(field);
  }

  @Test
  public void contains_field_with_name_returns_true_for_existing_field() {
    StructType struct = struct("Struct", List.of(field(1, string(), "name1")));
    assertThat(struct.containsFieldWithName("name1"))
        .isTrue();
  }

  @Test
  public void contains_field_with_name_returns_false_for_not_existing_field() {
    StructType struct = struct("Struct", List.of(field(1, string(), "name1")));
    assertThat(struct.containsFieldWithName("name2"))
        .isFalse();
  }

  private static List<Item> fields(Type... types) {
    List<Item> result = new ArrayList<>();
    for (int i = 0; i < types.length; i++) {
      result.add(new Item(types[i], "name" + i, Optional.empty()));
    }
    return result;
  }
}
