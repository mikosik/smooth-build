package org.smoothbuild.common.function;

import java.util.function.Function;

public class Functions {
  /**
   * Invokes {@link Function} that takes other {@link Function} as parameter by passing argument of
   * type {@link Function1} wrapped inside sneaky function that hides checked exception that can be
   * thrown by {@link Function1} so it conforms to {@link Function} interface.
   * <p>
   * This method has advantage over using sneaky function directly as it declares checked exception,
   * hidden by {@link #sneakyFunction(Function1)}, explicitly in its `throws` clause. This way any
   * change of exception type thrown by given instance of {@link Function1} will cause change of
   * exception type thrown by given call to this method and will make compiler enforce handling
   * of that change exception.
   * </p>
   */
  public static <S, R1, T extends Throwable, R2> R2 invokeWithTunneling(
      Function<Function<S, R1>, R2> function, Function1<S, R1, T> function1) throws T {
    return function.apply(sneakyFunction(function1));
  }

  /**
   * Wraps Function0 (that can throw checked exception) into implementation of java.util.Function
   * that looks like it doesn't throw checked exception. In fact exception thrown by function0
   * are propagated outside returned wrapper.
   * <p>
   * Most probably you should use {@link Functions#invokeWithTunneling(Function, Function1)} which
   * explicitly declares checked exception that can be thrown but is normally invisible to compiler
   * when you use sneakyFunction directly.
   * </p>
   */
  public static <A, R, T extends Throwable> Function<A, R> sneakyFunction(
      Function1<A, R, T> function0) {
    return new SneakyJavaFunction<>(function0);
  }

  private static class SneakyJavaFunction<A, R, T extends Throwable> implements Function<A, R> {
    private final Function1<A, R, T> wrapped;

    public SneakyJavaFunction(Function1<A, R, T> wrapped) {
      this.wrapped = wrapped;
    }

    @Override
    public R apply(A argument) {
      return invokeWithSneakyExceptionHiding(argument);
    }

    @SuppressWarnings("unchecked")
    private <E extends Throwable> R invokeWithSneakyExceptionHiding(A argument) throws E {
      try {
        return wrapped.apply(argument);
      } catch (Throwable e) {
        throw (E) e;
      }
    }
  }
}
