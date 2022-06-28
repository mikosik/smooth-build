package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Nal;

/**
 * Literal or expression in smooth language.
 */
public sealed abstract interface NamedP extends Parsed, Nal
    permits ArgP, MonoNamedP, GenericNamedP, RefableP {
}
