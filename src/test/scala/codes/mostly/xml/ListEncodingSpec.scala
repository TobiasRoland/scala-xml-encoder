package codes.mostly.xml

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class ListEncodingSpec extends AnyWordSpecLike with XmlMatchers with Matchers {

  import codes.mostly.xml.XmlSugar._

  case class Instrument(name: String, strings: Int)
  implicit val instrumentEncoder: XmlEncoder[Instrument] = i =>
    <instrument>
      <strings>{i.strings}</strings>
      <name>{i.name.capitalize}</name>
    </instrument>


  "List encoding" should {

    "encode empty list by completely omitting xml elements" in {
      val noInstruments: List[Instrument] = List()
      val xmlWithList = <weirdBand>{noInstruments.asXml}</weirdBand>
      xmlWithList should beLike(<weirdBand></weirdBand>)
    }
    "encode single-element" in {
      val oneInstrument: List[Instrument] = List(Instrument("guitar", 12))
      val xmlWithList =
        <weirdBand>
        {oneInstrument.asXml}
        </weirdBand>
      xmlWithList should beLike(
        <weirdBand>
          <instrument>
            <strings>12</strings>
            <name>Guitar</name>
          </instrument>
        </weirdBand>
      )
    }
    "encode several elements" in {
      val severalInstruments: List[Instrument] = List(
        Instrument("violin", 4),
        Instrument("guitar", 6),
        Instrument("piano", 230),
        Instrument("kick drum", 0),
      )

      val xmlWithList =
        <weirdBand>
        {severalInstruments.asXml}
      </weirdBand>

      xmlWithList should beLike(
        <weirdBand>
          <instrument>
            <strings>4</strings>
            <name>Violin</name>
          </instrument>
          <instrument>
            <strings>6</strings>
            <name>Guitar</name>
          </instrument>
          <instrument>
            <strings>230</strings>
            <name>Piano</name>
          </instrument>
          <instrument>
            <strings>0</strings>
            <name>Kick drum</name>
          </instrument>
        </weirdBand>
      )
    }
  }

}
