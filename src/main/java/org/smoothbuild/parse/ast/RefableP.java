package org.smoothbuild.parse.ast;

import java.util.Optional;

import org.smoothbuild.lang.like.common.RefableC;

/**
 * Referencable.
 */
public sealed interface RefableP extends Parsed, RefableC, NamedP
    permits MonoRefableP, GenericRefableP, TopRefableP {

  public Optional<AnnP> ann();

  public Optional<TypeP> evalT();

  public Optional<ObjP> body();
}
