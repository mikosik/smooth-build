package org.smoothbuild.common.log.report;

import java.io.PrintWriter;

public class SystemOutReporter extends PrintWriterReporter {
  public SystemOutReporter() {
    super(new PrintWriter(System.out));
  }
}
