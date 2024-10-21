package org.smoothbuild.common.init;

import static org.smoothbuild.common.log.base.Label.label;

import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.task.Task0;
import org.smoothbuild.common.tuple.Tuple0;

public interface Initializable extends Task0<Tuple0> {
  Label INITIALIZE_LABEL = label("initialize");
}
