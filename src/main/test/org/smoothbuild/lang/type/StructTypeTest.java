package org.smoothbuild.lang.type;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.type.ThoroughTypeMatcher.typeMatchingThoroughly;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.value.Value;

import com.google.common.collect.ImmutableMap;

public class StructTypeTest extends AbstractTypeTestCase {
  private Value type2;
  private ImmutableMap<String, Type> fields;

  @Override
  protected Type getType(TypesDb typesDb) {
    return typesDb.struct("Struct", ImmutableMap.of("a", typesDb.string(), "b", typesDb.string()));
  }

  @Test
  public void struct_type_without_fields_can_be_created() throws Exception {
    when(() -> typesDb.struct("Struct", ImmutableMap.of()));
    thenReturned();
  }

  @Test
  public void first_field_type_cannot_be_nothing() throws Exception {
    when(() -> typesDb.struct("Struct", ImmutableMap.of("field", typesDb.nothing())));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void first_field_type_cannot_be_nothing_array() throws Exception {
    when(() -> typesDb.struct(
        "Struct", ImmutableMap.of("field", typesDb.array(typesDb.nothing()))));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void first_field_type_cannot_be_array() throws Exception {
    when(() -> typesDb.struct(
        "Struct", ImmutableMap.of("field", typesDb.array(typesDb.string()))));
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void struct_type_with_different_field_order_has_different_hash() throws Exception {
    given(type = typesDb.struct("Struct",
        ImmutableMap.of("a", typesDb.string(), "b", typesDb.string())));
    given(type2 = typesDb.struct("Struct",
        ImmutableMap.of("b", typesDb.string(), "a", typesDb.string())));
    when(() -> type.hash());
    thenReturned(not(equalTo(type2.hash())));
  }

  @Test
  public void two_level_deep_struct_type_can_be_read_back() throws Exception {
    given(fields = ImmutableMap.of("field1", typesDb.string(), "field2", typesDb.string()));
    given(type = typesDb.struct("TypeName", fields));
    given(fields = ImmutableMap.of("field1", typesDb.string(), "field2", type));
    given(type = typesDb.struct("TypeName2", fields));
    when(() -> new TypesDb(hashedDb).read(type.hash()));
    thenReturned(typeMatchingThoroughly(type));
  }

  @Test
  public void creating_two_different_structs_with_same_name_is_possible() throws Exception {
    given(fields = ImmutableMap.of("field1", typesDb.string()));
    given(type = typesDb.struct("MyStruct", fields));
    given(fields = ImmutableMap.of("field1", typesDb.string(), "field2", typesDb.string()));
    given(type2 = typesDb.struct("MyStruct", fields));
    when(() -> type.hash());
    thenReturned(not(type2.hash()));
  }
}
