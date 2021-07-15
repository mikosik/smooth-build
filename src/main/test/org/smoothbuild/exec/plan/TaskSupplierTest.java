package org.smoothbuild.exec.plan;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import org.junit.jupiter.api.Test;
import org.smoothbuild.exec.compute.Task;

import com.google.common.base.Supplier;

public class TaskSupplierTest {
  @Test
  public void supplier_is_not_called_in_constructor() {
    Supplier<Task> supplier = mock(Supplier.class);
    new TaskSupplier(null, null, supplier);
    verifyNoInteractions(supplier);
  }

  @Test
  public void multiple_calls_to_get_causes_only_one_call_to_supplier() {
    Supplier<Task> supplier = mock(Supplier.class);
    TaskSupplier taskSupplier = new TaskSupplier(null, null, supplier);
    taskSupplier.getTask();
    taskSupplier.getTask();
    verify(supplier, times(1)).get();
  }

  @Test
  public void second_call_return_same_object_as_first() {
    Task task = mock(Task.class);
    Supplier<Task> supplier = () -> task;
    TaskSupplier taskSupplier = new TaskSupplier(null, null, supplier);
    assertThat(taskSupplier.getTask())
        .isSameInstanceAs(taskSupplier.getTask());
  }
}
