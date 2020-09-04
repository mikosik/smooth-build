package org.smoothbuild.db.object.spec;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.db.ObjectDb;
import org.smoothbuild.testing.TestingContext;

public class SpecCachingTest extends TestingContext {

  @Test
  public void creating_array_spec_reuses_cached_instance() {
    assertReturnsSameInstanceEachTime(() -> arraySpec(stringSpec()));
  }

  @Test
  public void reading_array_spec_reuses_cached_instance() {
    Spec spec = arraySpec(stringSpec());
    ObjectDb objectDbOther = objectDbOther();
    assertReturnsSameInstanceEachTime(() -> objectDbOther.getSpec(spec.hash()));
  }

  @Test
  public void creating_tuple_spec_reuses_cached_instance() {
    assertReturnsSameInstanceEachTime(() -> tupleSpec(List.of(stringSpec())));
  }

  @Test
  public void reading_tuple_spec_reuses_cached_instance() {
    Spec spec = tupleSpec(List.of(stringSpec()));
    ObjectDb objectDbOther = objectDbOther();

    assertReturnsSameInstanceEachTime(() -> objectDbOther.getSpec(spec.hash()));
  }

  private static void assertReturnsSameInstanceEachTime(Supplier<Object> supplier) {
    assertThat(supplier.get()).isSameInstanceAs(supplier.get());
  }
}
