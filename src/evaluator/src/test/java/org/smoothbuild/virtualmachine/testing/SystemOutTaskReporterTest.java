package org.smoothbuild.virtualmachine.testing;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.smoothbuild.common.log.base.ResultSource.EXECUTION;

import java.io.PrintWriter;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.evaluate.compute.ComputationResult;
import org.smoothbuild.virtualmachine.evaluate.task.Output;

public class SystemOutTaskReporterTest extends TestingVirtualMachine {
  @Test
  void log_is_printed_to_print_writer() throws BytecodeException {
    PrintWriter printWriter = mock();
    var systemOutTaskReporter = new SystemOutTaskReporter(printWriter);
    var storedLogs = bArray(bTuple(bString("ERROR"), bString("detailed message")));
    var output = new Output(null, storedLogs);
    var result = new ComputationResult(output, EXECUTION);

    systemOutTaskReporter.report(null, result);

    verify(printWriter).println("ERROR detailed message");
  }
}
