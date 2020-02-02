package org.smoothbuild.exec.task;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static java.util.Collections.unmodifiableList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.db.outputs.OutputDbException;
import org.smoothbuild.exec.task.TaskNode.IfNode;
import org.smoothbuild.exec.task.TaskNode.NormalNode;
import org.smoothbuild.parse.expr.Expression;

import com.google.common.collect.ImmutableList;

public class TaskBatch {
  private final TaskExecutor taskExecutor;
  private final List<TaskNode> rootTasks;

  @Inject
  public TaskBatch(TaskExecutor taskExecutor) {
    this.taskExecutor = taskExecutor;
    this.rootTasks = new ArrayList<>();
  }

  public List<TaskNode> rootTasks() {
    return unmodifiableList(rootTasks);
  }

  public void createTasks(Expression expression) {
    rootTasks.add(toTaskNode(expression.createTask(null)));
  }

  private static TaskNode toTaskNode(Task task) {
    if (task.name().equals("if")) {
      return new IfNode(task, toTaskNodes(task.children()));
    } else {
      return new NormalNode(task, toTaskNodes(task.children()));
    }
  }

  private static ImmutableList<TaskNode> toTaskNodes(ImmutableList<Task> dependencies) {
    return dependencies.stream()
        .map(TaskBatch::toTaskNode)
        .collect(toImmutableList());
  }

  public void executeAll() throws IOException, OutputDbException {
    for (TaskNode node : rootTasks) {
      node.execute(taskExecutor);
      if (!(node.hasSuccessfulResult())) {
        return;
      }
    }
  }

  public boolean containsErrors() {
    return !rootTasks
        .stream()
        .allMatch(TaskNode::hasSuccessfulResult);
  }
}
