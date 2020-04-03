package org.smoothbuild.exec.task.parallel;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.util.function.Consumer;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.exec.comp.MaybeOutput;
import org.smoothbuild.exec.comp.Output;
import org.smoothbuild.lang.object.base.Array;
import org.smoothbuild.lang.object.base.SObject;

@SuppressWarnings("ClassCanBeStatic")
public class ResultFeederTest {
  @Nested
  class initially {
    @Test
    public void result_returns_null() {
      ResultFeeder resultFeeder = new ResultFeeder();
      assertThat(resultFeeder.output())
          .isNull();
    }
  }

  @Nested
  class when_output_with_value_is_set {
    @Test
    public void result_returns_that_output() {
      ResultFeeder resultFeeder = new ResultFeeder();
      Output output = output(mock(SObject.class));
      MaybeOutput result = new MaybeOutput(output);
      resultFeeder.setResult(result);

      assertThat(resultFeeder.output())
          .isSameInstanceAs(output);
    }

    @Test
    public void value_available_listener_is_called() {
      ResultFeeder resultFeeder = new ResultFeeder();
      Runnable runnable = mock(Runnable.class);
      resultFeeder.addValueAvailableListener(runnable);

      resultFeeder.setResult(result(mock(SObject.class)));

      verify(runnable, only()).run();
    }

    @Test
    public void value_consumer_is_called() {
      ResultFeeder resultFeeder = new ResultFeeder();
      SObject sObject = mock(SObject.class);
      @SuppressWarnings("unchecked")
      Consumer<SObject> consumer = mock(Consumer.class);
      resultFeeder.addValueConsumer(consumer);

      resultFeeder.setResult(result(sObject));

      verify(consumer, only()).accept(same(sObject));
    }

    @Test
    public void output_consumer_is_called() {
      ResultFeeder resultFeeder = new ResultFeeder();
      SObject sObject = mock(SObject.class);
      @SuppressWarnings("unchecked")
      Consumer<Output> consumer = mock(Consumer.class);
      resultFeeder.addOutputConsumer(consumer);

      MaybeOutput result = result(sObject);
      resultFeeder.setResult(result);

      verify(consumer, only()).accept(eq(result.output()));
    }
  }

  @Nested
  class when_output_without_value_is_set {
    @Test
    public void result_returns_null() {
      ResultFeeder resultFeeder = new ResultFeeder();
      resultFeeder.setResult(new MaybeOutput(output(null)));

      assertThat(resultFeeder.output())
          .isNull();
    }

    @Test
    public void value_available_listener_is_not_called() {
      ResultFeeder resultFeeder = new ResultFeeder();
      Runnable runnable = mock(Runnable.class);
      resultFeeder.addValueAvailableListener(runnable);

      resultFeeder.setResult(new MaybeOutput(output(null)));

      verifyNoInteractions(runnable);
    }

    @Test
    public void value_consumer_is_not_called() {
      ResultFeeder resultFeeder = new ResultFeeder();
      @SuppressWarnings("unchecked")
      Consumer<SObject> consumer = mock(Consumer.class);
      resultFeeder.addValueConsumer(consumer);

      resultFeeder.setResult(new MaybeOutput(output(null)));

      verifyNoInteractions(consumer);
    }

    @Test
    public void output_consumer_is_not_called() {
      ResultFeeder resultFeeder = new ResultFeeder();
      @SuppressWarnings("unchecked")
      Consumer<Output> consumer = mock(Consumer.class);
      resultFeeder.addOutputConsumer(consumer);

      resultFeeder.setResult(new MaybeOutput(new IllegalArgumentException()));

      verifyNoInteractions(consumer);
    }
  }

  @Nested
  class when_maybe_output_with_exception_is_set {
    @Test
    public void result_returns_null() {
      ResultFeeder resultFeeder = new ResultFeeder();
      resultFeeder.setResult(new MaybeOutput(new IllegalArgumentException()));

      assertThat(resultFeeder.output())
          .isNull();
    }

    @Test
    public void value_available_listener_is_not_called() {
      ResultFeeder resultFeeder = new ResultFeeder();
      Runnable runnable = mock(Runnable.class);
      resultFeeder.addValueAvailableListener(runnable);

      resultFeeder.setResult(new MaybeOutput(new IllegalArgumentException()));

      verifyNoInteractions(runnable);
    }

    @Test
    public void value_consumer_is_not_called() {
      ResultFeeder resultFeeder = new ResultFeeder();
      @SuppressWarnings("unchecked")
      Consumer<SObject> consumer = mock(Consumer.class);
      resultFeeder.addValueConsumer(consumer);

      resultFeeder.setResult(new MaybeOutput(new IllegalArgumentException()));

      verifyNoInteractions(consumer);
    }

    @Test
    public void output_consumer_is_not_called() {
      ResultFeeder resultFeeder = new ResultFeeder();
      @SuppressWarnings("unchecked")
      Consumer<Output> consumer = mock(Consumer.class);
      resultFeeder.addOutputConsumer(consumer);

      resultFeeder.setResult(new MaybeOutput(new IllegalArgumentException()));

      verifyNoInteractions(consumer);
    }
  }

  private static MaybeOutput result(SObject sObject) {
    return new MaybeOutput(output(sObject));
  }

  private static Output output(SObject sObject) {
    return new Output(sObject, mock(Array.class));
  }
}
