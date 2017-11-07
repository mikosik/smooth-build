package org.smoothbuild.task.exec;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Evaluator;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class TaskBatch {
  private final TaskExecutor taskExecutor;
  private final ValuesDb valuesDb;
  private final List<Task> rootTasks;

  @Inject
  public TaskBatch(TaskExecutor taskExecutor, ValuesDb valuesDb) {
    this.taskExecutor = taskExecutor;
    this.valuesDb = valuesDb;
    this.rootTasks = new ArrayList<>();
  }

  public <T extends Value> Task createTasks(Expression expression) {
    Task root = createTasksImpl(expression.createEvaluator(valuesDb, null));
    rootTasks.add(root);
    return root;
  }

  private <T extends Value> Task createTasksImpl(Evaluator evaluator) {
    ImmutableList<Task> dependencies = createTasksImpl(evaluator.dependencies());
    return new Task(evaluator, dependencies);
  }

  private ImmutableList<Task> createTasksImpl(ImmutableList<? extends Evaluator> evaluators) {
    Builder<Task> builder = ImmutableList.builder();
    for (Evaluator evaluator : evaluators) {
      Task executor = createTasksImpl(evaluator);
      builder.add(executor);
    }
    return builder.build();
  }

  public void executeAll() {
    for (Task task : rootTasks) {
      executeGraph(task);
      if (task.graphContainsErrors()) {
        return;
      }
    }
  }

  private void executeGraph(Task task) {
    for (Task subTask : task.dependencies()) {
      executeGraph(subTask);
      if (subTask.graphContainsErrors()) {
        return;
      }
    }
    taskExecutor.execute(task);
  }

  public boolean containsErrors() {
    return rootTasks
        .stream()
        .anyMatch(Task::graphContainsErrors);
  }
}
