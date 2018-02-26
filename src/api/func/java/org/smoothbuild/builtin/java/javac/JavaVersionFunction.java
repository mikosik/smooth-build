package org.smoothbuild.builtin.java.javac;

import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.plugin.NotCacheable;
import org.smoothbuild.lang.plugin.SmoothFunction;
import org.smoothbuild.lang.value.SString;

public class JavaVersionFunction {
  @SmoothFunction()
  @NotCacheable
  public static SString javaVersion(NativeApi nativeApi) {
    String version = System.getProperty("java.version");
    if (version == null) {
      nativeApi.log().error("Cannot detect java version.");
      return null;
    } else {
      return nativeApi.create().string(version);
    }
  }
}
