package org.smoothbuild.function;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.junit.Assert.fail;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import org.junit.Test;
import org.smoothbuild.function.exc.CreatingInstanceFailedException;

public class ReflexiveInvokerTest {
  ReflexiveInvoker reflexiveInvoker = new ReflexiveInvoker();

  @Test
  public void invokeConstructor() throws Exception {
    Object object = reflexiveInvoker.invokeConstructor(MyClassWithNoArgConstructor.class
        .getConstructor());
    assertThat(object).isInstanceOf(MyClassWithNoArgConstructor.class);
  }

  @Test
  public void invokeConstructorWithParam() throws Exception {
    String value = "string value";
    Constructor<?> constructor = MyClassWithOneArgConstructor.class.getConstructor(String.class);

    Object object = reflexiveInvoker.invokeConstructor(constructor, value);

    assertThat(((MyClassWithOneArgConstructor) object).param).isEqualTo(value);
  }

  @Test
  public void invokingNoArgConstructorWithArgumentThrowsException() throws Exception {
    try {
      reflexiveInvoker.invokeConstructor(MyClassWithNoArgConstructor.class.getConstructor(),
          "string");
      fail("exception should be thrown");
    } catch (CreatingInstanceFailedException e) {
      // expected
      assertThat((Object) e.classThatFailed()).isEqualTo(MyClassWithNoArgConstructor.class);
    }
  }

  public static class MyClassWithNoArgConstructor {
    public MyClassWithNoArgConstructor() {}
  }

  public static class MyClassWithOneArgConstructor {
    public final String param;

    public MyClassWithOneArgConstructor(String param) {
      this.param = param;
    }
  }

  @Test
  public void invokeMethod() throws Exception {
    Class<?> klass = MyClassWithStringMethod.class;
    Method method = klass.getMethod("string");

    Object result = reflexiveInvoker.invokeMethod(klass, method);

    assertThat(result).isEqualTo("string value");
  }

  @Test
  public void invokeMethodWithArg() throws Exception {
    Class<?> klass = MyClassWithChainMethod.class;
    Method method = klass.getMethod("chain", Object.class);
    String string = "string";

    Object result = reflexiveInvoker.invokeMethod(klass, method, string);

    assertThat(result).isEqualTo(string);

  }

  public static class MyClassWithStringMethod {
    public static String string() {
      return "string value";
    }
  }

  public static class MyClassWithChainMethod {
    public static Object chain(Object value) {
      return value;
    }
  }
}
