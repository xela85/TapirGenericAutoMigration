package fix

import sttp.tapir.Schema
import sttp.tapir.generic.Derived
import sttp.tapir.generic.auto._

object ImplicitSchemaCall {

  case class Something(value: String)
  private val schema = implicitly[Derived[Schema[Something]]].value

}