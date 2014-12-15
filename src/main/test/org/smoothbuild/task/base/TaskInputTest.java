package org.smoothbuild.task.base;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.not;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.util.Empty;

public class TaskInputTest {
  private Task depTask1;
  private Task depTask2;
  private TaskInput taskInput;
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private SString sstring1;
  private SString sstring2;
  private TaskInput taskInput2;

  @Test
  public void task_input_takes_values_from_dependency_tasks() {
    given(depTask1 = mock(Task.class));
    given(sstring1 = objectsDb.string("abc"));
    given(willReturn(new TaskOutput(sstring1)), depTask1).output();
    given(taskInput = TaskInput.fromTaskReturnValues(asList(depTask1)));
    when(taskInput).values();
    thenReturned(contains(sstring1));
  }

  @Test
  public void different_inputs_have_different_hashes() throws Exception {
    given(depTask1 = mock(Task.class));
    given(depTask2 = mock(Task.class));
    given(sstring1 = objectsDb.string("abc"));
    given(sstring2 = objectsDb.string("def"));
    given(willReturn(new TaskOutput(sstring1)), depTask1).output();
    given(willReturn(new TaskOutput(sstring2)), depTask2).output();
    given(taskInput = TaskInput.fromTaskReturnValues(asList(depTask1)));
    given(taskInput2 = TaskInput.fromTaskReturnValues(asList(depTask2)));
    when(taskInput).hash();
    thenReturned(not(taskInput2.hash()));
  }

  @Test
  public void inputs_with_same_values_but_in_different_order_have_different_hashes()
      throws Exception {
    given(depTask1 = mock(Task.class));
    given(depTask2 = mock(Task.class));
    given(sstring1 = objectsDb.string("abc"));
    given(sstring2 = objectsDb.string("def"));
    given(willReturn(new TaskOutput(sstring1)), depTask1).output();
    given(willReturn(new TaskOutput(sstring2)), depTask2).output();
    given(taskInput = TaskInput.fromTaskReturnValues(asList(depTask1, depTask2)));
    given(taskInput2 = TaskInput.fromTaskReturnValues(asList(depTask2, depTask1)));
    when(taskInput).hash();
    thenReturned(not(taskInput2.hash()));
  }

  @Test
  public void equal_inputs_have_equal_hashes() throws Exception {
    given(depTask1 = mock(Task.class));
    given(depTask2 = mock(Task.class));
    given(sstring1 = objectsDb.string("abc"));
    given(willReturn(new TaskOutput(sstring1)), depTask1).output();
    given(willReturn(new TaskOutput(sstring1)), depTask2).output();
    given(taskInput = TaskInput.fromTaskReturnValues(asList(depTask1)));
    given(taskInput2 = TaskInput.fromTaskReturnValues(asList(depTask2)));
    when(taskInput).hash();
    thenReturned(taskInput2.hash());
  }

  @Test
  public void input_with_no_values_has_hash_different_from_input_with_one_value() throws Exception {
    given(depTask1 = mock(Task.class));
    given(depTask2 = mock(Task.class));
    given(sstring1 = objectsDb.string("abc"));
    given(willReturn(new TaskOutput(sstring1)), depTask1).output();
    given(taskInput = TaskInput.fromTaskReturnValues(asList(depTask1)));
    given(taskInput2 = TaskInput.fromTaskReturnValues(Empty.taskList()));
    when(taskInput).hash();
    thenReturned(not(taskInput2.hash()));
  }
}
