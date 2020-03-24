package org.smoothbuild.util.reflect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.reflect.Constructors.isPublic;

import org.junit.jupiter.api.Test;

public class ConstructorsTest {
  @Test
  public void private_constructor() throws NoSuchMethodException {
    assertThat(isPublic(MyPrivateConstructorClass.class.getDeclaredConstructor()))
        .isFalse();
  }

  public static class MyPrivateConstructorClass {
    private MyPrivateConstructorClass() {}
  }

  @Test
  public void protected_constructor() throws NoSuchMethodException {
    assertThat(isPublic(MyProtectedConstructorClass.class.getDeclaredConstructor()))
        .isFalse();
  }

  public static class MyProtectedConstructorClass {
    protected MyProtectedConstructorClass() {}
  }

  @Test
  public void package_private_constructor() throws NoSuchMethodException {
    assertThat(isPublic(MyPackageConstructorClass.class.getDeclaredConstructor()))
        .isFalse();
  }

  public static class MyPackageConstructorClass {
    MyPackageConstructorClass() {}
  }

  @Test
  public void public_constructor() throws NoSuchMethodException {
    assertThat(isPublic(MyPublicConstructorClass.class.getDeclaredConstructor()))
        .isTrue();
  }

  public static class MyPublicConstructorClass {
    public MyPublicConstructorClass() {}
  }
}
