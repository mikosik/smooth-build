package org.smoothbuild.virtualmachine.testing;

import com.google.common.base.Throwables;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReport;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;

public class FakeTaskReporter implements TaskReporter {
  private final List<TaskReport> taskReports = new CopyOnWriteArrayList<>();
  private final List<Throwable> throwables = new CopyOnWriteArrayList<>();

  @Override
  public void report(TaskReport taskReport) {
    taskReports.add(taskReport);
  }

  @Override
  public void reportEvaluationException(Throwable throwable) {
    throwables.add(throwable);
  }

  @Override
  public String toString() {
    var builder = new StringBuilder();
    appendTaskReports(builder);
    appendThrowables(builder);
    return builder.toString();
  }

  private void appendTaskReports(StringBuilder stringBuilder) {
    for (var taskReport : taskReports) {
      stringBuilder.append("\n");
      stringBuilder.append(taskReport.toPrettyString());
    }
  }

  public List<TaskReport> getTaskReports() {
    return taskReports;
  }

  private void appendThrowables(StringBuilder builder) {
    if (!throwables.isEmpty()) {
      builder.append("THROWABLES:\n");
      for (int i = 0; i < throwables.size(); i++) {
        builder.append("Throwable ").append(i).append("\n");
        builder.append(Throwables.getStackTraceAsString(throwables.get(i)));
      }
    }
  }
}
