package org.smoothbuild.install;

import static org.smoothbuild.install.ProjectPaths.TEMPORARY_PATH;

import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.smoothbuild.io.fs.base.PathS;

/**
 * This class is thread-safe.
 */
@Singleton
public class TempManager {
  private final AtomicInteger id = new AtomicInteger();

  @Inject
  public TempManager() {
  }

  public PathS tempPath() {
    return TEMPORARY_PATH.appendPart(Integer.toString(id.getAndIncrement()));
  }
}
