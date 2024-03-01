package org.smoothbuild.compilerfrontend.lang.type.tool;

import org.smoothbuild.compilerfrontend.lang.type.TempVarS;

/**
 * Constraint stating that TempVars in `schema` can be substituted in such a way
 * that `schema` is equal to `instantiation`.
 */
public record InstantiationConstraint(TempVarS instantiation, TempVarS schema)
    implements Constraint {}
