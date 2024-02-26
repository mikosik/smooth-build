package org.smoothbuild.run.eval.report;

import static com.google.common.base.Strings.padEnd;
import static java.util.Objects.requireNonNullElse;
import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.Strings.limitedWithEllipsis;
import static org.smoothbuild.common.log.Log.containsAnyFailure;
import static org.smoothbuild.run.eval.MessageStruct.level;
import static org.smoothbuild.run.eval.MessageStruct.text;

import jakarta.inject.Inject;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.Log;
import org.smoothbuild.compile.backend.BsMapping;
import org.smoothbuild.compile.frontend.lang.base.location.Location;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.vm.bytecode.BytecodeException;
import org.smoothbuild.vm.bytecode.expr.ExprB;
import org.smoothbuild.vm.bytecode.expr.value.FuncB;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;
import org.smoothbuild.vm.evaluate.compute.ComputationResult;
import org.smoothbuild.vm.evaluate.compute.ResultSource;
import org.smoothbuild.vm.evaluate.execute.TaskReporter;
import org.smoothbuild.vm.evaluate.task.CombineTask;
import org.smoothbuild.vm.evaluate.task.ConstTask;
import org.smoothbuild.vm.evaluate.task.InvokeTask;
import org.smoothbuild.vm.evaluate.task.OrderTask;
import org.smoothbuild.vm.evaluate.task.PickTask;
import org.smoothbuild.vm.evaluate.task.SelectTask;
import org.smoothbuild.vm.evaluate.task.Task;

public class TaskReporterImpl implements TaskReporter {
  // visible for testing
  static final int NAME_LENGTH_LIMIT = 43;
  private final TaskMatcher taskMatcher;
  private final Reporter reporter;
  private final BsMapping bsMapping;
  private final BsTraceTranslator bsTraceTranslator;

  @Inject
  public TaskReporterImpl(TaskMatcher taskMatcher, Reporter reporter, BsMapping bsMapping) {
    this.taskMatcher = taskMatcher;
    this.reporter = reporter;
    this.bsMapping = bsMapping;
    this.bsTraceTranslator = new BsTraceTranslator(bsMapping);
  }

  @Override
  public void report(Task task, ComputationResult result) throws BytecodeException {
    var source = result.source();
    var logs = logsFrom(result);
    report(task, header(task, source), logs);
  }

  private static List<Log> logsFrom(ComputationResult result) throws BytecodeException {
    return result
        .output()
        .messages()
        .elems(TupleB.class)
        .map(message -> new Log(level(message), text(message)));
  }

  private String header(Task task, ResultSource resultSource) {
    var label = "::Evaluating::" + label(task);
    var loc = locationOf(task.exprB());
    var locString = loc == null ? "unknown" : loc.toString();
    var trimmedLabel = limitedWithEllipsis(label, NAME_LENGTH_LIMIT);
    var labelColumn = padEnd(trimmedLabel, NAME_LENGTH_LIMIT + 1, ' ');
    var sourceString = resultSource.toString();
    var locColumn = sourceString.isEmpty() ? locString : padEnd(locString, 30, ' ') + " ";
    return labelColumn + locColumn + sourceString;
  }

  private String label(Task task) {
    return switch (task) {
      case CombineTask combineTask -> "(,)";
      case ConstTask constTask -> label(constTask);
      case InvokeTask invokeTask -> nameOf(invokeTask.nativeFunc()) + "()";
      case OrderTask orderTask -> "[,]";
      case PickTask pickTask -> "[].";
      case SelectTask selectTask -> "{}.";
    };
  }

  private String label(ConstTask constTask) {
    var valueB = constTask.valueB();
    return switch (valueB) {
      case FuncB funcB -> nameOf(funcB);
      default -> valueB.type().name();
    };
  }

  private Location locationOf(ExprB exprB) {
    return bsMapping.locMapping().get(exprB.hash());
  }

  private String nameOf(FuncB funcB) {
    return requireNonNullElse(bsMapping.nameMapping().get(funcB.hash()), "???");
  }

  private void report(Task task, String taskHeader, List<Log> logs) {
    boolean visible = taskMatcher.matches(task, logs);
    var traceS = bsTraceTranslator.translate(task.trace());
    if (containsAnyFailure(logs) && traceS != null) {
      taskHeader += "\n" + indent(traceS.toString());
    }
    reporter.report(visible, taskHeader, logs);
  }
}
