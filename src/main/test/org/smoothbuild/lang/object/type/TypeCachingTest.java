package org.smoothbuild.lang.object.type;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
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
  public void creating_struct_type_reuses_cached_instance() {
    assertReturnsSameInstanceEachTime(() -> structType(List.of(stringType())));
  }

  @Test
  public void reading_struct_type_reuses_cached_instance() {
    ConcreteType type = structType(List.of(stringType()));
    ObjectDb objectDbOther = objectDbOther();

    assertReturnsSameInstanceEachTime(() -> objectDbOther.get(type.hash()));
  }

  private static void assertReturnsSameInstanceEachTime(Supplier<Object> supplier) {
    assertThat(supplier.get()).isSameInstanceAs(supplier.get());
  }
}
