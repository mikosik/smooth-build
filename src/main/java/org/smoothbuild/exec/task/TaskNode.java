package org.smoothbuild.exec.task;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.exec.comp.Input.input;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;
import java.util.List;

import org.smoothbuild.db.outputs.OutputDbException;
import org.smoothbuild.exec.comp.Input;
import org.smoothbuild.lang.object.base.Bool;
import org.smoothbuild.lang.object.base.SObject;

import com.google.common.collect.ImmutableList;

public abstract class TaskNode {
  protected final Task task;
  protected final ImmutableList<TaskNode> children;
  protected TaskResult result;

  public TaskNode(Task task, ImmutableList<TaskNode> children) {
    this.task = task;
    this.children = children;
  }

  public boolean hasSuccessfulResult() {
    return result != null && result.hasOutputWithValue();
  }

  public TaskResult result() {
    return result;
  }

  public String name() {
    return task.name();
  }

  public abstract void execute(TaskExecutor taskExecutor) throws IOException,
      OutputDbException;

  public static class NormalNode extends TaskNode {
    public NormalNode(Task task, ImmutableList<TaskNode> children) {
      super(task, children);
    }

    @Override
    public void execute(TaskExecutor taskExecutor) throws IOException,
        OutputDbException {
      for (TaskNode subNode : children) {
        subNode.execute(taskExecutor);
        if (!subNode.hasSuccessfulResult()) {
          return;
        }
      }
      result = taskExecutor.execute(task, resultValuesToInput(children));
    }
  }

  public static class IfNode extends TaskNode {
    public IfNode(Task task, ImmutableList<TaskNode> children) {
      super(task, children);
    }

    @Override
    public void execute(TaskExecutor taskExecutor) throws IOException, OutputDbException {
      TaskNode conditionTask = children.get(0);
      conditionTask.execute(taskExecutor);
      if (!conditionTask.hasSuccessfulResult()) {
        return;
      }
      TaskNode thenTask = children.get(1);
      TaskNode elseTask = children.get(2);
      if (((Bool) conditionTask.result().output().value()).jValue()) {
        executeIfTask(conditionTask, thenTask, taskExecutor);
      } else {
        executeIfTask(conditionTask, elseTask, taskExecutor);
      }
    }

    private void executeIfTask(TaskNode conditionNode, TaskNode dependencyNode,
        TaskExecutor taskExecutor) throws IOException, OutputDbException {
      dependencyNode.execute(taskExecutor);
      if (!dependencyNode.hasSuccessfulResult()) {
        return;
      }
      // Only one of then/else values will be used and it will be used twice.
      // This way TaskExecutor can calculate task hash and use it for caching.
      Input input = resultValuesToInput(list(conditionNode, dependencyNode, dependencyNode));
      result = taskExecutor.execute(task, input);
    }
  }

  private static Input resultValuesToInput(List<TaskNode> children) {
    List<SObject> childValues = children
        .stream()
        .map(taskNode -> taskNode.result().output().value())
        .collect(toImmutableList());
    return input(childValues);
  }
}
