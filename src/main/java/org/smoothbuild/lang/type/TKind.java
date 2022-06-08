package org.smoothbuild.lang.type;

import org.smoothbuild.util.collect.Named;

/**
 * Type kind.
 */
public sealed interface TKind extends Named
  permits TypeS, PolyFuncTS {
}
