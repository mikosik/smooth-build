package org.smoothbuild.util.reflect;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.smoothbuild.util.reflect.Constructors.isPublic;

import org.junit.Test;

public class ConstructorsTest {
  @Test
  public void is_public() throws Exception {
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
