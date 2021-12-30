package org.smoothbuild.vm.job.job;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.smoothbuild.vm.parallel.ParallelJobExecutor.Worker;

import com.google.common.base.Supplier;

public class LazyJobTest {
  private Supplier<Job> supplier;

  @BeforeEach
  public void beforeEach() {
    supplier = mock(Supplier.class);
    when(supplier.get()).thenReturn(mock(Job.class));
  }

  @Test
  public void supplier_is_not_called_in_ctor() {
    new LazyJob(null, null, supplier);
    verifyNoInteractions(supplier);
  }

  @Test
  public void multiple_calls_to_name_causes_only_one_call_to_supplier() {
    LazyJob lazyJob = new LazyJob(null, null, supplier);
    lazyJob.name();
    lazyJob.name();
    verify(supplier, times(1)).get();
  }

  @Test
  public void multiple_calls_to_deps_causes_only_one_call_to_supplier() {
    LazyJob lazyJob = new LazyJob(null, null, supplier);
    lazyJob.deps();
    lazyJob.deps();
    verify(supplier, times(1)).get();
  }

  @Test
  public void multiple_calls_to_schedule_causes_only_one_call_to_supplier() {
    LazyJob lazyJob = new LazyJob(null, null, supplier);
    lazyJob.schedule(mock(Worker.class));
    lazyJob.schedule(mock(Worker.class));
    verify(supplier, times(1)).get();
  }
}
