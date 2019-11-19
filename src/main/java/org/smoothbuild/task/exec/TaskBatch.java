package org.smoothbuild.task.exec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.outputs.OutputsDbException;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.task.base.Input;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableList;

public class TaskBatch {
  private final TaskExecutor taskExecutor;
  private final ObjectsDb objectsDb;
  private final List<Task> rootTasks;

  @Inject
  public TaskBatch(TaskExecutor taskExecutor, ObjectsDb objectsDb) {
    this.taskExecutor = taskExecutor;
    this.objectsDb = objectsDb;
    this.rootTasks = new ArrayList<>();
  }

  public Task createTasks(Expression expression) {
    Task root = expression.createTask(objectsDb, null);
    rootTasks.add(root);
    return root;
  }

  public void executeAll() throws IOException, OutputsDbException {
    for (Task task : rootTasks) {
      executeGraph(task);
      if (!task.hasSuccessfulResult()) {
        return;
      }
    }
  }

  private void executeGraph(Task task) throws IOException, OutputsDbException {
    if (task.name().equals("if")) {
      executeIfGraph(task);
    } else {
      executeNormalGraph(task);
    }
  }

  private void executeIfGraph(Task task) throws IOException, OutputsDbException {
    ImmutableList<Task> dependencies = task.dependencies();
    Task conditionTask = dependencies.get(0);
    executeGraph(conditionTask);
    if (!conditionTask.hasSuccessfulResult()) {
      return;
    }
    Task thenTask = dependencies.get(1);
    Task elseTask = dependencies.get(2);
    if (((Bool) conditionTask.output().result()).jValue()) {
      executeIfTask(task, conditionTask, thenTask);
    } else {
      executeIfTask(task, conditionTask, elseTask);
    }
  }

  private void executeIfTask(Task task, Task conditionTask, Task dependencyTask) throws
      IOException, OutputsDbException {
    executeGraph(dependencyTask);
    if (!dependencyTask.hasSuccessfulResult()) {
      return;
    }
    // Only one of then/else values will be used and it will be used twice.
    // This way TaskExecutor can calculate task hash and use it for caching.
    List<Task> dependencies = ImmutableList.of(conditionTask, dependencyTask, dependencyTask);
    taskExecutor.execute(task, Input.fromResults(dependencies));
  }

  private void executeNormalGraph(Task task) throws IOException, OutputsDbException {
    for (Task subTask : task.dependencies()) {
      executeGraph(subTask);
      if (!subTask.hasSuccessfulResult()) {
        return;
      }
    }
    taskExecutor.execute(task, Input.fromResults(task.dependencies()));
  }

  public boolean containsErrors() {
    return !rootTasks
        .stream()
        .allMatch(Task::hasSuccessfulResult);
  }
}
