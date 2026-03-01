package org.smoothbuild.virtualmachine;

import static org.smoothbuild.common.log.base.Label.label;

import org.smoothbuild.common.log.base.Label;

public class VmConstants {
  public static final Label VM_LABEL = label(":vm");
  public static final Label VM_EVALUATE = VM_LABEL.append(":evaluate");
  public static final int CALL_DEPTH_LIMIT = 128;
}
