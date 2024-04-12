package org.smoothbuild.virtualmachine.evaluate.execute;

import static org.smoothbuild.virtualmachine.VirtualMachineConstants.EVALUATE_PREFIX;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.level;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.message;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.base.ResultSource;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationResult;
import org.smoothbuild.virtualmachine.evaluate.task.CombineTask;
import org.smoothbuild.virtualmachine.evaluate.task.ConstTask;
import org.smoothbuild.virtualmachine.evaluate.task.InvokeTask;
import org.smoothbuild.virtualmachine.evaluate.task.OrderTask;
import org.smoothbuild.virtualmachine.evaluate.task.PickTask;
import org.smoothbuild.virtualmachine.evaluate.task.SelectTask;
import org.smoothbuild.virtualmachine.evaluate.task.Task;

public record TaskReport(Label label, BTrace trace, ResultSource source, List<Log> logs) {
  public static TaskReport taskReport(Task task, ComputationResult result)
      throws BytecodeException {
    var source = result.source();
    var trace = task.trace();
    var label = taskLabel(task);
    var logs = logsFrom(result);
    return new TaskReport(label, trace, source, logs);
  }

  private static List<Log> logsFrom(ComputationResult result) throws BytecodeException {
    return result
        .output()
        .storedLogs()
        .elements(BTuple.class)
        .map(message -> new Log(level(message), message(message)));
  }

  private static Label taskLabel(Task task) {
    return switch (task) {
      case CombineTask combineTask -> newLabel("combine");
      case ConstTask constTask -> newLabel("const");
      case InvokeTask invokeTask -> newLabel("invoke");
      case OrderTask orderTask -> newLabel("order");
      case PickTask pickTask -> newLabel("pick");
      case SelectTask selectTask -> newLabel("select");
    };
  }

  private static Label newLabel(String combine) {
    return Label.label(EVALUATE_PREFIX, combine);
  }

  public String toPrettyString() {
    var builder = new StringBuilder();
    builder.append("TaskReport [");
    builder.append(this.label());
    builder.append("] ");
    builder.append(source);
    builder.append("\n");
    builder.append(trace.toString());
    builder.append("\n");
    for (var log : this.logs()) {
      builder.append(log.toPrettyString());
      builder.append("\n");
    }
    return builder.toString();
  }
}
