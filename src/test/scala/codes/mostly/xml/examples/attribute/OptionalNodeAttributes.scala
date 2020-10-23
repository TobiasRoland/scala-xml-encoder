package codes.mostly.xml.examples.attribute

import codes.mostly.xml.XmlMatchers
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

/**
 * I found this to be a useful addition for encoding XML with optional attributes on the nodes.
 */
class OptionalNodeAttributes extends AnyWordSpecLike with XmlMatchers with Matchers {

  import codes.mostly.xml.XmlSugar._

  "Optional attributes" should {


    "be present when value is present" in {
      val email: Option[String] = Some("me@example.com")
      val name: Option[String] = Some("Tobias Roland")
      val age: Option[Int] = Some(29)

      val userXml = <user>{name.getOrElse("")}</user>
        .optionalAttribute("email", email)
        .optionalAttribute("age", age.map(_.toString))

      userXml should beLike(<user age="29" email="me@example.com">Tobias Roland</user>)
    }

    "be omitted entirely when no values" in {
      val email: Option[String] = None
      val name: Option[String] = None
      val age: Option[Int] = None

      val userXml = <user>{name.getOrElse("")}</user>
        .optionalAttribute("email", email)
        .optionalAttribute("age", age.map(_.toString))

      userXml should beLike(<user></user>)
    }


  }
}
