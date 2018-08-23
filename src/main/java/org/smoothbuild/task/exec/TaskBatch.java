package org.smoothbuild.task.exec;

import java.io.IOException;
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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.hash.HashCode;

public class TaskBatch {
  private final TaskExecutor taskExecutor;
  private final ValuesDb valuesDb;
  private final HashCode runtimeHash;
  private final List<Task> rootTasks;

  @Inject
  public TaskBatch(TaskExecutor taskExecutor, ValuesDb valuesDb,
      @RuntimeHash HashCode runtimeHash) {
    this.taskExecutor = taskExecutor;
    this.valuesDb = valuesDb;
    this.runtimeHash = runtimeHash;
    this.rootTasks = new ArrayList<>();
  }

  public <T extends Value> Task createTasks(Expression expression) {
    Task root = createTasksImpl(expression.createEvaluator(valuesDb, null));
    rootTasks.add(root);
    return root;
  }

  private <T extends Value> Task createTasksImpl(Evaluator evaluator) {
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
      if (task.graphContainsErrors()) {
        return;
      }
    }
  }

  private void executeGraph(Task task) throws IOException {
    for (Task subTask : task.dependencies()) {
      executeGraph(subTask);
      if (subTask.graphContainsErrors()) {
        return;
      }
    }
    taskExecutor.execute(task, Input.fromResults(task.dependencies()));
  }

  public boolean containsErrors() {
    return rootTasks
        .stream()
        .anyMatch(Task::graphContainsErrors);
  }
}
