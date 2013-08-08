package org.smoothbuild.registry.instantiate;

import static org.fest.assertions.api.Assertions.assertThat;

import java.lang.reflect.Constructor;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.lang.function.Function;
import org.smoothbuild.lang.function.Params;
import org.smoothbuild.lang.function.exc.FunctionException;
import org.smoothbuild.registry.exc.CreatingInstanceFailedException;

public class ConstructorInvokerTest {
  ConstructorInvoker constructorInvoker = new ConstructorInvoker();

  @Test
  public void invoke() throws NoSuchMethodException, SecurityException,
      CreatingInstanceFailedException {
    String string = "abc";
    Function result = constructorInvoker.invoke(getConstructor(), string);
    assertThat(((MyFunction) result).value).isEqualTo(string);
  }

  @Test
  public void creatingInstanceFailedExceptionIsThrownForIncorrectType()
      throws NoSuchMethodException, SecurityException {
    try {
      constructorInvoker.invoke(getConstructor(), Integer.valueOf(33));
      Assert.fail("exception should be thrown");
    } catch (CreatingInstanceFailedException e) {
      // expected
    }
  }

  private Constructor<MyFunction> getConstructor() throws NoSuchMethodException, SecurityException {
    return MyFunction.class.getConstructor(String.class);
  }

  public static class MyFunction implements Function {
    public final String value;

    public MyFunction(String value) {
      this.value = value;
    }

    @Override
    public Params params() {
      return null;
    }

    @Override
    public Object execute() throws FunctionException {
      return null;
    }
  }
}
