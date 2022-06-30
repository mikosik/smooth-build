package org.smoothbuild.parse;

import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.like.Refable;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstVisitor;
import org.smoothbuild.parse.ast.FuncP;
import org.smoothbuild.parse.ast.RefP;
import org.smoothbuild.parse.ast.StructP;
import org.smoothbuild.util.NameBindings;
import org.smoothbuild.util.collect.Nameables;

public class ReferenceResolver extends AstVisitor {
  private final NameBindings<? extends Refable> nameBindings;
  private final Logger logger;

  public static void resolveReferences(Logger logger, DefsS imported, Ast ast) {
    var scope = scope(imported, ast);
    new ReferenceResolver(scope, logger)
        .visitAst(ast);
  }

  private static NameBindings<Refable> scope(DefsS imported, Ast ast) {
    var importedScope = new NameBindings<Refable>(imported.topRefables().map());
    var ctors = map(ast.structs(), StructP::ctor);
    var refables = ast.topRefables();
    return new NameBindings<>(importedScope, Nameables.toMap(concat(refables, ctors)));
  }

  public ReferenceResolver(NameBindings<? extends Refable> nameBindings, Logger logger) {
    this.nameBindings = nameBindings;
    this.logger = logger;
  }

  @Override
  public void visitFunc(FuncP funcP) {
    visitParams(funcP.params());
    funcP.body().ifPresent(expr -> {
      var referenceResolver = new ReferenceResolver(
          new NameBindings<>(nameBindings, funcP.params().map()), logger);
      referenceResolver.visitObj(expr);
    });
  }

  @Override
  public void visitRef(RefP ref) {
    super.visitRef(ref);
    String name = ref.name();
    if (nameBindings.contains(name)) {
      ref.setReferenced(nameBindings.get(name));
    } else {
      logger.log(parseError(ref.loc(), "`" + name + "` is undefined."));
    }
  }
}
