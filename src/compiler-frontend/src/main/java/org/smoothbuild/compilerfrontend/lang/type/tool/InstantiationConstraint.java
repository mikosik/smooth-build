package org.smoothbuild.compilerfrontend.lang.type.tool;

import org.smoothbuild.compilerfrontend.lang.type.STempVar;

/**
 * Constraint stating that TempVars in `schema` can be substituted in such a way
 * that `schema` is equal to `instantiation`.
 */
public record InstantiationConstraint(STempVar instantiation, STempVar schema)
    implements Constraint {}
