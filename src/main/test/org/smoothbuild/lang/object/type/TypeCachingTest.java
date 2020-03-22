package org.smoothbuild.lang.object.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.object.db.ObjectDb;
import org.smoothbuild.testing.TestingContext;

public class TypeCachingTest extends TestingContext {

  @Test
  public void creating_array_type_reuses_cached_instance() {
    assertReturnsSameInstanceEachTime(() -> arrayType(stringType()));
  }

  @Test
  public void reading_array_type_reuses_cached_instance() {
    ConcreteType type = arrayType(stringType());
    ObjectDb objectDbOther = objectDbOther();
    assertReturnsSameInstanceEachTime(() -> objectDbOther.get(type.hash()));
  }

  @Test
  public void creating_array_type_reuses_cached_instance_of_its_supertype() {
    StructType superType = structType("MySuperType", list());
    StructType type = structType("MyType", list(field(superType)));
    assertReturnsSameInstanceEachTime(() -> arrayType(type).superType());
  }

  @Test
  public void reading_array_type_reuses_cached_instance_of_its_supertype() {
    StructType superType = structType("MySuperType", list());
    StructType myType = structType("MyType", list(field(superType)));
    ConcreteType type = arrayType(myType);
    ObjectDb objectDbOther = objectDbOther();

    assertReturnsSameInstanceEachTime(
        () -> ((ConcreteType) objectDbOther.get(type.hash())).superType());
  }

  @Test
  public void creating_struct_type_reuses_cached_instance() {
    List<Field> fields = list(new Field(stringType(), "name", unknownLocation()));
    assertReturnsSameInstanceEachTime(() -> structType("MyStruct", fields));
  }

  @Test
  public void reading_struct_type_reuses_cached_instance() {
    List<Field> fields = list(new Field(stringType(), "name", unknownLocation()));
    ConcreteType type = structType("MyStruct", fields);
    ObjectDb objectDbOther = objectDbOther();

    assertReturnsSameInstanceEachTime(() -> objectDbOther.get(type.hash()));
  }

  private static void assertReturnsSameInstanceEachTime(Supplier<Object> supplier) {
    assertThat(supplier.get()).isSameInstanceAs(supplier.get());
  }

  private static Field field(StructType superType) {
    return new Field(superType, "name", unknownLocation());
  }
}
