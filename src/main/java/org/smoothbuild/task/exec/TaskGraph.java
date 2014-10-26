package org.smoothbuild.task.exec;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.lang.base.Value;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.Lists;

public class TaskGraph {
  private final TaskExecutor taskExecutor;
  private final List<Task<?>> rootTasks;

  @Inject
  public TaskGraph(TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
    this.rootTasks = Lists.newArrayList();
  }

  public <T extends Value> Task<T> createTasks(Expression<T> expression) {
    Task<T> root = createTasksImpl(expression);
    rootTasks.add(root);
    return root;
  }

  private <T extends Value> Task<T> createTasksImpl(Expression<T> expression) {
    ImmutableList<Task<?>> dependencies = createTasksImpl(expression.dependencies());
    return new Task<>(expression.createWorker(), dependencies);
  }

  private ImmutableList<Task<?>> createTasksImpl(ImmutableList<? extends Expression<?>> exprs) {
    Builder<Task<?>> builder = ImmutableList.builder();
    for (Expression<?> expression : exprs) {
      Task<?> executor = createTasksImpl(expression);
      builder.add(executor);
    }
    return builder.build();
  }

  public void executeAll() {
    for (Task<?> task : rootTasks) {
      executeGraph(task);
    }
  }

  private void executeGraph(Task<?> task) {
    for (Task<?> subTask : task.dependencies()) {
      executeGraph(subTask);
    }
    taskExecutor.execute(task);
  }
}
