package org.smoothbuild.common.testing;

import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.log.base.Log.containsAnyFailure;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.Reporter;

public class MemoryReporter implements Reporter {
  private final CopyOnWriteArrayList<Report> reports = new CopyOnWriteArrayList<>();

  @Override
  public void report(Report report) {
    reports.add(report);
  }

  public boolean containsFailure() {
    return reports.stream().anyMatch(r -> containsAnyFailure(r.logs()));
  }

  public List<Log> logs() {
    return listOfAll(streamOfAllLogs().toList());
  }

  private Stream<Log> streamOfAllLogs() {
    return reports.stream().flatMap(r -> r.logs().stream());
  }
}
