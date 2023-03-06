package org.smoothbuild;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class SmoothConstants {
  public static final int EXIT_CODE_SUCCESS = 0;
  public static final int EXIT_CODE_JAVA_EXCEPTION = 1;
  public static final int EXIT_CODE_ERROR = 2;

  public static final Charset CHARSET = StandardCharsets.UTF_8;
}
