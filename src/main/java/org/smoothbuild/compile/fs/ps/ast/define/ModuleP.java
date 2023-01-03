package org.smoothbuild.compile.fs.ps.ast.define;

import static org.smoothbuild.util.Strings.indent;
import static org.smoothbuild.util.collect.Iterables.joinToString;

import java.util.List;
import java.util.Objects;

import com.google.common.collect.ImmutableList;

public final class ModuleP implements WithScopeP {
  private final ImmutableList<StructP> structs;
  private final ImmutableList<NamedEvaluableP> evaluables;
  private ScopeP scope;

  public ModuleP(List<StructP> structs, List<NamedEvaluableP> evaluables) {
    this(structs, evaluables, null);
  }

  public ModuleP(List<StructP> structs, List<NamedEvaluableP> evaluables, ScopeP scope) {
    this.structs = ImmutableList.copyOf(structs);
    this.evaluables = ImmutableList.copyOf(evaluables);
    this.scope = scope;
  }

  public ImmutableList<NamedEvaluableP> evaluables() {
    return evaluables;
  }

  public ImmutableList<StructP> structs() {
    return structs;
  }

  @Override
  public ScopeP scope() {
    return scope;
  }

  @Override
  public void setScope(ScopeP scope) {
    this.scope = scope;
  }
  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }
    return object instanceof ModuleP that
        && Objects.equals(this.structs, that.structs)
        && Objects.equals(this.evaluables, that.evaluables);
  }

  @Override
  public int hashCode() {
    return Objects.hash(structs, evaluables);
  }

  @Override
  public String toString() {
    var fields = joinToString("\n",
        "structs = [",
        indent(joinToString(structs(), "\n")),
        "]",
        "evaluables = [",
        indent(joinToString(evaluables(), "\n")),
        "]"
    );
    return "ModuleP(\n" + indent(fields) + "\n)";
  }
}
