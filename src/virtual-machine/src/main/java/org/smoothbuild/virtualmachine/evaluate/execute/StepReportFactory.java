package org.smoothbuild.virtualmachine.evaluate.execute;

import static org.smoothbuild.common.log.report.Report.report;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.level;
import static org.smoothbuild.virtualmachine.bytecode.helper.StoredLogStruct.message;

import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.log.base.Log;
import org.smoothbuild.common.log.report.Report;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationResult;
import org.smoothbuild.virtualmachine.evaluate.step.Step;

public class StepReportFactory {
  public static Report create(Step step, ComputationResult result) throws BytecodeException {
    var source = result.source();
    var trace = step.trace();
    var label = step.label();
    var logs = logsFrom(result);
    return report(label, trace, source, logs);
  }

  private static List<Log> logsFrom(ComputationResult result) throws BytecodeException {
    return result
        .bOutput()
        .storedLogs()
        .elements(BTuple.class)
        .map(message -> new Log(level(message), message(message)));
  }
}
