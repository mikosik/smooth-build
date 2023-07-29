package org.smoothbuild.fs.project;

import java.util.concurrent.atomic.AtomicInteger;

import org.smoothbuild.util.fs.base.PathS;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;

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
    return ProjectPaths.TEMPORARY_PATH.appendPart(Integer.toString(id.getAndIncrement()));
  }
}
