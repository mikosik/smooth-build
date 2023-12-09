package org.smoothbuild.out.log;

import static org.smoothbuild.common.collect.List.list;

import org.smoothbuild.common.collect.List;

public class TestingLog {
  public static final Log FATAL_LOG = Log.fatal("fatal message");
  public static final Log ERROR_LOG = Log.error("error message");
  public static final Log WARNING_LOG = Log.warning("warning message");
  public static final Log INFO_LOG = Log.info("info message");

  public static List<Log> logsWithAllLevels() {
    return list(FATAL_LOG, ERROR_LOG, WARNING_LOG, INFO_LOG);
  }
}
