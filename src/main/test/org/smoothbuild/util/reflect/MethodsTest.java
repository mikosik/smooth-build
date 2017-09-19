package org.smoothbuild.util.reflect;

import static org.smoothbuild.util.reflect.Methods.canonicalName;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class MethodsTest {
  @Test
  public void canonical_name_of_non_static_method() throws Exception {
    when(() -> canonicalName(Object.class.getMethod("toString")));
    thenReturned("java.lang.Object.toString");
  }

  @Test
  public void canonical_name_of_static_method() throws Exception {
    when(() -> canonicalName(System.class.getMethod("currentTimeMillis")));
    thenReturned("java.lang.System.currentTimeMillis");
  }

  public static class InnerClass {
    public static void method() {}
  }

  @Test
  public void canonical_name_of_static_method_inside_inner_class() throws Exception {
    when(() -> canonicalName(InnerClass.class.getMethod("method")));
    thenReturned("org.smoothbuild.util.MethodsTest.InnerClass.method");
  }
}
