package org.smoothbuild.compilerfrontend.compile.ast.define;

import org.smoothbuild.common.log.location.HasLocation;

/**
 * Polymorphic entity.
 */
public sealed interface PPolymorphic extends HasLocation permits PLambda, PReference {}
