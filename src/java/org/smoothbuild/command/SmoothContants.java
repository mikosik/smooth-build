package org.smoothbuild.command;

import static org.smoothbuild.io.fs.base.Path.path;

import java.nio.charset.Charset;

import org.smoothbuild.io.fs.base.Path;

import com.google.common.base.Charsets;

public class SmoothContants {
  public static final Path DEFAULT_SCRIPT = path("build.smooth");

  public static final Charset CHARSET = Charsets.UTF_8;
}
