package scala_seminar

trait helpers {
  import org.scalatest.matchers._
  
  def anInstanceOf[T](implicit manifest: Manifest[T]) = { 
    val clazz = manifest.erasure.asInstanceOf[Class[T]] 
    new BePropertyMatcher[AnyRef] { 
      def apply(left: AnyRef) = BePropertyMatchResult(clazz.isAssignableFrom(left.getClass), "an instance of " + clazz.getName) 
    }
  }

   // var mutable:String = "mutable"
   // def get_mutable:String = mutable

   object ExpensiveCalc {
     val NumInterations = 1000

     def expensiveCalc = calculatePiFor(0, 1000000)

     def calculatePiFor(start: Int, nrOfElements: Int): Double = {
       var acc = 0.0
       for (i <- start until (start + nrOfElements))
         acc += 4.0 * (1 - (i % 2) * 2) / (2 * i + 1)
       acc
     }
   }
}
