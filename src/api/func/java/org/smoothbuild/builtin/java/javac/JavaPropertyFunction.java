package org.smoothbuild.builtin.java.javac;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class JavaPropertyFunction {
  @SmoothFunction(value = "javaProperty", cacheable = false)
  public static SString javaProperty(NativeApi nativeApi, SString name) {
    String nameString = name.data();
    String property = System.getProperty(nameString);
    if (property == null) {
      nativeApi.log().error("Unknown property '" + nameString + "'.");
      return null;
    } else {
      return nativeApi.create().string(property);
    }
  }
}
