package codes.mostly.xml.examples.encoding

import codes.mostly.xml.XmlMatchers
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class EncodingAnEntireHierarchy extends AnyWordSpecLike with XmlMatchers with Matchers {

  import codes.mostly.xml.XmlSugar._

  sealed trait PetKind
  case object Dog extends PetKind
  case object Cat extends PetKind
  case object Parrot extends PetKind

  case class Pet(nickname: String, kind: PetKind)
  case class Job(title: String, allowsPetsInTheOffice: Boolean)
  case class Hobby(title: String, expensive: Boolean)
  case class QualifiedName(honorificPrefix: String, name: String)
  case class Person(name: QualifiedName, occupation: Option[Job], pets: List[Pet], hobby: Option[Hobby])
  case class People(individuals: Set[Person])


  "Encoding a full hierarchy with common data structures " +
    "(options and basic collections, just have a look at the case classes and you'll get it)" in {

    implicit val petKindEncoder: XmlEncoder[PetKind] = {
      case Dog => <doggo/>
      case Cat => <cat/>
      case Parrot => <bird/>
    }
    implicit val petEncoder: XmlEncoder[Pet] = p =>
      <pet>
        <nickname>{p.nickname}</nickname>
        {p.kind.asXml}
      </pet>

    implicit val jobEncoder: XmlEncoder[Job] = j =>   <occupation title={j.title} allowsPets={j.allowsPetsInTheOffice.toString}/>
    implicit val hobbyEncoder: XmlEncoder[Hobby] = h =>
      <hobby>
        <hobbyName>{h.title}</hobbyName>
        <pricey>{h.expensive.toString}</pricey>
      </hobby>

    implicit val nameEncoder: XmlEncoder[QualifiedName] = qn => <name>{qn.honorificPrefix} {qn.name}</name>

    implicit val personEncoder: XmlEncoder[Person] = p =>
      <person>
        {p.name.asXml}
        {p.hobby.asXml}
        {p.occupation.asXml}
        <pets>{p.pets.asXml}</pets>
      </person>

    implicit val peopleEncoder: XmlEncoder[People] = p =>
      <people count={p.individuals.size.toString}>{p.individuals.asXml}</people>

    val person = Person(QualifiedName("Mr.", "Tobias"), None, Nil, None)
    val person2 = Person(QualifiedName("Rev.", "Saibot"), None, Nil, None)
    val person3 = Person(QualifiedName("Mx.", "Otbias"), None, Nil, None)
    val developer = Person(
      name = QualifiedName("Señor", "Toby"),
      occupation = Some(Job("Software Developer", allowsPetsInTheOffice = false)),
      pets = Pet("Astrid", Cat) :: Pet("Whitman", Dog) :: Nil,
      hobby = Some(Hobby("Drummer", expensive = true)) )
    val pirate = Person(
      name = QualifiedName("Captain", "Roland"),
      occupation = Some(Job("Pirate", allowsPetsInTheOffice = true)),
      pets = Pet("Pops", Parrot) :: Nil,
      hobby = Some(Hobby("Singing shanties", expensive = false))
    )

    People(Set(person, person2, person3, developer, pirate)).asTopLevelXml match {
      case Left(err) => fail(err)
      case Right(xml) => xml should beLike(
        <people count="5">
          <person>
            <name>Captain Roland</name>
            <hobby>
              <hobbyName>Singing shanties</hobbyName>
              <pricey>false</pricey>
            </hobby>
            <occupation title="Pirate" allowsPets="true"/>
            <pets>
              <pet>
                <nickname>Pops</nickname>
                <bird/>
              </pet>
            </pets>
          </person>
          <person>
            <name>Mx. Otbias</name>
            <pets></pets>
          </person>
          <person>
            <name>Rev. Saibot</name>
            <pets></pets>
          </person>
          <person>
            <name>Señor Toby</name>
            <hobby>
              <hobbyName>Drummer</hobbyName>
              <pricey>true</pricey>
            </hobby>
            <occupation title="Software Developer" allowsPets="false"/>
            <pets>
              <pet>
                <nickname>Astrid</nickname>
                <cat/>
              </pet>
              <pet>
                <nickname>Whitman</nickname>
                <doggo/>
              </pet>
            </pets>
          </person>
          <person>
            <name>Mr. Tobias</name>
            <pets></pets>
          </person>
        </people>
      )
    }
  }
}
