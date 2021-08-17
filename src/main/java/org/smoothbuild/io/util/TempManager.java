package org.smoothbuild.io.util;

import static org.smoothbuild.install.ProjectPaths.TEMPORARY_PATH;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.io.fs.base.Path;

/**
 * This class is thread-safe.
 */
@Singleton
public class TempManager {
  private final AtomicInteger id = new AtomicInteger();

  @Inject
  public TempManager() {
  }

  public Path tempPath() {
    return TEMPORARY_PATH.appendPart(Integer.toString(id.getAndIncrement()));
  }
}
