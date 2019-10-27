package org.smoothbuild.task.base;

import java.util.List;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.task.exec.Container;

import com.google.common.collect.ImmutableList;

public class Task {
  private final Evaluator evaluator;
  private final ImmutableList<Task> dependencies;
  private final Hash runtimeHash;
  private TaskResult result;

  public Task(Evaluator evaluator, List<? extends Task> dependencies, Hash runtimeHash) {
    this.evaluator = evaluator;
    this.dependencies = ImmutableList.copyOf(dependencies);
    this.runtimeHash = runtimeHash;
    this.result = null;
  }

  public Evaluator evaluator() {
    return evaluator;
  }

  public ImmutableList<Task> dependencies() {
    return dependencies;
  }

  public String name() {
    return evaluator.name();
  }

  public ConcreteType type() {
    return evaluator.type();
  }

  public boolean isInternal() {
    return evaluator.isInternal();
  }

  public Location location() {
    return evaluator.location();
  }

  public void execute(Container container, Input input) {
    try {
      result = new TaskResult(evaluator.evaluate(input, container), false);
    } catch (ComputationException e) {
      result = new TaskResult(e);
    }
  }

  public Output output() {
    return result.output();
  }

  public boolean shouldCacheOutput() {
    return evaluator.isCacheable() && result.hasOutput();
  }

  public Hash hash(Input input) {
    return Hash.of(
        runtimeHash,
        evaluator.hash(),
        input.hash());
  }

  public void setResult(TaskResult result) {
    this.result = result;
  }

  public TaskResult result() {
    return result;
  }

  public boolean hasSuccessfulResult() {
    return result != null && result.hasOutputWithValue();
  }
}
