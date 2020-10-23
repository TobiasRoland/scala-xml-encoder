package codes.mostly.xml

import org.scalatest.matchers.{MatchResult, Matcher}

import scala.xml.{Node, PrettyPrinter}

/**
 * Custom test mixin for XML verification
 */
trait XmlMatchers {


  /**
   * Usage:
   *
   * {{{
   * val myXml: Elem // Or Node, but for the most part Elem
   * myXml should beLike(<expected>expectedContent</expected>
   * }}}
   *
   * This method validates the XML is equivalent by
   * printing both XML elements in the same format and comparing
   * those two outputs. This avoids many "quirks" of xml comparison,
   * such as empty xml elements such as {{{</NoStartingElement>}}}, whitespace etc.
   *
   * Because of this, if you have a _really_ strict XML schema this might not be the method
   * for you. But for the vast majority of cases, this makes
   * testing xml a lot easier.
   */
  def beLike(expected: Node): Matcher[Node] = (actual: Node) => {
    val prettyPrinter     = new PrettyPrinter(150, 4)
    val formattedExpected = prettyPrinter.format(expected)
    val formattedActual   = prettyPrinter.format(actual)
    MatchResult.apply(
      formattedExpected equals formattedActual,
      s"Actual XML did not equal expected XML!\n\nExpected:\n[$formattedExpected]\n\nActual:\n[$formattedActual]\n\n",
      "Actual did equal expected XML"
    )
  }
}

