package org.smoothbuild.common.reflect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.reflect.Methods.canonicalName;
import static org.smoothbuild.common.reflect.Methods.isPublic;
import static org.smoothbuild.common.reflect.Methods.isStatic;

import org.junit.jupiter.api.Test;

public class MethodsTest {
  @Test
  void is_public() throws Exception {
    Class<?> klass = MyClass.class;

    assertThat(isPublic(klass.getDeclaredMethod("publicMethod"))).isTrue();
    assertThat(isPublic(klass.getDeclaredMethod("publicStaticMethod"))).isTrue();

    assertThat(isPublic(klass.getDeclaredMethod("packageMethod"))).isFalse();
    assertThat(isPublic(klass.getDeclaredMethod("packageStaticMethod"))).isFalse();

    assertThat(isPublic(klass.getDeclaredMethod("protectedMethod"))).isFalse();
    assertThat(isPublic(klass.getDeclaredMethod("protectedStaticMethod"))).isFalse();

    assertThat(isPublic(klass.getDeclaredMethod("privateMethod"))).isFalse();
    assertThat(isPublic(klass.getDeclaredMethod("privateStaticMethod"))).isFalse();
  }

  @Test
  void is_static() throws Exception {
    Class<MyClass> klass = MyClass.class;
    assertThat(isStatic(klass.getDeclaredMethod("publicMethod"))).isFalse();
    assertThat(isStatic(klass.getDeclaredMethod("publicStaticMethod"))).isTrue();

    assertThat(isStatic(klass.getDeclaredMethod("packageMethod"))).isFalse();
    assertThat(isStatic(klass.getDeclaredMethod("packageStaticMethod"))).isTrue();

    assertThat(isStatic(klass.getDeclaredMethod("protectedMethod"))).isFalse();
    assertThat(isStatic(klass.getDeclaredMethod("protectedStaticMethod"))).isTrue();

    assertThat(isStatic(klass.getDeclaredMethod("privateMethod"))).isFalse();
    assertThat(isStatic(klass.getDeclaredMethod("privateStaticMethod"))).isTrue();
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
  void canonical_name_of_non_static_method() throws Exception {
    assertThat(canonicalName(Object.class.getMethod("toString")))
        .isEqualTo("java.lang.Object.toString");
  }

  @Test
  void canonical_name_of_static_method() throws Exception {
    assertThat(canonicalName(System.class.getMethod("currentTimeMillis")))
        .isEqualTo("java.lang.System.currentTimeMillis");
  }

  @Test
  void canonical_name_of_static_method_inside_inner_class() throws Exception {
    assertThat(canonicalName(InnerClass.class.getMethod("method")))
        .isEqualTo("org.smoothbuild.common.reflect.MethodsTest.InnerClass.method");
  }

  public static class InnerClass {
    public static void method() {}
  }
}
