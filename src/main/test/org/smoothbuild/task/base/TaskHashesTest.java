package org.smoothbuild.task.base;

import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.task.base.Evaluator.valueEvaluator;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.nio.file.Paths;

import org.junit.Test;
import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.message.Location;

public class TaskHashesTest {
  private final Location location = Location.location(Paths.get("script.smooth"), 2);
  private final ValuesDb valuesDb = memoryValuesDb();

  private Evaluator evaluator;
  private Task task;
  private Task task2;

  @Test
  public void hashes_of_tasks_with_same_evaluator_are_equal() throws Exception {
    given(evaluator = valueEvaluator(valuesDb.string("work"), location));
    given(task = new Task(evaluator));
    given(task2 = new Task(evaluator));
    when(task).hash();
    thenReturned(task2.hash());
  }
}
