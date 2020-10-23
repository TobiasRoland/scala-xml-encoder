package codes.mostly.xml

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike
class OptionEncodingSpec extends AnyWordSpecLike with XmlMatchers with Matchers {

  import codes.mostly.xml.XmlSugar._

  "Option encoding" should {

    case class Instrument(name: String, strings: Int)

    implicit val encoder: XmlEncoder[Instrument] = i =>
      <instrument>
        <strings>{i.strings}</strings>
        <name>{i.name.capitalize}</name>
      </instrument>

    "encode empty option by not producing anything" in {
      val maybeInstrument: Option[Instrument] = None
      val xmlWhenOptionIsEmpty = <instruments>{maybeInstrument.asXml}</instruments>
      xmlWhenOptionIsEmpty should beLike(<instruments></instruments>)
    }

    "encode present option as an xml element" in {
      val maybeInstrument: Option[Instrument] = Some(Instrument("guitar", 6))
      val xmlWhenOptionIsEmpty = <instruments>{maybeInstrument.asXml}</instruments>
      xmlWhenOptionIsEmpty should beLike(
        <instruments>
          <instrument>
            <strings>6</strings>
            <name>Guitar</name>
          </instrument>
        </instruments>
      )
    }
  }

}
