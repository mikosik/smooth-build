package org.smoothbuild.parse;

import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.like.Refable;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstVisitor;
import org.smoothbuild.parse.ast.FuncP;
import org.smoothbuild.parse.ast.RefP;
import org.smoothbuild.parse.ast.StructP;
import org.smoothbuild.util.Scope;

public class ReferenceResolver extends AstVisitor {
  private final Scope<? extends Refable> scope;
  private final Logger logger;

  public static void resolveReferences(Logger logger, DefsS imported, Ast ast) {
    var scope = scope(imported, ast);
    new ReferenceResolver(scope, logger)
        .visitAst(ast);
  }

  private static Scope<Refable> scope(DefsS imported, Ast ast) {
    var importedScope = new Scope<Refable>(imported.topRefables());
    var ctors = map(ast.structs(), StructP::ctor);
    var refables = ast.topRefables();
    return new Scope<>(importedScope, nList(concat(refables, ctors)));
  }

  public ReferenceResolver(Scope<? extends Refable> scope, Logger logger) {
    this.scope = scope;
    this.logger = logger;
  }

  @Override
  public void visitFunc(FuncP funcP) {
    visitParams(funcP.params());
    funcP.body().ifPresent(expr -> {
      var referenceResolver = new ReferenceResolver(new Scope<>(scope, funcP.params()), logger);
      referenceResolver.visitObj(expr);
    });
  }

  @Override
  public void visitRef(RefP ref) {
    super.visitRef(ref);
    String name = ref.name();
    if (scope.contains(name)) {
      ref.setReferenced(scope.get(name));
    } else {
      logger.log(parseError(ref.loc(), "`" + name + "` is undefined."));
    }
  }
}
