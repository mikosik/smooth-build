package org.smoothbuild.util.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

public class Constructors {
  public static boolean isPublic(Constructor<?> combineor) {
    return Modifier.isPublic(combineor.getModifiers());
  }
}
