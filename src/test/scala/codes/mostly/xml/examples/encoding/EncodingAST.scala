package codes.mostly.xml.examples.encoding

import codes.mostly.xml.XmlMatchers
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

/**
 * An simple example of encoding an Abstract Syntax Tree
 */
class EncodingAST extends AnyWordSpecLike with XmlMatchers with Matchers {

  import codes.mostly.xml.XmlSugar._

  sealed trait Fruit
  case object Apple extends Fruit
  case class Mango(ripe: Boolean) extends Fruit
  case class Pear(color: String) extends Fruit

  implicit val encodeFruit: XmlEncoder[Fruit] = { // an exhaustive match
    case Apple        => <fruit name="apple"/>
    case Mango(true)  => <fruit name="mango"/>
    case Mango(false) => <fruit name="mango"><readyToEat/></fruit>
    case Pear(color)  => <fruit name="pear"><color>{color.toUpperCase}</color></fruit>
  }

  val apple: Apple.type = Apple
  val ripeMango: Mango = Mango(true)
  val unripeMango: Mango = Mango(false)
  val redPear: Pear = Pear("red")

  "Encoding an AST via an encoder for the super-type requires informing scala of the supertype" in {
    (apple: Fruit).asTopLevelXml match {
      case Left(err) => fail(err)
      case Right(xml) => xml should beLike(<fruit name="apple"/>)
    }
    (ripeMango: Fruit).asTopLevelXml match {
      case Left(err) => fail(err)
      case Right(xml) => xml should beLike(<fruit name="mango"/>)
    }
    (unripeMango: Fruit).asTopLevelXml match {
      case Left(err) => fail(err)
      case Right(xml) => xml should beLike(<fruit name="mango"><readyToEat/></fruit>)
    }
    (redPear: Fruit).asTopLevelXml match {
      case Left(err) => fail(err)
      case Right(xml) => xml should beLike(<fruit name="pear"><color>RED</color></fruit>)
    }
  }

  "Encoding when subclasses are in collections" in {
    <fruits>
      {Seq[Fruit](apple, ripeMango, unripeMango, redPear).asXml}
    </fruits> should beLike(
      <fruits>
        <fruit name="apple"/>
        <fruit name="mango"/>
        <fruit name="mango">
          <readyToEat/>
        </fruit>
        <fruit name="pear">
          <color>RED</color>
        </fruit>
      </fruits>
    )

  }


}
