package org.smoothbuild.task.base;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.work.ConstantWorker;
import org.smoothbuild.task.work.TaskWorker;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

import com.google.common.collect.ImmutableList;

public class TaskHashesTest {
  private static final CodeLocation CL = CodeLocation.codeLocation(2);
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();

  private Task dep;
  private Task dep2;
  private TaskWorker worker;
  private Task task;
  private Task task2;
  private ConstantWorker worker2;

  @Test
  public void hashes_of_tasks_with_same_worker_and_dependencies_are_equal() throws Exception {
    given(worker = new ConstantWorker(STRING, objectsDb.string("work"), CL));
    given(dep = mock(Task.class));
    given(willReturn(new TaskOutput(objectsDb.string("abc"))), dep).output();
    given(task = new Task(worker, ImmutableList.<Task> of(dep)));
    given(task2 = new Task(worker, ImmutableList.<Task> of(dep)));
    when(task).hash();
    thenReturned(task2.hash());
  }

  @Test
  public void hashes_of_tasks_with_same_worker_and_different_dependencies_are_not_equal()
      throws Exception {
    given(worker = new ConstantWorker(STRING, objectsDb.string("work"), CL));
    given(dep = mock(Task.class));
    given(willReturn(new TaskOutput(objectsDb.string("abc"))), dep).output();
    given(dep2 = mock(Task.class));
    given(willReturn(new TaskOutput(objectsDb.string("def"))), dep2).output();
    given(task = new Task(worker, ImmutableList.<Task> of(dep)));
    given(task2 = new Task(worker, ImmutableList.<Task> of(dep2)));
    when(task).hash();
    thenReturned(not(task2.hash()));
  }

  @Test
  public void hashes_of_tasks_with_different_worker_and_same_dependencies_are_not_equal()
      throws Exception {
    given(worker = new ConstantWorker(STRING, objectsDb.string("work"), CL));
    given(worker2 = new ConstantWorker(STRING, objectsDb.string("work2"), CL));
    given(dep = mock(Task.class));
    given(willReturn(new TaskOutput(objectsDb.string("abc"))), dep).output();
    given(task = new Task(worker, ImmutableList.<Task> of(dep)));
    given(task2 = new Task(worker2, ImmutableList.<Task> of(dep)));
    when(task).hash();
    thenReturned(not(task2.hash()));
  }
}
