package org.smoothbuild.lang.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.internal;
import static org.smoothbuild.util.Lists.list;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.object.db.ObjectDb;

import com.google.common.collect.Lists;

public class StructTypeTest extends AbstractTypeTestCase {
  @Override
  protected ConcreteType getType(ObjectDb objectDb) {
    return objectDb.structType("Struct", fields(objectDb.stringType(), objectDb.stringType()));
  }

  @Test
  public void struct_type_without_fields_can_be_created() {
    structType("Struct", list());
  }

  @Test
  public void struct_type_with_different_field_order_has_different_hash() {
    List<Field> fields = fields(stringType(), stringType());
    StructType type = structType("Struct", fields);
    StructType type2 = structType("Struct", Lists.reverse(fields));
    assertThat(type.hash())
        .isNotEqualTo(type2.hash());
  }

  @Test
  public void two_level_deep_struct_type_can_be_read_back() {
    StructType type = structType("TypeName", fields(stringType(), stringType()));
    assertTypesAreDeeplyEqual((ConcreteType) objectDbOther().get(type.hash()), type);
  }

  @Test
  public void creating_two_different_structs_with_same_name_is_possible() {
    StructType type = structType("MyStruct", fields(stringType()));
    StructType type2 = structType("MyStruct", fields(stringType(), stringType()));
    assertThat(type.hash())
        .isNotEqualTo(type2.hash());
  }

  private static List<Field> fields(ConcreteType... types) {
    ArrayList<Field> result = new ArrayList<>();
    for (int i = 0; i < types.length; i++) {
      result.add(new Field(types[i], "name" + i, internal()));
    }
    return result;
  }
}
