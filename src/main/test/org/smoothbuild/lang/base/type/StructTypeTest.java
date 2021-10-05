package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.TestingItemSignature.itemSignature;
import static org.smoothbuild.util.Lists.list;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class StructTypeTest extends TestingContext {
  @Test
  public void struct_type_without_fields_can_be_created() {
    structT("Struct", list());
  }

  @Test
  public void first_field_type_can_be_nothing() {
    structT("Struct", fields(nothingT()));
  }

  @Test
  public void first_field_type_can_be_nothing_array() {
    structT("Struct", fields(arrayT(nothingT())));
  }

  @Test
  public void field_can_be_retrieved_by_name() {
    ItemSignature field = itemSignature(stringT(), "name1");
    StructType struct = structT("Struct", list(field));
    assertThat(struct.fieldWithName("name1"))
        .isSameInstanceAs(field);
  }

  @Test
  public void contains_field_with_name_returns_true_for_existing_field() {
    StructType struct = structT("Struct", list(itemSignature(stringT(), "name1")));
    assertThat(struct.containsFieldWithName("name1"))
        .isTrue();
  }

  @Test
  public void contains_field_with_name_returns_false_for_not_existing_field() {
    StructType struct = structT("Struct", list(itemSignature(stringT(), "name1")));
    assertThat(struct.containsFieldWithName("name2"))
        .isFalse();
  }

  private static ImmutableList<ItemSignature> fields(Type... types) {
    Builder<ItemSignature> builder = ImmutableList.builder();
    for (int i = 0; i < types.length; i++) {
      builder.add(new ItemSignature(types[i], "name" + i, Optional.empty()));
    }
    return builder.build();
  }
}
