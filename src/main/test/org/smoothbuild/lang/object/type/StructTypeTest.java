package org.smoothbuild.lang.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.object.db.ObjectDb;

import com.google.common.collect.Lists;

public class StructTypeTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(ObjectDb objectDb) {
    return objectDb.structType(List.of(objectDb.stringType(), objectDb.stringType()));
  }

  @Test
  public void struct_type_without_fields_can_be_created() {
    structType(list());
  }

  @Test
  public void struct_type_with_different_field_order_has_different_hash() {
    List<ConcreteType> fields = List.of(stringType(), blobType());
    StructType type = structType(fields);
    StructType type2 = structType(Lists.reverse(fields));
    assertThat(type.hash())
        .isNotEqualTo(type2.hash());
  }

  @Test
  public void two_level_deep_struct_type_can_be_read_back() {
    StructType type = structType(List.of(stringType(), stringType()));
    assertTypesAreDeeplyEqual((ConcreteType) objectDbOther().get(type.hash()), type);
  }

  @Test
  public void creating_same_struct_twice_is_possible() {
    structType(List.of(stringType()));
    structType(List.of(stringType()));
  }
}
