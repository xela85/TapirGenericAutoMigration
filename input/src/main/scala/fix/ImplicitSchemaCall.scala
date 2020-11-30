/*
rule = GenericAuto
*/
package fix

import sttp.tapir.Schema
import sttp.tapir.generic.Derived

object ImplicitSchemaCall {

  case class Something(value: String)
  private val schema = implicitly[Derived[Schema[Something]]].value

}