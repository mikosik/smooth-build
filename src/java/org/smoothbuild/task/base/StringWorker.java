package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.base.STypes.STRING;

import org.smoothbuild.db.taskresults.TaskResult;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.NativeApiImpl;

import com.google.common.hash.HashCode;

public class StringWorker extends TaskWorker<SString> {
  private final SString string;

  public StringWorker(SString string, CodeLocation codeLocation) {
    super(workerHash(string), STRING, STRING.name(), true, false, codeLocation);
    this.string = checkNotNull(string);
  }

  private static HashCode workerHash(SString string) {
    return WorkerHashes.workerHash(StringWorker.class, string.hash());
  }

  @Override
  public TaskResult<SString> execute(Iterable<? extends SValue> input, NativeApiImpl nativeApi) {
    return new TaskResult<>(string);
  }
}
