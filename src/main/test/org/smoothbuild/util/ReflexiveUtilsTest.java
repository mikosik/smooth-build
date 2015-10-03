package org.smoothbuild.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.smoothbuild.util.ReflexiveUtils.isPublic;
import static org.smoothbuild.util.ReflexiveUtils.isStatic;

import org.junit.Test;

public class ReflexiveUtilsTest {

  @Test
  public void is_method_public() throws Exception {
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
  public void is_method_static() throws Exception {
    Class<?> klass = MyClass.class;

    assertFalse(isStatic(klass.getDeclaredMethod("publicMethod")));
    assertTrue(isStatic(klass.getDeclaredMethod("publicStaticMethod")));

    assertFalse(isStatic(klass.getDeclaredMethod("packageMethod")));
    assertTrue(isStatic(klass.getDeclaredMethod("packageStaticMethod")));

    assertFalse(isStatic(klass.getDeclaredMethod("protectedMethod")));
    assertTrue(isStatic(klass.getDeclaredMethod("protectedStaticMethod")));

    assertFalse(isStatic(klass.getDeclaredMethod("privateMethod")));
    assertTrue(isStatic(klass.getDeclaredMethod("privateStaticMethod")));
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
  public void is_constructor_public() throws Exception {
    assertTrue(isPublic(MyPublicConstructorClass.class.getDeclaredConstructor()));
    assertFalse(isPublic(MyPackageConstructorClass.class.getDeclaredConstructor()));
    assertFalse(isPublic(MyProtectedConstructorClass.class.getDeclaredConstructor()));
    assertFalse(isPublic(MyPrivateConstructorClass.class.getDeclaredConstructor()));
  }

  public static class MyPublicConstructorClass {
    public MyPublicConstructorClass() {}
  }

  public static class MyPackageConstructorClass {
    MyPackageConstructorClass() {}
  }

  public static class MyProtectedConstructorClass {
    protected MyProtectedConstructorClass() {}
  }

  public static class MyPrivateConstructorClass {
    private MyPrivateConstructorClass() {}
  }
}
