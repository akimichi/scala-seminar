import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.FunSuite
import org.scalatest.matchers.ShouldMatchers
import org.scalatest.{ FunSpec, BeforeAndAfterAll, BeforeAndAfterEach }

import scala_seminar._

class Appendix01Spec extends FunSpec with ShouldMatchers with helpers {
  describe("monad"){
    /**
     * (M, unitM, bindM)
     * 
     *
     */ 
  }
  describe("発展"){
    type Name = String
    type Iden = Int
    type Mark = Int

    type Codes = List[(Name,Iden)]
    type Marks = List[(Iden,Mark)]

    val codes:Codes = List("ANDERSON" -> 101372,
                           "BAYLIS" -> 101369,
                           "CARTER" -> 101370,
                           "DENNIS" -> 101371,
                           "EDWARD" -> 101373)
    val marks:Marks = List(101369 -> 62,
                           101370 -> 75,
                           101371 -> 62,
                           101372 -> 30,
                           101373 -> 50)
    
    def pair[A,B,C] : Pair[A => B, A => C] => A => Pair[B,C] = {(fg:Pair[A => B, A => C]) => { x:A =>
      val (f,g) : Pair[A => B, A => C] = fg
      (f(x), g(x))
    }}
    /* 
     cross(xs ys) = [(x, y) |  x <- xs, y <- ys]
     */ 
    def cross[A,B,C,D] : Pair[A => B, C => D] => Pair[A,C] => Pair[B,D] = {(fg:Pair[A => B, C => D]) => { ac:Pair[A,C] =>
      val (f,g) : Pair[A => B, C => D] = fg
      val (a,c) : Pair[A,C] = ac
      val (b,d) : Pair[B,D] = (f(a), g(c))
        (b,d)
    }}

    /*
     def collate(codes:Codes, marks:Marks) : List[Pair[Name,Mark]] = {
     val ids:List[Iden] = for {
     code <- codes
     mark <- marks
     
     } yield {
     val ab : (Name,Iden) => Name = {(name,iden) =>
     name
     }
     val bd : (Iden, Mark) => Mark = {(iden,mark) =>
     mark
     }
     cross[(Name,Iden),Name,(Iden,Mark),Mark]((ab,bd))(code, mark)
     }
     }
     */ 
  }
}



