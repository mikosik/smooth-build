package org.smoothbuild.exec.plan;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smoothbuild.exec.compute.LazyTask;
import org.smoothbuild.exec.compute.Task;
import org.smoothbuild.exec.parallel.ParallelTaskExecutor.Worker;

import com.google.common.base.Supplier;

public class LazyTaskTest {
  private Supplier<Task> supplier;

  @BeforeEach
  public void beforeEach() {
    supplier = mock(Supplier.class);
    when(supplier.get()).thenReturn(mock(Task.class));
  }

  @Test
  public void supplier_is_not_called_in_constructor() {
    new LazyTask(null, null, supplier);
    verifyNoInteractions(supplier);
  }

  @Test
  public void multiple_calls_to_name_causes_only_one_call_to_supplier() {
    LazyTask lazyTask = new LazyTask(null, null, supplier);
    lazyTask.name();
    lazyTask.name();
    verify(supplier, times(1)).get();
  }

  @Test
  public void multiple_calls_to_dependencies_causes_only_one_call_to_supplier() {
    LazyTask lazyTask = new LazyTask(null, null, supplier);
    lazyTask.dependencies();
    lazyTask.dependencies();
    verify(supplier, times(1)).get();
  }

  @Test
  public void multiple_calls_to_description_causes_only_one_call_to_supplier() {
    LazyTask lazyTask = new LazyTask(null, null, supplier);
    lazyTask.description();
    lazyTask.description();
    verify(supplier, times(1)).get();
  }

  @Test
  public void multiple_calls_to_kind_causes_only_one_call_to_supplier() {
    LazyTask lazyTask = new LazyTask(null, null, supplier);
    lazyTask.kind();
    lazyTask.kind();
    verify(supplier, times(1)).get();
  }

  @Test
  public void multiple_calls_to_start_computation_causes_only_one_call_to_supplier() {
    LazyTask lazyTask = new LazyTask(null, null, supplier);
    lazyTask.startComputation(mock(Worker.class));
    lazyTask.startComputation(mock(Worker.class));
    verify(supplier, times(1)).get();
  }
}
