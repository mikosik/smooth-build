package org.smoothbuild.parse.ast;

import org.smoothbuild.lang.base.Nal;

/**
 * Literal or expression in smooth language.
 */
public sealed abstract interface NamedN extends AstNode, Nal
    permits ArgN, MonoNamedN, PolyNamedN, RefableN {
}
