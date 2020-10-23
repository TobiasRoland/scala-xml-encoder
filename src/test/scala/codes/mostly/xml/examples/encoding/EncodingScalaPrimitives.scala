package codes.mostly.xml.examples.encoding

import codes.mostly.xml.XmlMatchers
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class EncodingScalaPrimitives extends AnyWordSpecLike with XmlMatchers with Matchers {

  import codes.mostly.xml.XmlSugar._

  "Encoding basic scala types such as string" should {
      "can be simple when you just need to put a value from an attribute" in {
        case class Person(name: String)
        implicit val encoder: XmlEncoder[Person] = p =>
          <person>
            <name>{p.name}</name>
          </person>
        Person("Tobias Roland")
      }

    val complexBehaviourStringEncoder: XmlEncoder[String] = str => str.trim.split(" ").toList match {
      case Nil                            => <nameless/>
      case "" :: Nil                      => <nameless/>
      case firstName :: Nil               => <name><first>{firstName}</first></name>
      case firstName :: familyName :: Nil =>
        <name>
          <first>{firstName}</first>
          <second>{familyName}</second>
        </name>
      case _ => <complexName>str</complexName>
    }

      "can be done implicitly" in {
        implicit val namEnc = complexBehaviourStringEncoder
        "Tobias Roland".asTopLevelXml match {
          case Left(err) => fail(err)
          case Right(xml) => xml should beLike(
              <name>
                <first>Tobias</first>
                <second>Roland</second>
              </name>
          )
        }
      }
      "can be done explicitly" in {
        "Tobias Roland".asTopLevelXml(complexBehaviourStringEncoder) match {
          case Left(err) => fail(err)
          case Right(xml) => xml should beLike(
              <name>
                <first>Tobias</first>
                <second>Roland</second>
              </name>
          )
        }
      }
      "can be done explicitly when wrapped in a collection (but it's a bit ugly)" in {
        <person>
          {Option("Tobias Roland").asXml(encodeOpt(complexBehaviourStringEncoder))}
        </person> should beLike(
          <person>
              <name>
                <first>Tobias</first>
              <second>Roland</second>
            </name>
          </person>
          )
        }

      "can be done implicitly when wrapped in a collection (I quite like this approach)" in {
        <person>
          {implicit val e = complexBehaviourStringEncoder; Option("Tobias Roland").asXml}
        </person> should beLike(
            <person>
              <name>
                <first>Tobias</first>
                <second>Roland</second>
              </name>
            </person>
          )
        }


      "can be done with multiple encoders" in {
        val nameEncoder: XmlEncoder[String] = name => <name>{name}</name>
        val jobEncoder: XmlEncoder[String] = name => <job>{name}</job>
        case class Person(name: Option[String], job: Option[String])

        // This `{implicit val e = thingEncoder; thing.asXml}` pattern is my preferred approach,
        // but if you like to do it explicitly that's cool too.
        implicit val encoder: XmlEncoder[Person] = p =>
          <person>
            {implicit val e = nameEncoder; p.name.asXml}
            {implicit val e = jobEncoder; p.job.asXml}
          </person>

        Person(None,None).asTopLevelXml match {
          case Left(err) => fail(err)
          case Right(xml) => xml should beLike(
            <person>
            </person>
          )
        }
      }
    }
}
