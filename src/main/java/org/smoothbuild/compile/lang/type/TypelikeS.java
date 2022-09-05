package org.smoothbuild.compile.lang.type;

import org.smoothbuild.util.collect.Named;

/**
 * Type or Type Schema in smooth language.
 */
public sealed interface TypelikeS extends Named
    permits TypeS, SchemaS {
}
