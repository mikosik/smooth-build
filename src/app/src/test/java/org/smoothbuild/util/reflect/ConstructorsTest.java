package org.smoothbuild.util.reflect;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.util.reflect.Constructors.isPublic;

import org.junit.jupiter.api.Test;

public class ConstructorsTest {
  @Test
  public void private_ctor() throws NoSuchMethodException {
    assertThat(isPublic(MyPrivateCtorClass.class.getDeclaredConstructor()))
        .isFalse();
  }

  public static class MyPrivateCtorClass {
    private MyPrivateCtorClass() {}
  }

  @Test
  public void protected_ctor() throws NoSuchMethodException {
    assertThat(isPublic(MyProtectedCtorClass.class.getDeclaredConstructor()))
        .isFalse();
  }

  public static class MyProtectedCtorClass {
    protected MyProtectedCtorClass() {}
  }

  @Test
  public void package_private_ctor() throws NoSuchMethodException {
    assertThat(isPublic(MyPackageCtorClass.class.getDeclaredConstructor()))
        .isFalse();
  }

  public static class MyPackageCtorClass {
    MyPackageCtorClass() {}
  }

  @Test
  public void public_ctor() throws NoSuchMethodException {
    assertThat(isPublic(MyPublicCtorClass.class.getDeclaredConstructor()))
        .isTrue();
  }

  public static class MyPublicCtorClass {
    public MyPublicCtorClass() {}
  }
}
