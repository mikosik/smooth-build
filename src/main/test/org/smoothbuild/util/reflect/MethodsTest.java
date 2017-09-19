package org.smoothbuild.util.reflect;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.smoothbuild.util.reflect.Methods.canonicalName;
import static org.smoothbuild.util.reflect.Methods.isPublic;
import static org.smoothbuild.util.reflect.Methods.isStatic;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

public class MethodsTest {

  @Test
  public void is_public() throws Exception {
    Class<?> klass = MyClass.class;

    assertTrue(isPublic(klass.getDeclaredMethod("publicMethod")));
    assertTrue(isPublic(klass.getDeclaredMethod("publicStaticMethod")));

    assertFalse(isPublic(klass.getDeclaredMethod("packageMethod")));
    assertFalse(isPublic(klass.getDeclaredMethod("packageStaticMethod")));

    assertFalse(isPublic(klass.getDeclaredMethod("protectedMethod")));
    assertFalse(isPublic(klass.getDeclaredMethod("protectedStaticMethod")));

    assertFalse(isPublic(klass.getDeclaredMethod("privateMethod")));
    assertFalse(isPublic(klass.getDeclaredMethod("privateStaticMethod")));
  }

  @Test
  public void is_static() throws Exception {
    assertFalse(isStatic(MyClass.class.getDeclaredMethod("publicMethod")));
    assertTrue(isStatic(MyClass.class.getDeclaredMethod("publicStaticMethod")));

    assertFalse(isStatic(MyClass.class.getDeclaredMethod("packageMethod")));
    assertTrue(isStatic(MyClass.class.getDeclaredMethod("packageStaticMethod")));

    assertFalse(isStatic(MyClass.class.getDeclaredMethod("protectedMethod")));
    assertTrue(isStatic(MyClass.class.getDeclaredMethod("protectedStaticMethod")));

    assertFalse(isStatic(MyClass.class.getDeclaredMethod("privateMethod")));
    assertTrue(isStatic(MyClass.class.getDeclaredMethod("privateStaticMethod")));
  }

  public static class MyClass {
    public void publicMethod() {}

    public static void publicStaticMethod() {}

    void packageMethod() {}

    static void packageStaticMethod() {}

    protected void protectedMethod() {}

    protected static void protectedStaticMethod() {}

    @SuppressWarnings("unused")
    private void privateMethod() {}

    @SuppressWarnings("unused")
    private static void privateStaticMethod() {}
  }

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
    thenReturned("org.smoothbuild.util.reflect.MethodsTest.InnerClass.method");
  }
}
