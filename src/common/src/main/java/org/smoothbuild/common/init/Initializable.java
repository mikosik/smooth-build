package org.smoothbuild.common.init;

import static org.smoothbuild.common.log.base.Label.label;

import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.task.Task0;

public interface Initializable extends Task0<Void> {
  Label INITIALIZE_LABEL = label("initialize");
}
