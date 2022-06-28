package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.like.Refable;

/**
 * Referencable.
 */
public sealed interface RefableP extends Parsed, Refable, NamedP
    permits MonoRefableP, GenericRefableP, TopRefableP {

  public Optional<AnnP> ann();

  public Optional<TypeP> evalT();

  public Optional<ObjP> body();
}
