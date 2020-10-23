package codes.mostly.xml.examples.encoding

import codes.mostly.xml.XmlMatchers
import org.scalatest.flatspec.AnyFlatSpecLike
import org.scalatest.matchers.should.Matchers

/**
 * The basic example of usage - see also `EncodingAnEntireHierarchy` for a bigger example
 * that also includes some examples of using collections.
 */
class EncodingCaseClass extends AnyFlatSpecLike with XmlMatchers with Matchers {

  import codes.mostly.xml.XmlSugar._

  "XML encoding a case class" should "encode when an implicit XmlEncoder is in scope" in {
    case class Person(name: String, age: Int)
    implicit val encoder: XmlEncoder[Person] = person =>
      <person>
        <name>{person.name}</name>
        <age>{person.age}</age>
      </person>

    val person = Person("Tobias", 29)

    person.asTopLevelXml match {
      case Left(error) => fail(error)
      case Right(element) => element should beLike(
        <person>
          <name>Tobias</name>
          <age>29</age>
        </person>
      )
    }
  }

  "Multiple implicit coders in combination" should "encode correctly when using .asXml for the 'nested' types" in {
    case class Friend(name: String, age: Int)
    case class Pet(name: String, legs: Int)
    case class Person(name: String, profession: String, pet: Pet, child: Friend)

    val person = Person("Tobias", "coder", Pet("Caligula", 0), Friend("Artemis", 30))

    implicit val petEncoder: XmlEncoder[Pet] = pet =>
      <pet>
        <name>{pet.name}</name>
        <legs>
          <legCount>{pet.legs}</legCount>
        </legs>
      </pet>
    implicit val childEncoder: XmlEncoder[Friend] = child =>
      <friend>
        <name>{child.name}</name>
        <age>{child.age}</age>
      </friend>

    implicit val personEncoder: XmlEncoder[Person] = person => <person>
      <name>{person.name}</name>
      <job>{person.profession.toUpperCase}</job>
      {person.pet.asXml}
      {person.child.asXml}
    </person>

    person.asTopLevelXml match {
      case Left(err)      => fail(err)
      case Right(element) => element should beLike(
        <person>
          <name>Tobias</name>
          <job>CODER</job>
          <pet>
            <name>Caligula</name>
            <legs>
              <legCount>0</legCount>
            </legs>
          </pet>
          <friend>
            <name>Artemis</name>
            <age>30</age>
          </friend>
        </person>
      )
    }
  }
}
