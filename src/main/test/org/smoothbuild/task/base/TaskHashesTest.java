package org.smoothbuild.task.base;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.db.values.ValuesDb.valuesDb;
import static org.smoothbuild.task.base.Computer.valueComputer;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.message.CodeLocation;

import com.google.common.collect.ImmutableList;

public class TaskHashesTest {
  private static final CodeLocation CL = CodeLocation.codeLocation(2);
  private final ValuesDb valuesDb = valuesDb();

  private Task dep;
  private Task dep2;
  private Computer computer;
  private Task task;
  private Task task2;
  private Computer computer2;

  @Test
  public void hashes_of_tasks_with_same_computer_and_dependencies_are_equal() throws Exception {
    given(computer = valueComputer(valuesDb.string("work"), CL));
    given(dep = mock(Task.class));
    given(willReturn(new Output(valuesDb.string("abc"))), dep).output();
    given(task = new Task(computer, ImmutableList.<Task> of(dep)));
    given(task2 = new Task(computer, ImmutableList.<Task> of(dep)));
    when(task).hash();
    thenReturned(task2.hash());
  }

  @Test
  public void hashes_of_tasks_with_same_computer_and_different_dependencies_are_not_equal()
      throws Exception {
    given(computer = valueComputer(valuesDb.string("work"), CL));
    given(dep = mock(Task.class));
    given(willReturn(new Output(valuesDb.string("abc"))), dep).output();
    given(dep2 = mock(Task.class));
    given(willReturn(new Output(valuesDb.string("def"))), dep2).output();
    given(task = new Task(computer, ImmutableList.<Task> of(dep)));
    given(task2 = new Task(computer, ImmutableList.<Task> of(dep2)));
    when(task).hash();
    thenReturned(not(task2.hash()));
  }

  @Test
  public void hashes_of_tasks_with_different_computer_and_same_dependencies_are_not_equal()
      throws Exception {
    given(computer = valueComputer(valuesDb.string("work"), CL));
    given(computer2 = valueComputer(valuesDb.string("work2"), CL));
    given(dep = mock(Task.class));
    given(willReturn(new Output(valuesDb.string("abc"))), dep).output();
    given(task = new Task(computer, ImmutableList.<Task> of(dep)));
    given(task2 = new Task(computer2, ImmutableList.<Task> of(dep)));
    when(task).hash();
    thenReturned(not(task2.hash()));
  }
}
