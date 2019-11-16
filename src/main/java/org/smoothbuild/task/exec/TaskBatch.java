package org.smoothbuild.task.exec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.lang.object.db.ObjectsDb;
import org.smoothbuild.task.RuntimeHash;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.task.base.Input;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class TaskBatch {
  private final TaskExecutor taskExecutor;
  private final ObjectsDb objectsDb;
  private final Hash runtimeHash;
  private final List<Task> rootTasks;

  @Inject
  public TaskBatch(TaskExecutor taskExecutor, ObjectsDb objectsDb,
      @RuntimeHash Hash runtimeHash) {
    this.taskExecutor = taskExecutor;
    this.objectsDb = objectsDb;
    this.runtimeHash = runtimeHash;
    this.rootTasks = new ArrayList<>();
  }

  public <T extends SObject> Task createTasks(Expression expression) {
    Task root = createTasksImpl(expression.createEvaluator(objectsDb, null));
    rootTasks.add(root);
    return root;
  }

  private <T extends SObject> Task createTasksImpl(Evaluator evaluator) {
    List<Task> children = createTasksImpl(evaluator.children());
    return new Task(evaluator, children, runtimeHash);
  }

  private List<Task> createTasksImpl(List<Evaluator> evaluators) {
    Builder<Task> builder = ImmutableList.builder();
    for (Evaluator evaluator : evaluators) {
      builder.add(createTasksImpl(evaluator));
    }
    return builder.build();
  }

  public void executeAll() throws IOException {
    for (Task task : rootTasks) {
      executeGraph(task);
      if (!task.hasSuccessfulResult()) {
        return;
      }
    }
  }

  private void executeGraph(Task task) throws IOException {
    if (task.name().equals("if")) {
      executeIfGraph(task);
    } else {
      executeNormalGraph(task);
    }
  }

  private void executeIfGraph(Task task) throws IOException {
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
      IOException {
    executeGraph(dependencyTask);
    if (!dependencyTask.hasSuccessfulResult()) {
      return;
    }
    // Only one of then/else values will be used and it will be used twice.
    // This way TaskExecutor can calculate task hash and use it for caching.
    List<Task> dependencies = ImmutableList.of(conditionTask, dependencyTask, dependencyTask);
    taskExecutor.execute(task, Input.fromResults(dependencies));
  }

  private void executeNormalGraph(Task task) throws IOException {
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
