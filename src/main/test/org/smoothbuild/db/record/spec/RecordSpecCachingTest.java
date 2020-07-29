package org.smoothbuild.db.record.spec;

import static com.google.common.truth.Truth.assertThat;

import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.record.db.RecordDb;
import org.smoothbuild.testing.TestingContext;

public class RecordSpecCachingTest extends TestingContext {

  @Test
  public void creating_array_spec_reuses_cached_instance() {
    assertReturnsSameInstanceEachTime(() -> arraySpec(stringSpec()));
  }

  @Test
  public void reading_array_spec_reuses_cached_instance() {
    Spec spec = arraySpec(stringSpec());
    RecordDb recordDbOther = recordDbOther();
    assertReturnsSameInstanceEachTime(() -> recordDbOther.get(spec.hash()));
  }

  @Test
  public void creating_tuple_spec_reuses_cached_instance() {
    assertReturnsSameInstanceEachTime(() -> tupleSpec(List.of(stringSpec())));
  }

  @Test
  public void reading_tuple_spec_reuses_cached_instance() {
    Spec spec = tupleSpec(List.of(stringSpec()));
    RecordDb recordDbOther = recordDbOther();

    assertReturnsSameInstanceEachTime(() -> recordDbOther.get(spec.hash()));
  }

  private static void assertReturnsSameInstanceEachTime(Supplier<Object> supplier) {
    assertThat(supplier.get()).isSameInstanceAs(supplier.get());
  }
}
