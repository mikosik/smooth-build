package org.smoothbuild.function;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.function.ReflexiveUtils.isPublic;
import static org.smoothbuild.function.ReflexiveUtils.isStatic;

import org.junit.Test;

public class ReflexiveUtilsTest {

  @Test
  public void testIsMethodPublic() throws Exception {
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
  public void testIsMethodStatic() throws Exception {
    Class<?> klass = MyClass.class;

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
  public void testIsConstructorPublic() throws Exception {
    assertThat(isPublic(MyPublicConstructorClass.class.getDeclaredConstructor())).isTrue();
    assertThat(isPublic(MyPackageConstructorClass.class.getDeclaredConstructor())).isFalse();
    assertThat(isPublic(MyProtectedConstructorClass.class.getDeclaredConstructor())).isFalse();
    assertThat(isPublic(MyPrivateConstructorClass.class.getDeclaredConstructor())).isFalse();
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
