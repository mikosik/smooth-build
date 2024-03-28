package org.smoothbuild.virtualmachine.testing;

import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.logToString;

import com.google.common.base.Throwables;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationResult;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;
import org.smoothbuild.virtualmachine.evaluate.task.Task;

public class FakeTaskReporter implements TaskReporter {
  private final List<Reported> reports = new CopyOnWriteArrayList<>();
  private final List<Throwable> throwables = new CopyOnWriteArrayList<>();

  @Override
  public void report(Task task, ComputationResult result) throws BytecodeException {
    reports.add(new Reported(task, result));
  }

  @Override
  public void reportEvaluationException(Throwable throwable) {
    throwables.add(throwable);
  }

  @Override
  public String toString() {
    var builder = new StringBuilder();
    appendReports(builder);
    appendThrowables(builder);
    return builder.toString();
  }

  private void appendReports(StringBuilder stringBuilder) {
    try {
      for (Reported reported : reports) {
        stringBuilder.append("task = ");
        stringBuilder.append(reported.task.getClass().getSimpleName());
        stringBuilder.append("\n");
        BArray array = reported.result().output().storedLogs();
        for (BTuple log : array.elements(BTuple.class)) {
          stringBuilder.append(logToString(log));
          stringBuilder.append("\n");
        }
      }
    } catch (BytecodeException e) {
      throw new RuntimeException(e);
    }
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

  public record Reported(Task task, ComputationResult result) {}
}
