package org.smoothbuild.parse;

import static org.smoothbuild.parse.ParseError.parseError;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Maps.map;

import org.smoothbuild.lang.define.DefsS;
import org.smoothbuild.lang.define.FuncS;
import org.smoothbuild.lang.define.TopRefableS;
import org.smoothbuild.lang.define.ValS;
import org.smoothbuild.lang.like.common.RefableC;
import org.smoothbuild.lang.like.wrap.FuncW;
import org.smoothbuild.lang.like.wrap.TopRefableW;
import org.smoothbuild.out.log.Logger;
import org.smoothbuild.parse.ast.Ast;
import org.smoothbuild.parse.ast.AstVisitor;
import org.smoothbuild.parse.ast.FuncP;
import org.smoothbuild.parse.ast.RefP;
import org.smoothbuild.parse.ast.StructP;
import org.smoothbuild.util.NameBindings;
import org.smoothbuild.util.Throwables;
import org.smoothbuild.util.collect.Nameables;

public class ReferenceResolver extends AstVisitor {
  private final NameBindings<? extends RefableC> nameBindings;
  private final Logger logger;

  public static void resolveReferences(Logger logger, DefsS imported, Ast ast) {
    var scope = scope(imported, ast);
    new ReferenceResolver(scope, logger)
        .visitAst(ast);
  }

  private static NameBindings<RefableC> scope(DefsS imported, Ast ast) {
    var binding = map(imported.topRefables().map(), k -> k, ReferenceResolver::wrap);
    var importedBindings = new NameBindings<RefableC>(binding);
    var ctors = map(ast.structs(), StructP::ctor);
    var refables = ast.topRefables();
    return new NameBindings<>(importedBindings, Nameables.toMap(concat(refables, ctors)));
  }

  private static TopRefableW wrap(TopRefableS topRefableS) {
    return switch (topRefableS) {
      case FuncS funcS -> new FuncW(funcS);
      case ValS valS -> new TopRefableW(valS);
      default -> throw Throwables.unexpectedCaseExc(topRefableS);
    };
  }

  public ReferenceResolver(NameBindings<? extends RefableC> nameBindings, Logger logger) {
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
