package codes.mostly.xml.examples.encoding

import codes.mostly.xml.XmlMatchers
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

/**
 * It should be easy to encode nested collections
 */
class EncodingCollections extends AnyWordSpecLike with XmlMatchers with Matchers {

  import codes.mostly.xml.XmlSugar._

  case class Employee(name: String, hasProvidedTaxCode: Boolean)
  case class TerribleAccountingSoftware(employees: Option[List[Set[Seq[Option[List[Employee]]]]]]) // Hopefully a contrived example

  implicit val developerEncoder: XmlEncoder[Employee] = developer =>
    <emp>
      <name>{developer.name}</name>
      <isPayingTax>{developer.hasProvidedTaxCode.toString}</isPayingTax>
    </emp>

  implicit val accountingSoftwareEncoder: XmlEncoder[TerribleAccountingSoftware] = software =>
    <accountingSofware3000>
      <employees>{software.employees.asXml}</employees>
    </accountingSofware3000>

  "Encoding combinations of collections should require no effort" in {
    val accountingSoftwareWithEmployees = TerribleAccountingSoftware(
      employees = Some(
        List(
          Set(),
          Set(Seq()),
          Set(Seq(None)),
          Set(Seq(Some(List()))),
          Set(Seq(), Seq(Some(List(Employee("Tobias Roland", hasProvidedTaxCode = true))))),
          Set(),
          Set(Seq()),
          Set(Seq(None)),
          Set(Seq(Some(List()))),
          Set(Seq(), Seq(Some(List(Employee("Dnalor Saibot", hasProvidedTaxCode = false), Employee("Roland Tobias", hasProvidedTaxCode = true)))))))
      )

    accountingSoftwareWithEmployees.asTopLevelXml match {
      case Left(err)  => fail(err)
      case Right(xml) => xml should beLike(
        <accountingSofware3000>
          <employees>
            <emp>
              <name>Tobias Roland</name>
              <isPayingTax>true</isPayingTax>
            </emp>
            <emp>
              <name>Dnalor Saibot</name>
              <isPayingTax>false</isPayingTax>
            </emp>
            <emp>
              <name>Roland Tobias</name>
              <isPayingTax>true</isPayingTax>
            </emp>
          </employees>
        </accountingSofware3000>
      )
    }

    TerribleAccountingSoftware(None).asTopLevelXml match {
      case Left(err) => fail(err)
      case Right(xml) => xml should beLike(
        <accountingSofware3000>
          <employees></employees>
        </accountingSofware3000>
      )
    }
  }

}
