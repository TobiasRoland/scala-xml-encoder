package codes.mostly.xml

import codes.mostly.xml.XmlSugar._
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class SetEncodingSpec extends AnyWordSpecLike with XmlMatchers with Matchers {

  "Set encoding" should {

    case class Instrument(name: String, strings: Int)
    implicit val encoder: XmlEncoder[Instrument] = i =>
      <instrument>
        <strings>{i.strings}</strings>
        <name>{i.name.capitalize}</name>
      </instrument>


    "encode empty by not producing any elements'" in {
      val noInstruments: Set[Instrument] = Set()
      val xmlWithSet = <weirdBand>{noInstruments.asXml}</weirdBand>
      xmlWithSet should beLike(<weirdBand></weirdBand>)
    }
    "encode single-element" in {
      val oneInstrument: Set[Instrument] = Set(Instrument("guitar", 12))
      val xmlWithSet =
        <weirdBand>
          {oneInstrument.asXml}
        </weirdBand>
      xmlWithSet should beLike(
        <weirdBand>
          <instrument>
            <strings>12</strings>
            <name>Guitar</name>
          </instrument>
        </weirdBand>
      )
    }
    "encode several elements" in {
      val severalInstruments: Set[Instrument] = Set(
        Instrument("violin", 4),
        Instrument("guitar", 6),
        Instrument("piano", 230),
        Instrument("kick drum", 0),
      )

      val xmlWithSet =
        <weirdBand>
          {severalInstruments.asXml}
        </weirdBand>

      xmlWithSet should beLike(
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
