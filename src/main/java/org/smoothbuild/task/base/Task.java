package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.lang.message.Messages.containsErrors;

import org.smoothbuild.lang.message.Location;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.task.exec.ContainerImpl;

import com.google.common.hash.HashCode;

public class Task {
  private final Evaluator evaluator;
  private Output output;

  public Task(Evaluator evaluator) {
    this.evaluator = evaluator;
    this.output = null;
  }

  public Evaluator evaluator() {
    return evaluator;
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

  public void execute(ContainerImpl container, Input input) {
    output = evaluator.evaluate(input, container);
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

  public boolean graphContainsErrors() {
    return !hasOutput() || containsErrors(output.messages());
  }

  public HashCode hash() {
    return evaluator.hash();
  }
}
