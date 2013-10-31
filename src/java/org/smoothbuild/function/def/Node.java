package org.smoothbuild.function.def;

import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.exec.TaskGenerator;

public interface Node {

  public CodeLocation codeLocation();

  public Type type();

  public LocatedTask generateTask(TaskGenerator taskGenerator);
}
