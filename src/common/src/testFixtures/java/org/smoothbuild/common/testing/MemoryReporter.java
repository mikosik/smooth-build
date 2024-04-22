package org.smoothbuild.common.testing;

import static org.smoothbuild.common.collect.List.listOfAll;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.common.log.report.Reporter;

public class MemoryReporter implements Reporter {
  private final CopyOnWriteArrayList<Report> reports = new CopyOnWriteArrayList<>();

  @Override
  public void submit(Report report) {
    reports.add(report);
  }

  public boolean containsFailure() {
    return reports.stream().anyMatch(r -> Log.containsFailure(r.logs()));
  }

  public List<Report> reports() {
    return listOfAll(reports);
  }

  public List<Log> logs() {
    return listOfAll(streamOfAllLogs().toList());
  }

  private Stream<Log> streamOfAllLogs() {
    return reports.stream().flatMap(r -> r.logs().stream());
  }

  @Override
  public String toString() {
    return listOfAll(reports).map(Report::toPrettyString).toString("\n");
  }
}
