package codes.mostly.xml

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

class SeqEncodingSpec extends AnyWordSpecLike with XmlMatchers with Matchers {

  "Seq encoding" should {

    import codes.mostly.xml.XmlSugar._

    case class Instrument(name: String, strings: Int)

    implicit val encoder: XmlEncoder[Instrument] = i =>
      <instrument>
        <strings>{i.strings}</strings>
        <name>{i.name.capitalize}</name>
      </instrument>

    "encode empty by not producing any elements" in {
      val noInstruments: Seq[Instrument] = Seq()
      val xmlWithSeq = <weirdBand>{noInstruments.asXml}</weirdBand>
      xmlWithSeq should beLike(<weirdBand></weirdBand>)
    }
    "encode single-element" in {
      val oneInstrument: Seq[Instrument] = Seq(Instrument("guitar", 12))
      val xmlWithSeq =
        <weirdBand>
          {oneInstrument.asXml}
        </weirdBand>
      xmlWithSeq should beLike(
        <weirdBand>
          <instrument>
            <strings>12</strings>
            <name>Guitar</name>
          </instrument>
        </weirdBand>
      )
    }
    "encode several" in {
      val severalInstruments: Seq[Instrument] = Seq(
        Instrument("violin", 4),
        Instrument("guitar", 6),
        Instrument("piano", 230),
        Instrument("kick drum", 0),
      )

      val xmlWithSeq =
        <weirdBand>
          {severalInstruments.asXml}
        </weirdBand>

      xmlWithSeq should beLike(
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
