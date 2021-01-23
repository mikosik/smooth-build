package org.smoothbuild.util;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;

public class MemoizingSupplierTest {
  @Test
  public void does_not_call_wrapped_supplier_when_not_needed() {
    ThrowingSupplier<String, RuntimeException> supplier = mock(ThrowingSupplier.class);
    new MemoizingSupplier<>(supplier);
    verifyNoInteractions(supplier);
  }

  @Test
  public void returns_result_from_wrapper_supplier() {
    var memoizer = new MemoizingSupplier<>(() -> "abc");
    assertThat(memoizer.get())
        .isEqualTo("abc");
  }

  @Test
  public void returns_cached_result_from_wrapper_supplier_on_second_invocation() {
    AtomicInteger atomicInteger = new AtomicInteger(1);
    var memoizer = new MemoizingSupplier<>(atomicInteger::getAndIncrement);
    assertThat(memoizer.get())
        .isEqualTo(1);
    assertThat(memoizer.get())
        .isEqualTo(1);
  }
}
