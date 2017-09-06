package org.smoothbuild.parse;

import static java.util.stream.Collectors.toSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.CallNode;
import org.smoothbuild.parse.ast.CallNode.ParamRefFlag;
import org.smoothbuild.parse.ast.FuncNode;

public class MarkParamRef {
  public static List<ParseError> markParamRef(Ast ast) {
    ArrayList<ParseError> errors = new ArrayList<>();
    new AstVisitor() {
      private Set<Name> params;

      public void visitFunction(FuncNode func) {
        params = paramNames(func);
        super.visitFunction(func);
        params = null;
      }

      private Set<Name> paramNames(FuncNode func) {
        return func
            .params()
            .stream()
            .map(p -> p.name())
            .collect(toSet());
      }

      public void visitCall(CallNode call) {
        if (params.contains(call.name())) {
          call.set(ParamRefFlag.class, new ParamRefFlag());
        }
        super.visitCall(call);
      }
    }.visitAst(ast);
    return errors;
  }
}
