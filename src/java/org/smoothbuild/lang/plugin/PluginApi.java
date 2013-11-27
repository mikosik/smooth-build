package org.smoothbuild.lang.plugin;

import org.smoothbuild.lang.type.SValueBuilders;
import org.smoothbuild.message.base.Message;

public interface PluginApi extends SValueBuilders {
  public void report(Message message);
}
