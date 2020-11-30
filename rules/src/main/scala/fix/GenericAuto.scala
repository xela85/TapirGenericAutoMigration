package fix

import scalafix.v1._
import scala.meta._

class GenericAuto extends SemanticRule("GenericAuto") {

  override def fix(implicit doc: SemanticDocument): Patch = {
    val tapirSchemaOrValidatorUsage = doc.tree.collect {
      case Term.ApplyType(Term.Name("jsonBody"), _) => ()
      case Term.ApplyType(Term.Name("implicitly"), List(Type.Apply(Type.Name("Derived"), List(Type.Apply(Type.Name("Schema"), _))))) => ()
    }

    if(tapirSchemaOrValidatorUsage.nonEmpty) Patch.addGlobalImport(importer"sttp.tapir.generic.auto._")
    else Patch.empty
  }

}
