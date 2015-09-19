package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkState;

import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.compute.Computer;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.collect.ImmutableList;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hasher;

public class Task {
  private final Computer computer;
  private final ImmutableList<Task> dependencies;
  private TaskOutput output;

  public Task(Computer computer, ImmutableList<Task> dependencies) {
    this.computer = computer;
    this.dependencies = dependencies;
    this.output = null;
  }

  public Computer computer() {
    return computer;
  }

  public ImmutableList<Task> dependencies() {
    return dependencies;
  }

  public String name() {
    return computer.name();
  }

  public Type resultType() {
    return computer.resultType();
  }

  public boolean isInternal() {
    return computer.isInternal();
  }

  public boolean isCacheable() {
    return computer.isCacheable();
  }

  public CodeLocation codeLocation() {
    return computer.codeLocation();
  }

  public void execute(ContainerImpl container) {
    output = computer.execute(input(), container);
  }

  public TaskOutput output() {
    checkState(output != null);
    return output;
  }

  public void setOutput(TaskOutput output) {
    this.output = output;
  }

  public boolean hasOutput() {
    return output != null;
  }

  public HashCode hash() {
    Hasher hasher = Hash.newHasher();
    hasher.putBytes(computer.hash().asBytes());
    hasher.putBytes(input().hash().asBytes());
    return hasher.hash();
  }

  private TaskInput input() {
    return TaskInput.fromResults(dependencies);
  }
}
