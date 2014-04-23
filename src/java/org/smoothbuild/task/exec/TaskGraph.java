package org.smoothbuild.task.exec;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.lang.function.def.Node;

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

  public <T extends SValue> Task<T> createTasks(Node<T> node) {
    Task<T> root = createTasksImpl(node);
    rootTasks.add(root);
    return root;
  }

  public <T extends SValue> Task<T> createTasksImpl(Node<T> node) {
    ImmutableList<Task<?>> dependencies = createTasksImpl(node.dependencies());
    return new Task<>(node.createWorker(), dependencies);
  }

  private ImmutableList<Task<?>> createTasksImpl(ImmutableList<? extends Node<?>> nodes) {
    Builder<Task<?>> builder = ImmutableList.builder();
    for (Node<?> node : nodes) {
      Task<?> executor = createTasksImpl(node);
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
