# Xml Sugar: A scala-xml Encoder 
A tiny micro-library for encoding [scala-xml](https://github.com/scala/scala-xml) with zero dependencies (outside of scala-xml itself)

# How do I use this to produce XML
Import it

```scala
import codes.mostly.xml.XmlSugar._
```

Define your encoders:

```scala
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
```

And produce XML:

```scala
myAccountingSoftware.asTopLevelXml match {
    Left(error: String)         => TerribleAccountingSoftware(...) // in case you goofed and didn't produce EXACTLY one xml element
    Right(elem: scala.xml.Elem) => ??? // here's your valid XML element
}
```

That's it!

# Usage examples

* [Encode a basic case class](src/test/scala/codes/mostly/xml/examples/encoding/EncodingCaseClass.scala)
* [Encode a more complex case class (with nested case classes and collections)](src/test/scala/codes/mostly/xml/examples/encoding/EncodingAnEntireHierarchy.scala)
* [Encode an AST / a type hierarchy](src/test/scala/codes/mostly/xml/examples/encoding/EncodingAST.scala)
* [Encode Collections (List, Set, Seq, Option)](src/test/scala/codes/mostly/xml/examples/encoding/EncodingCollections.scala)
* [Encode Option](src/test/scala/codes/mostly/xml/examples/encoding/EncodingOptions.scala)
* [Encode Scala Primitives (String, Int, Long and such)](src/test/scala/codes/mostly/xml/examples/encoding/EncodingScalaPrimitives.scala)
* [Encode the same type in multiple different ways](src/test/scala/codes/mostly/xml/examples/encoding/EncodingTheSameTypeDifferently.scala)
* [Encode while having to omit an XML element](src/test/scala/codes/mostly/xml/examples/encoding/OmittingNodeWhenCollectionIsEmpty.scala)
* [Adding an optional attribute to an XML Element](src/test/scala/codes/mostly/xml/examples/attribute/OptionalNodeAttributes.scala)

# What's the design idea here
To make writing XML more fun and less annoying. It's using implicit encoders which
is inspired by `circe`, but a little simpler and a lot less grand in scope.

The general idea is:

1. Put implicit XML encoders for your models into scope
2. Once you have that, define your XML using standard scala-xml syntax `.asXml` to encode the child types.
3. Empty collections means no XML elements are produced
  
You can see the intended usage in this example

# I use Cats, are there encoders for [name_of_thing_here]
I like cats too, but I didn't want to add more dependencies to this micro library.

In general, if you need to add encoders for any structural types, the easiest
thing is to convert them to a list and call `.asXml` on them. If you for instance wanted
NonEmptyList and NonEmptySet encoders, you could do:

```scala
import codes.mostly.xml.XmlSugar._

implicit def encodeNEL[A](implicit aEnc: XmlEncoder[A]): XmlEncoder[NonEmptyList[A]] = nel => nel.toList.toXml
implicit def encodeNES[A](implicit aEnc: XmlEncoder[A]): XmlEncoder[NonEmptySet[A]]  = nes => nes.toNonEmptyList.toXml
```

And now you can encode to your heart's content:

```scala
case class Person(name: String, hobbies: NonEmptySet[Hobby], jobs: NonEmptyList[Job])
implicit val personEncoder: XmlEncoder[Person] = p => 
    <person name={p.name}>
       {p.hobbies.asXml}  <!-- assuming an XmlEncoder[Hobby] exists in scope -->
       {p.jobs.asXml}     <!-- assuming an XmlEncoder[Job] exists in scope -->
    </person>
```

# Limitations of this library
- No de-coding of XML
- Automatically deriving XML based on your case classes is out of scope for this project.
- It's assumed your encoders are always deterministic - this won't catch any errors thrown or raised during encoding.
