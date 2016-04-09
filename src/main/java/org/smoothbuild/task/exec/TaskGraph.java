package org.smoothbuild.task.exec;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.values.ValuesDb;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.value.Value;
import org.smoothbuild.task.base.Task;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

public class TaskGraph {
  private final TaskExecutor taskExecutor;
  private final ValuesDb valuesDb;
  private final List<Task> rootTasks;

  @Inject
  public TaskGraph(TaskExecutor taskExecutor, ValuesDb valuesDb) {
    this.taskExecutor = taskExecutor;
    this.valuesDb = valuesDb;
    this.rootTasks = new ArrayList<>();
  }

  public <T extends Value> Task createTasks(Expression expression) {
    Task root = createTasksImpl(expression);
    rootTasks.add(root);
    return root;
  }

  private <T extends Value> Task createTasksImpl(Expression expression) {
    ImmutableList<Task> dependencies = createTasksImpl(expression.dependencies());
    return new Task(expression.createComputer(valuesDb), dependencies);
  }

  private ImmutableList<Task> createTasksImpl(ImmutableList<? extends Expression> expressions) {
    Builder<Task> builder = ImmutableList.builder();
    for (Expression expression : expressions) {
      Task executor = createTasksImpl(expression);
      builder.add(executor);
    }
    return builder.build();
  }

  public void executeAll() {
    for (Task task : rootTasks) {
      executeGraph(task);
    }
  }

  private void executeGraph(Task task) {
    for (Task subTask : task.dependencies()) {
      executeGraph(subTask);
    }
    taskExecutor.execute(task);
  }
}
