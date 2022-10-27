package org.smoothbuild.run.eval.report;

import static com.google.common.base.Strings.padEnd;
import static java.util.Objects.requireNonNullElse;
import static org.smoothbuild.run.eval.MessageStruct.level;
import static org.smoothbuild.run.eval.MessageStruct.text;
import static org.smoothbuild.util.Strings.limitedWithEllipsis;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.List;

import javax.inject.Inject;

import org.smoothbuild.bytecode.expr.ExprB;
import org.smoothbuild.bytecode.expr.inst.FuncB;
import org.smoothbuild.bytecode.expr.inst.TupleB;
import org.smoothbuild.compile.lang.base.Loc;
import org.smoothbuild.compile.sb.BsMapping;
import org.smoothbuild.out.log.Log;
import org.smoothbuild.out.report.Reporter;
import org.smoothbuild.vm.compute.ComputationResult;
import org.smoothbuild.vm.compute.ResultSource;
import org.smoothbuild.vm.execute.TaskReporter;
import org.smoothbuild.vm.task.CombineTask;
import org.smoothbuild.vm.task.ConstTask;
import org.smoothbuild.vm.task.NativeCallTask;
import org.smoothbuild.vm.task.OrderTask;
import org.smoothbuild.vm.task.PickTask;
import org.smoothbuild.vm.task.SelectTask;
import org.smoothbuild.vm.task.Task;

public class ConsoleTaskReporter implements TaskReporter {
  // visible for testing
  static final int NAME_LENGTH_LIMIT = 43;
  private final TaskMatcher taskMatcher;
  private final Reporter reporter;
  private final BsMapping bsMapping;

  @Inject
  public ConsoleTaskReporter(TaskMatcher taskMatcher, Reporter reporter, BsMapping bsMapping) {
    this.taskMatcher = taskMatcher;
    this.reporter = reporter;
    this.bsMapping = bsMapping;
  }

  @Override
  public void report(Task task, ComputationResult result) {
    var source = result.source();
    var logs = map(result.output().messages().elems(TupleB.class), m -> new Log(level(m), text(m)));
    report(task, header(task, source), logs);
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

  private Loc locationOf(ExprB exprB) {
    return bsMapping.locMapping().get(exprB.hash());
  }

  private String label(Task task) {
    return switch (task) {
      case CombineTask combineTask -> "{}";
      case ConstTask constTask -> constTask.instB().type().name();
      case NativeCallTask nativeCallTask -> nameOf(nativeCallTask.natFunc()) + "()";
      case OrderTask orderTask -> "[]";
      case PickTask pickTask -> "[].";
      case SelectTask selectTask -> ".";
    };
  }

  private String nameOf(FuncB funcB) {
    return requireNonNullElse(bsMapping.nameMapping().get(funcB.hash()), "");
  }

  private void report(Task task, String taskHeader, List<Log> logs) {
    boolean visible = taskMatcher.matches(task, logs);
    reporter.report(visible, taskHeader, logs);
  }
}
