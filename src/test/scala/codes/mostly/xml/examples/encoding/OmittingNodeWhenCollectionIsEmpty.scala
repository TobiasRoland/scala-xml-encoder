package codes.mostly.xml.examples.encoding

import codes.mostly.xml.XmlMatchers
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike

/**
 * You could end up in a situation where you don't want to output XML at all
 */
class OmittingNodeWhenCollectionIsEmpty extends AnyWordSpecLike with XmlMatchers with Matchers {

  import codes.mostly.xml.XmlSugar._

  case class Friend(name: String)
  case class Person(name: String, friends: List[Friend])

  implicit val friendEncoder: XmlEncoder[Friend] = f => <friend>{f.name}</friend>

  val personWithFriends: Person   = Person("Tobias", List(Friend("Emma"), Friend("Jaimie")))
  val personWithOneFriend: Person = Person("Dnalor", List(Friend("Ezra")))
  val lonelyPerson: Person        = Person("Saibot", List())


  "Omitting an xml node" should {
    "be possible by using a local variable and `Nil` (since this utility basically just provides a wrapper over List[Elem])" in {
      implicit val encoderThatOmitsEmptyFriendList: XmlEncoder[Person] = p => {
        val friendList = p.friends match {
          case Nil               => Nil
          case onlyFriend :: Nil => onlyFriend.asXml
          case friends           => <friends>{friends.asXml}</friends>
        }
        <person name={p.name}>{friendList}</person>
      }

      lonelyPerson.asTopLevelXml match {
        case Left(err) => fail(err)
        case Right(xml) => xml should beLike(<person name="Saibot"></person>)
      }

      personWithOneFriend.asTopLevelXml match {
        case Left(err) => fail(err)
        case Right(xml) => xml should beLike(<person name="Dnalor"><friend>Ezra</friend></person>)
      }

      personWithFriends.asTopLevelXml match {
        case Left(err)  => fail(err)
        case Right(xml) => xml should beLike(
          <person name="Tobias">
            <friends>
              <friend>Emma</friend>
              <friend>Jaimie</friend>
            </friends>
          </person>
        )
      }
    }
  }
}
