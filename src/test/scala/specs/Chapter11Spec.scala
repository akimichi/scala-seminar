package test
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter11Spec extends FunSpec with ShouldMatchers with helpers {
  describe("sec 11.1: Identifier"){
    it("識別子にはいろいろな記号が利用できる"){
      val !#%&*+-:/<=>?@\^|~ = """!#%&*+-:/<=>?@\^|~"""
    }
    it("Javaと同じように、Unicodeを識別子に利用できる"){
      val √ = scala.math.sqrt _
      √(2) should equal(1.4142135623730951)
    }
    it("バッククオート内にはどんな文字も利用できる"){
      class `class`
      val `null` = new `class`
    }
  }
  describe("sec 11.2: Infix operators"){
    it("レシーバを指定する . と 適用する引数を指定する () は省略できる"){
      1.to(10) should equal{
        1 to 10
      }
    }
    it("演算子もメソッドとして定義する"){
      class Fraction(val numerator:Int, val denominator:Int) {
        def *(other:Fraction) = new Fraction(numerator * other.numerator, denominator * other.denominator)
        final override def equals(other: Any) = {
          val that = other.asInstanceOf[Fraction]
          if (that == null) false
          else {
            (numerator / denominator) == (that.numerator / that.denominator)
          }
        }
      }
      val a = new Fraction(1,2)
      val b = new Fraction(2,1)
      a.*(b) should equal{
        info("省略することで二項演算子のように記述できる")
        a * b
      }
    }    
  }
  describe("sec 11.3: Unary Operators"){
    class Fraction(val numerator:Int, val denominator:Int) {
      def *(other:Fraction) = new Fraction(numerator * other.numerator, denominator * other.denominator)
      final override def equals(other: Any) = {
        val that = other.asInstanceOf[Fraction]
        if (that == null) false
        else {
          (numerator / denominator) == (that.numerator / that.denominator)
        }
      }
      def reciprocal() = new Fraction(denominator, numerator)
      def unary_!() = reciprocal()
    }
    it("1項演算子は原則として後置演算子として記述できる"){
      1.toString() should equal {
        1 toString
      }
      val a = new Fraction(1,2)
      (a reciprocal) should equal {
        new Fraction(2,1)
      }
    }
    it("いくつかの1項演算子は前置演算子として定義できる"){
      val a = new Fraction(1,2)
      (! a) should equal {
        new Fraction(2,1)
      }
    }
  }
  describe("sec 11.4: Assignment Operators"){
    
    it("a operator= b は a = a operator b と同義である"){
      var a = 1
      a += 1
      a should equal(2)
    }
    info("ただし、<=,>=,!= は比較演算子となる")
  }
  describe("sec 11.5: Precedence"){
    info("演算子の優先順位は、定義された演算子の最初の文字で決まる")
  }
  describe("sec 11.6: Associativity"){
    it("原則として2項演算子は左結合である"){
      (17 - 2 - 9) should equal{
        (17 - 2) -9
      }
    }
    it("例外的に :で終る演算子は右結合となる"){
      (1 :: 2 :: Nil) should equal{
        1 :: (2 :: Nil)
      }
    }
    it("左結合の二項演算子は、2番目の引数に属するメソッドである"){
      (2 :: Nil) should equal{
        Nil.::(2)
      }
    }
  }
  describe("sec 11.7: The apply and update Methods"){
    class Fraction(val numerator:Int, val denominator:Int) {
      def *(other:Fraction) = new Fraction(numerator * other.numerator, denominator * other.denominator)
      final override def equals(other: Any) = {
        val that = other.asInstanceOf[Fraction]
        if (that == null) false
        else {
          (numerator / denominator) == (that.numerator / that.denominator)
        }
      }
      def apply(multitude:Int) = new Fraction(multitude *denominator, multitude *numerator)
      val multitude: Int => Fraction = (mul) => {
        new Fraction(mul * denominator, mul * numerator)
      }
    }
    it("applyを使う"){
      val fraction = new Fraction(1,2)
      fraction(2) should equal {
        fraction.apply(2)
      }
    }
    it("λ式を使う"){
      val fraction = new Fraction(1,2)
      fraction.multitude(2) should equal {
        fraction.apply(2)
      }
    }
    it("updateを使う"){
      val scores = new scala.collection.mutable.HashMap[String,Int]
      scores("Bob") = 100
      scores.update("Tom",200)
      scores("Bob") should equal(100)
      scores("Tom") should equal(200)
    }
    describe("companion object で定義された applyはファクトリーとして利用されることが多い"){
      class Fraction(val numerator:Int, val denominator:Int) {
        def *(other:Fraction) = new Fraction(numerator * other.numerator, denominator * other.denominator)
        final override def equals(other: Any) = {
          val that = other.asInstanceOf[Fraction]
          if (that == null) false
          else {
            (numerator / denominator) == (that.numerator / that.denominator)
          }
        }
      }
      object Fraction {
        def apply(numerator:Int, denominator:Int) = {
          @annotation.tailrec
          def gcd(a: Int, b: Int): Int = {
            b match {
              case 0 => a
              case _ => gcd(b, (a % b))
            }
          }
          val common_devisor:Int = gcd(numerator,denominator)
          new Fraction(numerator / common_devisor, denominator / common_devisor)
        }
      }
      it("apply を使ってインスタンスを生成する"){
        (Fraction(3,4) * Fraction(2,5)) should equal {
          Fraction(3*2,4*5)
        }
        Fraction(10,2) should equal {
          Fraction(5,1)
        }
      }
    }
  }
  describe("sec 11.8: Extractors"){
    class Fraction(val numerator:Int, val denominator:Int) {
      def *(other:Fraction) = new Fraction(numerator * other.numerator, denominator * other.denominator)
      final override def equals(other: Any) = {
        val that = other.asInstanceOf[Fraction]
        if (that == null) false
        else {
          (numerator / denominator) == (that.numerator / that.denominator)
        }
      }
    }
    object Fraction {
      def unapply(input:Fraction): Option[(Int,Int)] = {
        if(input.denominator == 0)
          None
        else
          Some((input.numerator, input.denominator))
      }
      def apply(numerator:Int, denominator:Int) = new Fraction(numerator, denominator)
    }
    it("パターンマッチで抽出子を利用する"){
      Fraction(5,1) match {
        case Fraction(n,d) => {
          n should equal(5)
          d should equal(1)
        }
      }
    }
    it("抽出子のみを定義する"){
      object Name {
        def unapply(input:String): Option[(String,String)] = {
          val position = input.indexOf(" ")
          if(position == -1)
            None
          else
            Some((input.substring(0,position),input.substring(position+1)))
        }
      }
      val Name(first,last) = "Akimichi Tatsukawa"
      first should equal("Akimichi")
      last should equal("Tatsukawa")
    }
    it("case class は抽出子が定義されている"){
      case class Currency(value: Double, unit:String)
      
      val euro = Currency(29.95, "EUR")
      val dollar = Currency(10.0, "USD")
      val yen = Currency(10000.0, "JPY")

      def toS(currency:Currency) = currency match {
        case Currency(amount:Double,"EUR") => {
          "%f EUR".format(amount)
        }
        case Currency(amount,"USD") => {
          "$ %f".format(amount)
        }
        case Currency(amount,"YEN") => {
          "%f ¥".format(amount)
        }
      }
      toS(euro) should equal("29.950000 EUR")
    }
  }
  describe("sec 11.9: Extractors with One or No Arguments"){
    it("抽出子が一つの引数を持つ場合"){
      object Number {
        def unapply(input:String) : Option[Int] = {
          try {
            Some(Integer.parseInt(input.trim))
          } catch {
            case ex: NumberFormatException => None
          }
        }
      }
      val Number(n) = "1729"
      n should equal(1729)
    }
    it("抽出子が引数を持たない場合"){
      object IsCompound {
        def unapply(input:String):Boolean = input.contains(" ")
      }
      object Name {
        def unapply(input:String): Option[(String,String)] = {
          val position = input.indexOf(" ")
          if(position == -1)
            None
          else
            Some((input.substring(0,position),input.substring(position+1)))
        }
      }
      def extract_lastname(name:String) = {
        name match {
          case Name(first, last @ IsCompound()) => {
            last
          }
          case Name(first, last) => last
        }
      }
      extract_lastname("Akimichi Tatsukawa") should equal("Tatsukawa")
      extract_lastname("Peter van der Linden") should equal("van der Linden")
    }
  }
  describe("sec 11.10: The unapplySeq Method"){
    object Name {
      def unapplySeq(input:String): Option[Seq[String]] = {
        if(input.trim == "")
          None
        else
          Some(input.trim.split("\\s+"))
      }
    }
    def extract_lastname(name:String) = {
      name match {
        case Name(first, last) => last
        case Name(first, middle, last) => last
        case Name(first, "van","der", last) => last
      }
    }
    extract_lastname("Akimichi Tatsukawa") should equal("Tatsukawa")
    extract_lastname("Peter van der Linden") should equal("Linden")
  }
}
