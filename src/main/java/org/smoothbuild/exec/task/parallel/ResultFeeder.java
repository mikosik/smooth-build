package org.smoothbuild.exec.task.parallel;

import java.util.function.Consumer;

import org.smoothbuild.exec.comp.MaybeOutput;
import org.smoothbuild.exec.comp.Output;
import org.smoothbuild.lang.object.base.SObject;
import org.smoothbuild.util.concurrent.Feeder;

/**
 * This class is thread-safe.
 * Consumers registered with {@link #addValueConsumer(Consumer)} and
 * {@link #addValueAvailableListener(Runnable)} are called without any lock held.
 */
public class ResultFeeder {
  private final Feeder<Output> feeder;

  public ResultFeeder() {
    this.feeder = new Feeder<>();
  }

  public Output output() {
    return feeder.value();
  }

  public void addValueAvailableListener(Runnable runnable) {
    addValueConsumer((value) -> runnable.run());
  }

  public void addValueConsumer(Consumer<SObject> valueConsumer) {
    feeder.addConsumer((Output output) -> valueConsumer.accept(output.value()));
  }

  public void addOutputConsumer(Consumer<Output> outputConsumer) {
    feeder.addConsumer(outputConsumer);
  }

  public void setResult(MaybeOutput maybeOutput) {
    if (maybeOutput.hasOutputWithValue()) {
      feeder.accept(maybeOutput.output());
    }
  }
}
