package org.smoothbuild.lang.base.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.type.api.StructType;
import org.smoothbuild.testing.TestingContext;

public class StructTypeTest extends TestingContext {
  @Test
  public void struct_type_without_fields_can_be_created() {
    structT("Struct", list());
  }

  @Test
  public void first_field_type_can_be_nothing() {
    structT("Struct", list(nothingT()));
  }

  @Test
  public void first_field_type_can_be_nothing_array() {
    structT("Struct", list(arrayT(nothingT())));
  }

  @Test
  public void different_size_of_fields_and_names_causes_exception() {
    assertCall(() -> structT("Struct", list(stringT()), list("name1", "name2")))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void field_can_be_retrieved_by_name() {
    StructType struct = structT("Struct", list(stringT()), list("field-name"));
    assertThat(struct.fieldWithName("field-name"))
        .isSameInstanceAs(stringT());
  }

  @Test
  public void contains_field_with_name_returns_true_for_existing_field() {
    StructType struct = structT("Struct", list(stringT()), list("name1"));
    assertThat(struct.containsFieldWithName("name1"))
        .isTrue();
  }

  @Test
  public void contains_field_with_name_returns_false_for_not_existing_field() {
    StructType struct = structT("Struct", list(stringT()), list("name1"));
    assertThat(struct.containsFieldWithName("name2"))
        .isFalse();
  }
}
