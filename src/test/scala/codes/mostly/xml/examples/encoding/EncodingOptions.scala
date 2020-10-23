package codes.mostly.xml.examples.encoding

import codes.mostly.xml.XmlMatchers
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class EncodingOptions extends AnyWordSpecLike with XmlMatchers with Matchers {

  import codes.mostly.xml.XmlSugar._

  "Encoding case classes with other encodeable case classes as optional fields" should {

    case class Dog(name: String, playful: Boolean)
    case class Person(name: String, pet: Option[Dog])

    implicit val dogEncoder: XmlEncoder[Dog] = d => <dog name={d.name} isPlayful={d.playful.toString}/>
    implicit val personEncoder: XmlEncoder[Person] = p => <person name={p.name}><pets>{p.pet.asXml}</pets></person>

    "encode" when {
      "optional value is empty" in {
        val personWithoutPet = Person("Tobias", None)
        personWithoutPet.asTopLevelXml match {
          case Left(err) => fail(err)
          case Right(personXml) => personXml should beLike(
            <person name="Tobias">
              <pets></pets>
            </person>
          )
        }
      }
      "optional value is present" in {
        val personWithPet = Person("Ezra", Some(Dog("Whitman", playful = true)))
        personWithPet.asTopLevelXml match {
          case Left(err) => fail(err)
          case Right(personXml) => personXml should beLike(
            <person name="Ezra">
              <pets>
                <dog name="Whitman" isPlayful="true"/>
              </pets>
            </person>
          )
        }
      }
    }

  }
}
