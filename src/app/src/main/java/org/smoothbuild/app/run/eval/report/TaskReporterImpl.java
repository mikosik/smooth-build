package org.smoothbuild.app.run.eval.report;

import static com.google.common.base.Throwables.getStackTraceAsString;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.Label.label;
import static org.smoothbuild.common.log.Log.fatal;
import static org.smoothbuild.common.log.ResultSource.NOOP;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.level;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.message;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Label;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.common.log.Reporter;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.value.FuncB;
import org.smoothbuild.virtualmachine.bytecode.expr.value.TupleB;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationResult;
import org.smoothbuild.virtualmachine.evaluate.execute.TaskReporter;
import org.smoothbuild.virtualmachine.evaluate.task.CombineTask;
import org.smoothbuild.virtualmachine.evaluate.task.ConstTask;
import org.smoothbuild.virtualmachine.evaluate.task.InvokeTask;
import org.smoothbuild.virtualmachine.evaluate.task.OrderTask;
import org.smoothbuild.virtualmachine.evaluate.task.PickTask;
import org.smoothbuild.virtualmachine.evaluate.task.SelectTask;
import org.smoothbuild.virtualmachine.evaluate.task.Task;

public class TaskReporterImpl implements TaskReporter {
  // visible for testing
  static final int NAME_LENGTH_LIMIT = 43;
  private final Reporter reporter;
  private final BsTranslator bsTranslator;

  public TaskReporterImpl(Reporter reporter, BsTranslator bsTranslator) {
    this.reporter = reporter;
    this.bsTranslator = bsTranslator;
  }

  @Override
  public void report(Task task, ComputationResult result) throws BytecodeException {
    var source = result.source();
    var traceS = bsTranslator.translate(task.trace());
    var details = traceS == null ? "" : traceS.toString();
    var logs = logsFrom(result);
    var label = taskLabel(task);
    reporter.report(label, details, source, logs);
  }

  @Override
  public void reportEvaluationException(Throwable throwable) {
    reporter.report(
        label(),
        "",
        NOOP,
        list(fatal("Evaluation failed with: " + getStackTraceAsString(throwable))));
  }

  private static List<Log> logsFrom(ComputationResult result) throws BytecodeException {
    return result
        .output()
        .storedLogs()
        .elements(TupleB.class)
        .map(message -> new Log(level(message), message(message)));
  }

  private Label taskLabel(Task task) {
    return switch (task) {
      case CombineTask combineTask -> label("combine");
      case ConstTask constTask -> label("const", constTask.valueB().type().name());
      case InvokeTask invokeTask -> label("call", nameOf(invokeTask.nativeFunc()));
      case OrderTask orderTask -> label("order");
      case PickTask pickTask -> label("pick");
      case SelectTask selectTask -> label("select");
    };
  }

  private String nameOf(FuncB funcB) {
    return bsTranslator.nameFor(funcB.hash());
  }
}
