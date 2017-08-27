package org.smoothbuild.task.base;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.task.base.Evaluator.valueEvaluator;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.message.Location;

import com.google.common.collect.ImmutableList;

public class TaskHashesTest {
  private final Location location = Location.location(2);
  private final ValuesDb valuesDb = memoryValuesDb();

  private Task dep;
  private Task dep2;
  private Evaluator evaluator;
  private Task task;
  private Task task2;
  private Evaluator evaluator2;

  @Test
  public void hashes_of_tasks_with_same_evaluator_and_dependencies_are_equal() throws Exception {
    given(evaluator = valueEvaluator(valuesDb.string("work"), location));
    given(dep = mock(Task.class));
    given(willReturn(new Output(valuesDb.string("abc"))), dep).output();
    given(task = new Task(evaluator, ImmutableList.<Task> of(dep)));
    given(task2 = new Task(evaluator, ImmutableList.<Task> of(dep)));
    when(task).hash();
    thenReturned(task2.hash());
  }

  @Test
  public void hashes_of_tasks_with_same_evaluator_and_different_dependencies_are_not_equal()
      throws Exception {
    given(evaluator = valueEvaluator(valuesDb.string("work"), location));
    given(dep = mock(Task.class));
    given(willReturn(new Output(valuesDb.string("abc"))), dep).output();
    given(dep2 = mock(Task.class));
    given(willReturn(new Output(valuesDb.string("def"))), dep2).output();
    given(task = new Task(evaluator, ImmutableList.<Task> of(dep)));
    given(task2 = new Task(evaluator, ImmutableList.<Task> of(dep2)));
    when(task).hash();
    thenReturned(not(task2.hash()));
  }

  @Test
  public void hashes_of_tasks_with_different_evaluator_and_same_dependencies_are_not_equal()
      throws Exception {
    given(evaluator = valueEvaluator(valuesDb.string("work"), location));
    given(evaluator2 = valueEvaluator(valuesDb.string("work2"), location));
    given(dep = mock(Task.class));
    given(willReturn(new Output(valuesDb.string("abc"))), dep).output();
    given(task = new Task(evaluator, ImmutableList.<Task> of(dep)));
    given(task2 = new Task(evaluator2, ImmutableList.<Task> of(dep)));
    when(task).hash();
    thenReturned(not(task2.hash()));
  }
}
