package org.smoothbuild.function.plugin;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Method;

import org.junit.Test;

public class ReflexiveInvokerTest {

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

    Object result = ReflexiveInvoker.invokeMethod(klass, method);

    assertThat(result).isEqualTo("string value");
  }

  @Test
  public void invokeMethodWithArg() throws Exception {
    Class<?> klass = MyClassWithChainMethod.class;
    Method method = klass.getMethod("chain", Object.class);
    String string = "string";

    Object result = ReflexiveInvoker.invokeMethod(klass, method, string);

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
