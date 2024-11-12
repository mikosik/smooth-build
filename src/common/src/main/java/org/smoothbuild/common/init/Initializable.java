package org.smoothbuild.common.init;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.log.base.Label.label;
import static org.smoothbuild.common.log.base.Log.fatal;
import static org.smoothbuild.common.task.Output.output;
import static org.smoothbuild.common.tuple.Tuples.tuple;

import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.task.Output;
import org.smoothbuild.common.task.Task0;
import org.smoothbuild.common.tuple.Tuple0;

public abstract class Initializable implements Task0<Tuple0> {
  public static Label INITIALIZE_LABEL = label("initialize");
  private final String componentName;

  protected Initializable(String componentName) {
    this.componentName = componentName;
  }

  @Override
  public Output<Tuple0> execute() {
    var label = INITIALIZE_LABEL.append(componentName);
    try {
      executeImpl();
      return output(tuple(), label, list());
    } catch (Exception e) {
      var fatal = fatal("Initializing " + componentName + " failed with exception:", e);
      return output(label, list(fatal));
    }
  }

  protected abstract void executeImpl() throws Exception;
}
