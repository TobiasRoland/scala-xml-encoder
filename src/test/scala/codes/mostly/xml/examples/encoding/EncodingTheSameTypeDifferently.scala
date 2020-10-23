package codes.mostly.xml.examples.encoding

import java.time.format.DateTimeFormatter
import java.time.{Instant, LocalDateTime, ZoneOffset}

import codes.mostly.xml.XmlMatchers
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpecLike


class EncodingTheSameTypeDifferently extends AnyWordSpecLike with XmlMatchers with Matchers {

  import codes.mostly.xml.XmlSugar._

  val instantForHumans: XmlEncoder[Instant] = i => {
    val time = LocalDateTime.ofEpochSecond(i.toEpochMilli, 0, ZoneOffset.UTC)
    val formattedTime = time.format(DateTimeFormatter.ISO_LOCAL_DATE)
    <humanFriendly>{formattedTime}</humanFriendly>
  }
  val instantForMachines: XmlEncoder[Instant] = i => <timestamp>{i.toEpochMilli.toString}ms</timestamp>
  val instantAsHoursSinceEpoch: XmlEncoder[Instant] = i => <timeSinceEpoch>{(i.toEpochMilli / 60 / 60).toString} hours</timeSinceEpoch>

  case class Event(description: String, timestamp: Option[Instant])



  "Encoding the same value in different ways" should {
    "be doable" in {
      implicit val eventEncoder: XmlEncoder[Event] = event =>
        <event>
          <description>{event.description}</description>
          {implicit val e = instantForHumans; event.timestamp.asXml}
          {implicit val e = instantForMachines; event.timestamp.asXml}
          {implicit val e = instantAsHoursSinceEpoch; event.timestamp.asXml}
        </event>

      val eventWithTimestamp    = Event("Some event occurred", Some(Instant.ofEpochMilli(220022002L)))
      val eventWithoutTimestamp = Event("Some event without a timestamp occurred", None)

      eventWithTimestamp.asTopLevelXml match {
        case Left(err)  => fail(err)
        case Right(xml) => xml should beLike(
          <event>
            <description>Some event occurred</description>
            <humanFriendly>1976-12-21</humanFriendly>
            <timestamp>220022002ms</timestamp>
            <timeSinceEpoch>61117 hours</timeSinceEpoch>
          </event>
        )

          eventWithoutTimestamp.asTopLevelXml match {
            case Left(err)  => fail(err)
            case Right(xml) => xml should beLike(
              <event>
                <description>Some event without a timestamp occurred</description>
              </event>
            )

          }
      }

    }
  }




}
