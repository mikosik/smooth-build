package org.smoothbuild.lang.type;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.type.ThoroughTypeMatcher.typeMatchingThoroughly;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
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
}
