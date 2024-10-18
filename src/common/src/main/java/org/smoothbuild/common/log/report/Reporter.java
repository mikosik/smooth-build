package org.smoothbuild.common.log.report;

import com.google.inject.ImplementedBy;

@ImplementedBy(SystemOutReporter.class)
public interface Reporter {
  public void submit(Report report);
}
