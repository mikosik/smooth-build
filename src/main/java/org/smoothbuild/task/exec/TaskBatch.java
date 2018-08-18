package org.smoothbuild.task.exec;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.RuntimeHash;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.task.base.Input;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.util.Dag;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.hash.HashCode;

public class TaskBatch {
  private final TaskExecutor taskExecutor;
  private final ValuesDb valuesDb;
  private final HashCode runtimeHash;
  private final List<Dag<Task>> rootTasks;

  @Inject
  public TaskBatch(TaskExecutor taskExecutor, ValuesDb valuesDb,
      @RuntimeHash HashCode runtimeHash) {
    this.taskExecutor = taskExecutor;
    this.valuesDb = valuesDb;
    this.runtimeHash = runtimeHash;
    this.rootTasks = new ArrayList<>();
  }

  public <T extends Value> Dag<Task> createTasks(Expression expression) {
    Dag<Task> root = createTasksImpl(expression.createEvaluator(valuesDb, null));
    rootTasks.add(root);
    return root;
  }

  private <T extends Value> Dag<Task> createTasksImpl(Evaluator evaluator) {
    List<Dag<Task>> children = createTasksImpl(evaluator.children());
    return new Dag<Task>(new Task(evaluator, runtimeHash), children);
  }

  private List<Dag<Task>> createTasksImpl(List<Evaluator> evaluators) {
    Builder<Dag<Task>> builder = ImmutableList.builder();
    for (Evaluator evaluator : evaluators) {
      Dag<Task> executor = createTasksImpl(evaluator);
      builder.add(executor);
    }
    return builder.build();
  }

  public void executeAll() {
    for (Dag<Task> task : rootTasks) {
      executeGraph(task);
      if (task.elem().graphContainsErrors()) {
        return;
      }
    }
  }

  private void executeGraph(Dag<Task> task) {
    for (Dag<Task> subTask : task.children()) {
      executeGraph(subTask);
      if (subTask.elem().graphContainsErrors()) {
        return;
      }
    }
    taskExecutor.execute(task.elem(), Input.fromResults(task.children()));
  }

  public boolean containsErrors() {
    return rootTasks
        .stream()
        .map(Dag::elem)
        .anyMatch(Task::graphContainsErrors);
  }
}
