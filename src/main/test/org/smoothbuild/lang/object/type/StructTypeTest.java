package org.smoothbuild.lang.object.type;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.lang.object.type.ThoroughTypeMatcher.typeMatchingThoroughly;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.db.ObjectsDb;

import com.google.common.collect.Lists;

public class StructTypeTest extends AbstractTypeTestCase {
  private SObject type2;
  private List<Field> fields;

  @Override
  protected ConcreteType getType(ObjectsDb objectsDb) {
    return objectsDb.structType("Struct", fields(objectsDb.stringType(), objectsDb.stringType()));
  }

  @Test
  public void struct_type_without_fields_can_be_created() throws Exception {
    when(() -> structType("Struct", list()));
    thenReturned();
  }

  @Test
  public void first_field_type_cannot_be_nothing() throws Exception {
    when(() -> structType("Struct", fields(nothingType())));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void first_field_type_cannot_be_nothing_array() throws Exception {
    when(() -> structType("Struct", fields(arrayType(nothingType()))));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void first_field_type_cannot_be_array() throws Exception {
    when(() -> structType("Struct", fields(arrayType(stringType()))));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void struct_type_with_different_field_order_has_different_hash() throws Exception {
    given(fields = fields(stringType(), stringType()));
    given(type = structType("Struct", fields));
    given(type2 = structType("Struct", Lists.reverse(fields)));
    when(() -> type.hash());
    thenReturned(not(equalTo(type2.hash())));
  }

  @Test
  public void two_level_deep_struct_type_can_be_read_back() throws Exception {
    given(fields = fields(stringType(), stringType()));
    given(type = structType("TypeName", fields));
    given(fields = fields(stringType(), type));
    given(type = structType("TypeName2", fields));
    when(() -> new ObjectsDb(hashedDb()).get(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void creating_two_different_structs_with_same_name_is_possible() throws Exception {
    given(fields = fields(stringType()));
    given(type = structType("MyStruct", fields));
    given(fields = fields(stringType(), stringType()));
    given(type2 = structType("MyStruct", fields));
    when(() -> type.hash());
    thenReturned(not(type2.hash()));
  }

  private List<Field> fields(ConcreteType... types) {
    ArrayList<Field> result = new ArrayList<>();
    for (int i = 0; i < types.length; i++) {
      result.add(new Field(types[i], "name" + i, unknownLocation()));
    }
    return result;
  }
}
