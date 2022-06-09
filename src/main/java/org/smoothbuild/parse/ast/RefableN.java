package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.like.Refable;

/**
 * Referencable.
 */
public sealed interface RefableN extends Refable, AstNode, NamedN
    permits MonoRefableN, PolyRefableN, RefableObjN {

  public Optional<AnnN> ann();

  public Optional<TypeN> evalTN();

  public Optional<ObjN> body();
}
