package org.smoothbuild.run.eval.report;

import static com.google.common.base.Strings.padEnd;
import static java.util.Objects.requireNonNullElse;
import static org.smoothbuild.out.log.Level.WARNING;
import static org.smoothbuild.run.eval.MessageStruct.level;
import static org.smoothbuild.run.eval.MessageStruct.text;
import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.Strings.limitedWithEllipsis;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.value.ClosureB;
import org.smoothbuild.bytecode.expr.value.FuncB;
import org.smoothbuild.bytecode.expr.value.TupleB;
import org.smoothbuild.compile.lang.base.location.Location;
import org.smoothbuild.compile.sb.BsMapping;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.vm.compute.ComputationResult;
import org.smoothbuild.vm.compute.ResultSource;
import org.smoothbuild.vm.execute.TaskReporter;
import org.smoothbuild.vm.task.CombineTask;
import org.smoothbuild.vm.task.ConstTask;
import org.smoothbuild.vm.task.InvokeTask;
import org.smoothbuild.vm.task.OrderTask;
import org.smoothbuild.vm.task.PickTask;
import org.smoothbuild.vm.task.SelectTask;
import org.smoothbuild.vm.task.Task;

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
  public void report(Task task, ComputationResult result) {
    var source = result.source();
    var logBuffer = logsFrom(result);
    report(task, header(task, source), logBuffer);
  }

  private static LogBuffer logsFrom(ComputationResult result) {
    var logBuffer = new LogBuffer();
    result.output().messages().elems(TupleB.class)
        .forEach(message -> logBuffer.log(new Log(level(message), text(message))));
    return logBuffer;
  }

  private String header(Task task, ResultSource resultSource) {
    var tag = label(task);
    var loc = locationOf(task.exprB());
    var locString = loc == null ? "unknown" : loc.toString();
    var trimmedLabel = limitedWithEllipsis(tag, NAME_LENGTH_LIMIT);
    var labelColumn = padEnd(trimmedLabel, NAME_LENGTH_LIMIT + 1, ' ');
    var sourceString = resultSource.toString();
    var locColumn = sourceString.isEmpty()
        ? locString
        : padEnd(locString, 30, ' ') + " ";
    return labelColumn + locColumn + sourceString;
  }

  private String label(Task task) {
    return switch (task) {
      case CombineTask combineTask -> "{...}";
      case ConstTask constTask -> label(constTask);
      case InvokeTask invokeTask -> nameOf(invokeTask.nativeFunc()) + "()";
      case OrderTask orderTask -> "[...]";
      case PickTask pickTask -> "[].";
      case SelectTask selectTask -> "{}.";
    };
  }

  private String label(ConstTask constTask) {
    var valueB = constTask.valueB();
    return switch (valueB) {
      case ClosureB closureB -> nameOf(closureB.func());
      case FuncB funcB -> nameOf(funcB);
      default -> valueB.type().name();
    };
  }

  private Location locationOf(ExprB exprB) {
    if (exprB instanceof ClosureB closureB) {
      return locationOf(closureB.func());
    } else {
      return bsMapping.locMapping().get(exprB.hash());
    }
  }

  private String nameOf(FuncB funcB) {
    return requireNonNullElse(bsMapping.nameMapping().get(funcB.hash()), "???");
  }

  private void report(Task task, String taskHeader, LogBuffer logs) {
    boolean visible = taskMatcher.matches(task, logs);
    var traceS = bsTraceTranslator.translate(task.trace());
    if (logs.containsAtLeast(WARNING) && traceS != null) {
      taskHeader += "\n" + indent(traceS.toString());
    }
    reporter.report(visible, taskHeader, logs);
  }
}
