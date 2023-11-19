package org.smoothbuild.compile.frontend.compile.ast.define;

import static org.smoothbuild.common.Strings.indent;
import static org.smoothbuild.common.collect.Iterables.joinToString;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Objects;

public final class ModuleP implements ScopedP {
  private final String name;
  private final ImmutableList<StructP> structs;
  private final ImmutableList<NamedEvaluableP> evaluables;
  private ScopeP scope;

  public ModuleP(String name, List<StructP> structs, List<NamedEvaluableP> evaluables) {
    this(name, structs, evaluables, null);
  }

  public ModuleP(
      String name, List<StructP> structs, List<NamedEvaluableP> evaluables, ScopeP scope) {
    this.name = name;
    this.structs = ImmutableList.copyOf(structs);
    this.evaluables = ImmutableList.copyOf(evaluables);
    this.scope = scope;
  }

  @Override
  public String name() {
    return name;
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
    var fields = joinToString(
        "\n",
        "structs = [",
        indent(joinToString(structs(), "\n")),
        "]",
        "evaluables = [",
        indent(joinToString(evaluables(), "\n")),
        "]");
    return "ModuleP(\n" + indent(fields) + "\n)";
  }
}
