package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class Task {
  private final Evaluator evaluator;
  private final ImmutableList<Task> dependencies;
  private Output output;

  public Task(Evaluator evaluator, ImmutableList<Task> dependencies) {
    this.evaluator = evaluator;
    this.dependencies = dependencies;
    this.output = null;
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

  public Type resultType() {
    return evaluator.resultType();
  }

  public boolean isInternal() {
    return evaluator.isInternal();
  }

  public boolean isCacheable() {
    return evaluator.isCacheable();
  }

  public Location location() {
    return evaluator.location();
  }

  public void execute(ContainerImpl container) {
    output = evaluator.evaluate(input(), container);
  }

  public Output output() {
    checkState(output != null);
    return output;
  }

  public void setOutput(Output output) {
    this.output = output;
  }

  public boolean hasOutput() {
    return output != null;
  }

  public HashCode hash() {
    Hasher hasher = Hash.newHasher();
    hasher.putBytes(evaluator.hash().asBytes());
    hasher.putBytes(input().hash().asBytes());
    return hasher.hash();
  }

  private Input input() {
    return Input.fromResults(dependencies);
  }
}
