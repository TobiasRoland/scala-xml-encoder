package codes.mostly.xml

import scala.xml.{Attribute, Elem, Null, Text}

private[xml] sealed trait XmlSugar {
  def asElems: List[Elem] = this match {
    case XmlAbsent => Nil
    case XmlElem(a) => List(a)
    case XmlElems(as) => as.flatMap(_.asElems)
  }
}

/** When no XML needs to be produced
 */
private[xml] case object XmlAbsent extends XmlSugar

/** When a single XML element needs to be produced
 */
private[xml] final case class XmlElem(a: Elem) extends XmlSugar

/** When some amount of XML elements (or none) needs to be produced
 */
private[xml] final case class XmlElems(as: List[XmlSugar]) extends XmlSugar

object XmlSugar {
  def from(elems: Seq[Elem]): XmlSugar = elems.toList match {
    case Nil => XmlAbsent
    case x :: Nil => XmlElem(x)
    case xs => XmlElems(xs.map(x => from(x :: Nil)))
  }

  /** Automatically convert a single scala XML elem type to the AST
   */
  implicit val elemToXml: Elem => XmlSugar = a => from(a :: Nil)

  /** Automatically convert several (or no) XML elem types to the AST
   */
  implicit val elemSeqToXml: Seq[Elem] => XmlSugar = seq => from(seq)

  /** Automatically convert a list of XML elems to the XmlElems AST type
   */
  implicit val listOfXmlAsXml: List[XmlSugar] => XmlElems = XmlElems
  implicit val maybeOfXmlAsXml: Option[XmlSugar] => XmlSugar = _.getOrElse(XmlAbsent)

  /** Encoder for XML AST
   *
   * @tparam A the type to encode
   */
  trait XmlEncoder[A] {
    def apply(a: A): XmlSugar
  }

  /** Obj. for xml enoding
   */
  object XmlEncoder {
    def instance[A](f: A => XmlSugar): XmlEncoder[A] = f(_)

    def apply[A: XmlEncoder]: XmlEncoder[A] = implicitly[XmlEncoder[A]]
  }

  /*
   * Implicit encoders for a handful of common types. Since implicit,
   * they _will_ combine to encode - e.g. `val x = List[Seq[Option[NEL[A]]]]; x.asXml` should compile just fine
   * if you provide just a `XmlEncoder[A]`
   *
   * If you're using Cats types in your project, consider defining:
   *
   * implicit def encodeNEL[A](implicit aEnc: XmlEncoder[A]): XmlEncoder[NonEmptyList[A]] = nel => nel.toList.toXml
   * implicit def encodeNES[A](implicit aEnc: XmlEncoder[A]): XmlEncoder[NonEmptySet[A]]  = nes => nes.toNonEmptyList.toXml
   *
   */
  implicit def encodeOpt[A](implicit aEnc: XmlEncoder[A]): XmlEncoder[Option[A]] = opt => opt.map(_.toXml)

  implicit def encodeList[A](implicit aEnc: XmlEncoder[A]): XmlEncoder[List[A]] = lst => lst.map(_.toXml)

  implicit def encodeSet[A](implicit aEnc: XmlEncoder[A]): XmlEncoder[Set[A]] = set => set.toList.toXml

  implicit def encodeSeq[A](implicit aEnc: XmlEncoder[A]): XmlEncoder[Seq[A]] = seq => seq.toList.toXml

  /** Enrich `A` with syntax to allow easy encoding of elements to scala XML, assuming implicit encoder for A
   * is available in scope
   *
   * @param a encoding subject
   * @tparam A the type to encode
   */
  implicit class richA[A](a: A) {

    /** Lift A into the XML AST (only meant to be used directly from this package object)
     *
     * @param aEnc encoder for A
     * @return XML ast for `a`
     */
    private[xml] def toXml(implicit aEnc: XmlEncoder[A]): XmlSugar = aEnc.apply(a)

    /** Convert into scala XML types
     */
    def asXml(implicit aEnc: XmlEncoder[A]): List[Elem] = a.toXml.asElems

    /** Use this method when you need to produce xml with one-and-only-one element on the level
     *
     * @param aEnc encoder for A
     * @return
     */
    def asTopLevelXml(implicit aEnc: XmlEncoder[A]): Either[String, Elem] = a.asXml match {
      case Nil => Left("No XML produced")
      case elem :: Nil => Right(elem)
      case elems => Left(s"More than one elem was produced; full list was: $elems")
    }
  }

  /** Enrich `Option[String]` to allow making attributes for Xml elements a lot easier
   *
   * @param maybe the value that, when present, will become an attribute
   */
  implicit class attributableOption(maybe: Option[String]) {

    /** Convert content into (text) value of attribute named by key param
     *
     * @param key name of the attribute
     * @return attribute or empty
     */
    def asAttribute(key: String): Option[Attribute] = maybe.map(str => Attribute(None, key, Text(str), Null))
  }

  /** Enrich the scala `Elem` type
   */
  implicit class richElem(elem: Elem) {

    /** Add optional attribute to elem (omit entirely if not present)
     */
    def optionalAttribute(key: String, content: Option[String]): Elem = content
      .map(str => Attribute(None, key, Text(str), Null))
      .fold(elem)(a => elem % a)
  }
}
