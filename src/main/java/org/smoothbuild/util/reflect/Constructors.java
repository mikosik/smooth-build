package org.smoothbuild.util.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class Constructors {
  public static boolean isPublic(Constructor<?> constructor) {
    return Modifier.isPublic(constructor.getModifiers());
  }
}
