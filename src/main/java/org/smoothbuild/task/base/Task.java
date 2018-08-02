package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkState;
import static org.smoothbuild.db.hashed.Hash.newHasher;
import static org.smoothbuild.lang.message.Messages.containsErrors;

import org.smoothbuild.lang.base.Location;
import org.smoothbuild.lang.type.ConcreteType;
import org.smoothbuild.task.exec.Container;

import com.google.common.hash.HashCode;

public class Task {
  private final Evaluator evaluator;
  private Output output;
  private final HashCode hash;

  public Task(Evaluator evaluator, HashCode runtimeHash) {
    this.evaluator = evaluator;
    this.output = null;
    this.hash = newHasher()
        .putBytes(evaluator.hash().asBytes())
        .putBytes(runtimeHash.asBytes())
        .hash();
  }

  public Evaluator evaluator() {
    return evaluator;
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

  public boolean isCacheable() {
    return evaluator.isCacheable() && output.isCacheable();
  }

  public Location location() {
    return evaluator.location();
  }

  public void execute(Container container, Input input) {
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
    return hash;
  }
}
