import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Chapter02Spec extends FunSpec with ShouldMatchers with helpers {
  describe("Chap 2."){
    info("本章では、副作用をもたない式 expression と副作用を持つ文 statement について")
    describe("Sec 2.1"){
      it("条件構造の if も式 expression である"){
        info("式 expression とは評価されると値を返すものであり、一方で文 statement とは値を返さずに副作用を生じるものである")
        var x = 0
        val s = if (x > 0) 1 else -1
        s should equal(-1)
        info("返り値を捨てれば文のように作用するが、好ましいスタイルではない")
        if (x > 0) {
          x = x
          x should be >= 0
        } else {
          x = - x
          x should be >= 0
        }
      }
      it("したがってif式も型 type を持つ"){
        info("値は常に何らかの型に帰属するものであり、式が常に何らかの値を返すものであれば、式は常に型を持つ")
        val x = 10
        val s = if (x > 0) 1 else -1
        s.isInstanceOf[Int] should equal(true)
      }
      it("演習: 次のif式はどのような型を持つか"){
        val x = "a string"
        val s = if (x.length > 0)
                  x.length
                else
                  "empty string"
        s.isInstanceOf[Any] should equal(true)
        info("src/test/resources/scala_class_hierachy.jpg を参照")
      }
    }
    describe("Sec 2.2"){
      it("多くの場合にセミコロンは省略できる。文の区切り"){
        pending
      }
    }    
    describe("Sec 2.3"){
      it("ブロック内で複数の式を逐次実行する"){
        pending
      }
    }    
    describe("Sec 2.4"){
      it("標準出力に出力する"){
        print("Answer: ")
        println(42)

        printf("Hello, %s! You are %d years old.\n", "Fred",42)
      }
      /*
      it("標準入力から入力する"){
        val name = readLine("Your name: ")
        print("Your age: ")
        val age = readInt()
        printf("Hello, %s! Next year, you will be %d.\n", name, age + 1)
      }
      */
      info("以上の例から明らかなように、副作用を持つメソッドはテストが難しい")
      it("副作用を持つメソッドをmockでテストする"){
        trait Output {
          def println(s: String) = Console.println(s)
        }
        trait MockOutput extends Output {
          var messages: Seq[String] = Seq()
          override def println(s: String) = messages = messages :+ s
        }
        object mock extends MockOutput {
          def print(){
            println("Answer: " + 42)
          }
        }
        mock.print()
        mock.messages should contain("Answer: 42")
      }

    }
    describe("Sec 2.5"){
      it("whileでループする"){
        var n = 10
        var r = 0
        while ( n > 0 ) {
          r = r * n
          n -= 1
        }
        n should equal(0)
      }
      it("forでループする"){
        val n = 10
        var r = 1
        for( i <- 1 to n) {
          r = r * i
        }
        r should equal(3628800)
      }
      it("untilを使う"){
        val s = "Hello"
        var sum = 0
        for( i <- 0 until s.length) {
          sum += s(i)
        }
        sum should equal(500)
      }
      it("foreachを持つコレクションはfor式のなかでジェレネータとして指定できる"){
        val s = "Hello"
        var sum = 0
        for( char <- s) {
          sum += char
        }
        sum should equal(500)

        {
          var sum = 0
          s.foreach{char =>
            sum += char
          }
          sum should equal(500)
        }

      }
      
      it("breakは標準では提供されていない"){}
      it("breakableでloopを抜ける"){
        import scala.util.control.Breaks
        
        val breaks = new Breaks()
        import breaks.{break,breakable}
        var sum = 0
        breakable {
          for(i <- 1 to 100) {
            sum = sum + i
            if(i >= 6)
              break
          }
        }
        sum should equal(21)
      }
      it("再帰でbreak同等を実現する"){
        val upto = 6
        @annotation.tailrec
        def loop(range:Range,accumulator:Int):Int = {
          val head = range.head
          if(head > upto)
            accumulator
          else
            loop(range.tail, accumulator + range.head)
        }
        val range:Range = 1 to 100
        loop(range, 0) should equal(21)
        info("この再帰の例は下記の高階関数と比べると冗長だが、引数で accumulator を利用するテクニックは「状態」を保持する方法として重要である")
        info("上記の再帰関数は末尾再帰となっている点に注意")
      }
      it("高階関数で同様の機能を実現する"){
        val range = 1 to 100
        val upto = 6
        range.take(upto).foldLeft(0){ (accum, item) =>
          accum + item
        } should equal(21)
      }
    }
    describe("Sec 2.6"){
      it("入れ子のループも1行で記述できる"){
        for(i <- 1 to 3; from = 4 - i; j <- from to 3)
          print((10 * i + j) + " ")
        info("13 22 23 31 32 33 と出力される")
      }
      it("yieldでmapと同様の処理が可能である"){
        val result = for(i <- 1 to 10) yield i % 3
        result should equal(Seq(1, 2, 0, 1, 2, 0, 1, 2, 0, 1))

        (1 to 10) map{ i =>
          i % 3
        } should equal(Seq(1, 2, 0, 1, 2, 0, 1, 2, 0, 1))
      }
    }
    describe("Sec 2.7"){
      import scala.math.BigInt
      it("再帰関数による factorial の実装"){
        info("再帰関数の定義には、返値の指定が必須である")
        info("これは型推論がプログラムの実行時ではなくコンパイル時に、構文解析によって達成されることを端的に表わしている")
        // @annotation.tailrec
        def fac(n: BigInt): BigInt = if (n <= 0) 1 else n * fac(n - 1)
        fac(10) should equal(3628800)
        info("この再帰関数は末尾再帰になっていない点に注意")

      }
      it("末尾再帰による factorial の実装"){
        @annotation.tailrec
        def facrec(n: BigInt, accum:BigInt): BigInt = {
          if (n <= 0)
            accum
          else
            facrec(n - 1, n * accum)
        }
        facrec(10,1) should equal(3628800)
      }
      it("factorialを並列化する"){
        object parallel {
          import akka.dispatch.{Await, ExecutionContext, Future}
          import akka.util.Timeout
          import akka.util.duration._
          import java.util.concurrent.{TimeoutException, Executors}
          val executorService = Executors.newFixedThreadPool(8)
          implicit val context = ExecutionContext.fromExecutor(executorService)
          implicit val timeout = Timeout(5 seconds)
          
          def facrec(n: BigInt, downto:BigInt, accum:BigInt): BigInt = {
            if (n == downto)
              accum
            else
              facrec(n - 1, downto, n * accum)
          }
          def facrec_par():BigInt = {
            val listOfFutures:List[Future[BigInt]] = List(Future{facrec(6000,5000,1)}.mapTo[BigInt],
                                                          Future{facrec(5000,4000,1)}.mapTo[BigInt],
                                                          Future{facrec(4000,3000,1)}.mapTo[BigInt],
                                                          Future{facrec(3000,2000,1)}.mapTo[BigInt],
                                                          Future{facrec(2000,1000,1)}.mapTo[BigInt],
                                                          Future{facrec(1000,1,1)}.mapTo[BigInt])
            val one:BigInt = 1
            Await.result(Future.fold(listOfFutures)(one)(_ * _), timeout.duration).asInstanceOf[BigInt]
          }
        }
        import parallel._
        facrec_par() should equal(facrec(6000,0,1) )
      }
    }
    describe("Sec 2.8"){
      def decorate(str: String, left: String = "[", right: String = "]") = left + str + right
      it("デフォルト引数の指定の仕方"){
        decorate("Hello") should equal("[Hello]")
        decorate("Hello","<<<",">>>") should equal("<<<Hello>>>")
        decorate("Hello",">>>[") should equal(">>>[Hello]")
      }
      it("名前付き引数の指定の仕方"){
        decorate(left = "<<<", str = "Hello", right = ">>>") should equal("<<<Hello>>>")
      }
      it("デフォルト引数と名前付き引数を同時に指定することも可能である"){
        decorate("Hello", right = ">>>") should equal("[Hello>>>")
      }
    }
    describe("Sec 2.9"){
      def sum(args: Int*) = {
        var result = 0
        for (arg <- args) result += arg
        result
      }
      it("可変長引数の渡しかた"){
        sum(1, 4, 9, 16, 25) should equal(55)
      }
      it("可変長引数にSeqをそのまま渡す方法"){
        sum(1 to 5: _*) should equal(15)
      }
      it("可変長引数を用いて再帰関数を定義する場合"){
        def recursivesum(args: Int*) : Int = {
          if(args.length == 0 )
            0
          else
            args.head + recursivesum(args.tail : _*)
        }
        recursivesum(1,2,3,4,5) should equal(15)
      }
    }
    describe("sec 2.10"){
      it("手続き procedure の定義の方法"){
        def box(s:String) {
          val border = "-" * s.length + "--\n"
          println(border + "|" + s + "|\n" + border)
        }
        box("a string")
      }
      it("返り値を unit に指定することで手続きを定義できる"){
        def box(s:String):Unit = {
          val border = "-" * s.length + "--\n"
          println(border + "|" + s + "|\n" + border)
        }
        info("実は unit も型であるが、空のtupleとなっている")
        box("another string").isInstanceOf[Unit] should equal(true)
      }
    }
    describe("sec 2.11"){
      it("変数の定義に lazy を前置すると、遅延評価される"){
        lazy val words = scala.io.Source.fromFile("src/test/scala/specs/chapter02spec.scala").mkString
        info("遅延評価とは、定義時ではなく、実際にアクセスされる時点まで評価されない仕組みのこと")
      }
      info("lazy val は、循環参照の解決に不可欠である")
      it("循環参照の例"){
        lazy val a:Int = b + 1
        lazy val b:Int = a + 1
      }
      it("lazy val は、無限ストリームなどの遅延型データ構造の構築に有効である") {
        lazy val fib: Stream[BigInt] = Stream.cons(0,
	                                               Stream.cons(1, fib.zip(fib.tail).map(p => p._1 + p._2)))
        fib.take(3).foldLeft(BigInt(0)){ (accum, item) =>
          accum + item
        } should equal(2)
      }
    }
    describe("sec 2.12"){
     info("scalaは,javaの検査例外 checked exception を実装していない")
     info("ただし、@throwsアノテーションを使えば検査例外を要求できる")
      
     it("throwは、Nothing型を返す式 expression である"){
       val if_expression = { x:Int => 
         if(x>=0)
           scala.math.sqrt(x)
         else
           throw new IllegalArgumentException("x should not be negative")
       }
       if_expression should be (anInstanceOf[Nothing])
       info("Nothing型はScalaのクラス階層において全ての型のサブタイプになる(src/test/resources/scala_class_hierachy.jpgを参照)")
     }
     it("例外を捕捉する場合は、catch文のなかでパターンマッチをおこなう"){
       import java.net.{URL,MalformedURLException}
       import java.io.IOException
       val path = "http://horstmann.com/fred-tiny.gif"
       try {
         val url = new URL(path)
         scala.io.Source.fromURL(url,"UTF-8").mkString
       } catch {
         case _: MalformedURLException => println("Bad URL: " + path)
         case ex:IOException => ex.printStackTrace()
       }
     }
     it("try/finallyでリソースの解放を処理できる"){
       import java.net.URL
       import java.io.InputStream
       val url = new URL("http://www.google.co.jp")
       val in = url.openStream()
       try {
         scala.io.Source.fromInputStream(in)
       } finally {
         in.close()
       }
       
     }
     it("tryとcatchは値を返す") {
       import java.net.{URL,MalformedURLException}
       import java.io.IOException
       val path = "https://github.com/akimichi/scala-seminar"
       
       val content:String = try {
         val url = new URL(path)
         scala.io.Source.fromURL(url,"UTF-8").mkString
       } catch {
         case _: MalformedURLException => ""
         case ex:IOException => ""
       }
       content.length should not equal(0)
       info("ちなみに、finallyは値を返さない")
     }
     it("finallyで例外が発生すると、その例外がスローされる"){
       
     }
     it("Tryを用いた関数的な例外処理"){
       info("http://danielwestheide.com/blog/2012/12/26/the-neophytes-guide-to-scala-part-6-error-handling-with-try.html")
     }
    }
  }
}

