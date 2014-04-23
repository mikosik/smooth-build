package org.smoothbuild.task.exec;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.expr.Expr;

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

  public <T extends SValue> Task<T> createTasks(Expr<T> expr) {
    Task<T> root = createTasksImpl(expr);
    rootTasks.add(root);
    return root;
  }

  public <T extends SValue> Task<T> createTasksImpl(Expr<T> expr) {
    ImmutableList<Task<?>> dependencies = createTasksImpl(expr.dependencies());
    return new Task<>(expr.createWorker(), dependencies);
  }

  private ImmutableList<Task<?>> createTasksImpl(ImmutableList<? extends Expr<?>> exprs) {
    Builder<Task<?>> builder = ImmutableList.builder();
    for (Expr<?> expr : exprs) {
      Task<?> executor = createTasksImpl(expr);
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
