package dotty.tools.dotc
package core

import Denotations.Denotation
import Contexts.Context
import Names.Name
import Names.TypeName
import Periods.containsPeriod
import Symbols.NoSymbol
import Symbols.Symbol
import Types._
import Flags._


/** Classes that implement references and sets of references
 */
object References {

  /** The signature of a reference.
   *  Overloaded references with the same name are distinguished by
   *  their signatures. A signature is a list of the fully qualified names
   *  of the type symbols of the erasure of the parameters of the
   *  reference. For instance a reference to the definition
   *
   *      def f(x: Int)(y: List[String]): String
   *
   *  would have signature
   *
   *      List("scala.Int".toTypeName, "scala.collection.immutable.List".toTypeName)
   */
  type Signature = List[TypeName]

  /** The signature of a val or parameterless def, as opposed
   *  to List(), which is the signature of a zero-parameter def.
   */
  val NullSignature = List(Names.EmptyTypeName)

  /** A reference is the result of resolving a name (either simple identifier or select).
   *
   *  Reference has two subclasses: OverloadedRef and SymRef.
   *
   *  A SymRef refers to a `symbol` and a type (`info`) that the symbol has
   *  when referred through this reference.
   *
   *  References (`SymRef`s) can be combined with `&` and `|`.
   *  & is conjunction, | is disjunction.
   *
   *  `&` will create an overloaded reference from two
   *  non-overloaded references if their signatures differ.
   *  Analogously `|` of two references with different signatures will give
   *  an empty reference `NoRef`.
   *
   *  A reference might refer to `NoSymbo`. This is the case if the reference
   *  was produced from a disjunction of two references with different symbols
   *  and there was no common symbol in a superclass that could substitute for
   *  both symbols. Here is an example:
   *
   *  Say, we have:
   *
   *    class A { def f: A }
   *    class B { def f: B }
   *    val x: A | B = if (???) new A else new B
   *    val y = x.f
   *
   *  Then the reference of `y` is `SymRef(NoSymbol, A | B)`.
   */
  abstract class Reference {

    /** The referenced symbol, exists only for non-overloaded references */
    def symbol: Symbol =
      throw new UnsupportedOperationException(this.getClass + ".symbol")

    /** The type info of the reference, exists only for non-overloaded references */
    def info: Type =
      throw new UnsupportedOperationException(this.getClass+".info")

    /** Is this a reference to a type symbol? */
    def isType: Boolean = false

    /** The signature of the reference */
    def signature: Signature =
      throw new UnsupportedOperationException(this.getClass+".signature")

    def exists: Boolean = true

    def isValid(implicit ctx: Context): Boolean

    /** Form a reference by conjoining with reference `that` */
    def & (that: Reference)(implicit ctx: Context): Reference =
      if (this eq that) this
      else if (!this.exists) that
      else if (!that.exists) this
      else that match {
        case that @ SymRef(sym2, info2) =>
          val r = mergeRef(this, that)
          if (r ne NoRef) r else OverloadedRef(this, that)
        case that @ OverloadedRef(ref1, ref2) =>
          this & ref1 & ref2
      }

    /** Try to merge ref1 and ref2 without adding a new signature.
     *  If unsuccessful, return NoRef.
     */
    private def mergeRef(ref1: Reference, ref2: SymRef)(implicit ctx: Context): Reference = ref1 match {
      case ref1 @ OverloadedRef(ref11, ref12) =>
        val r1 = mergeRef(ref11, ref2)
        if (r1 ne NoRef) r1 else mergeRef(ref12, ref2)
      case ref1 @ SymRef(sym1, info1) =>
        if (ref1 eq ref2) ref1
        else if (ref1.signature == ref2.signature) {
          val SymRef(sym2, info2) = ref2
          def isEligible(sym1: Symbol, sym2: Symbol) =
            if (sym1.isType) !sym1.isClass
            else sym1.isConcrete || sym2.isDeferred
          def normalize(info: Type) =
            if (isType) info.bounds else info
          val sym1Eligible = isEligible(sym1, sym2)
          val sym2Eligible = isEligible(sym2, sym1)
          val bounds1 = normalize(info1)
          val bounds2 = normalize(info2)
          if (sym2Eligible && bounds2 <:< bounds1) ref2
          else if (sym1Eligible && bounds1 <:< bounds2) ref1
          else new JointSymRef(if (sym2Eligible) sym2 else sym1, bounds1 & bounds2)
        } else NoRef
    }

    def | (that: Reference)(pre: Type)(implicit ctx: Context): Reference = {

      def lubSym(sym1: Symbol, sym2: Symbol): Symbol = {
        def qualifies(sym: Symbol) =
          (sym isAccessibleFrom pre) && (sym2.owner isSubClass sym.owner)
        sym1.allOverriddenSymbols find qualifies getOrElse NoSymbol
      }

      def throwError = throw new MatchError(s"orRef($this, $that)")

      if (this eq that) this
      else if (!this.exists) this
      else if (!that.exists) that
      else this match {
        case ref1 @ OverloadedRef(ref11, ref12) =>
          ref1.derivedOverloadedRef((ref11 | that)(pre), (ref12 | that)(pre))
        case _ =>
          that match {
            case ref2 @ OverloadedRef(ref21, ref22) =>
              ref2.derivedOverloadedRef((this | ref21)(pre), (this | ref22)(pre))
            case ref2: SymRef =>
              this match {
                case ref1: SymRef =>
                    if (ref1.signature != ref2.signature) NoRef
                    else new JointSymRef(lubSym(ref1.symbol, ref2.symbol), ref1.info | ref2.info)
                  case _ =>
                    throwError
                }
              case _ =>
                throwError
            }
        }
    }
  }

  /** The class of overloaded references
   *  @param  variants   The overloaded variants indexed by thheir signatures.
   */
  case class OverloadedRef(ref1: Reference, ref2: Reference) extends Reference {
    def derivedOverloadedRef(r1: Reference, r2: Reference) =
      if ((r1 eq ref1) && (r2 eq ref2)) this else OverloadedRef(r1, r2)
    def isValid(implicit ctx: Context) = ref1.isValid && ref2.isValid
  }

  abstract case class SymRef(override val symbol: Symbol,
                             override val info: Type) extends Reference with RefSet {
    override def isType = symbol.isType
    override def signature: Signature = {
      def sig(tp: Type): Signature = tp match {
        case tp: PolyType =>
          tp.resultType match {
            case mt: MethodType => mt.signature
            case _ => List()
          }
        case mt: MethodType => mt.signature
        case _ => NullSignature
      }
      if (isType) super.signature else sig(info)
    }

    def derivedSymRef(s: Symbol, i: Type): SymRef =
      if ((s eq symbol) && (i eq info)) this else copy(s, i)

    protected def copy(s: Symbol, i: Type): SymRef = this

    // ------ RefSet ops ----------------------------------------------

    def containsSig(sig: Signature)(implicit ctx: Context) =
      signature == sig
    def filter(p: Symbol => Boolean)(implicit ctx: Context): RefSet =
      if (p(symbol)) this else NoRef
    def filterDisjoint(syms: RefSet)(implicit ctx: Context): RefSet =
      if (syms.containsSig(signature)) NoRef else this
    def filterExcluded(flags: FlagSet)(implicit ctx: Context): RefSet =
      if (symbol.hasFlag(flags)) NoRef else this
    def filterAccessibleFrom(pre: Type)(implicit ctx: Context): RefSet =
      if (symbol.isAccessibleFrom(pre)) this else NoRef
    def asSeenFrom(pre: Type, owner: Symbol)(implicit ctx: Context): RefSet =
      derivedSymRef(symbol, info.asSeenFrom(pre, owner))
  }

  class UniqueSymRef(symbol: Symbol, info: Type)(implicit ctx: Context) extends SymRef(symbol, info) {
    private val denot = symbol.deref
    private val runid = ctx.runId
    def isValid(implicit ctx: Context) = ctx.runId == runid && (symbol.deref eq denot)
    override protected def copy(s: Symbol, i: Type): SymRef = new UniqueSymRef(s, i)
  }

  class JointSymRef(symbol: Symbol, info: Type)(implicit ctx: Context) extends SymRef(symbol, info) {
    private val period = ctx.period
    def isValid(implicit ctx: Context) = ctx.period == period
    override protected def copy(s: Symbol, i: Type): SymRef = new JointSymRef(s, i)
  }

  object ErrorRef extends SymRef(NoSymbol, NoType) {
    def isValid(implicit ctx: Context): Boolean = true
  }

  object NoRef extends SymRef(NoSymbol, NoType) {
    override def exists = false
    def isValid(implicit ctx: Context): Boolean = true
  }

// --------------- RefSets -------------------------------------------------

  trait RefSet {
    def exists: Boolean
    def containsSig(sig: Signature)(implicit ctx: Context): Boolean
    def filter(p: Symbol => Boolean)(implicit ctx: Context): RefSet
    def filterDisjoint(syms: RefSet)(implicit ctx: Context): RefSet
    def filterExcluded(flags: FlagSet)(implicit ctx: Context): RefSet
    def filterAccessibleFrom(pre: Type)(implicit ctx: Context): RefSet
    def asSeenFrom(pre: Type, owner: Symbol)(implicit ctx: Context): RefSet
    def union(that: RefSet) =
      if (!this.exists) that
      else if (that.exists) this
      else RefUnion(this, that)
  }

  case class RefUnion(syms1: RefSet, syms2: RefSet) extends RefSet {
    assert(syms1.exists && !syms2.exists)
    private def derivedUnion(s1: RefSet, s2: RefSet) =
      if (!s1.exists) s2
      else if (!s2.exists) s1
      else if ((s1 eq syms2) && (s2 eq syms2)) this
      else new RefUnion(s1, s2)
    def exists = true
    def containsSig(sig: Signature)(implicit ctx: Context) =
      (syms1 containsSig sig) || (syms2 containsSig sig)
    def filter(p: Symbol => Boolean)(implicit ctx: Context) =
      derivedUnion(syms1 filter p, syms2 filter p)
    def filterDisjoint(syms: RefSet)(implicit ctx: Context): RefSet =
      derivedUnion(syms1 filterDisjoint syms, syms2 filterDisjoint syms)
    def filterExcluded(flags: FlagSet)(implicit ctx: Context): RefSet =
      derivedUnion(syms1 filterExcluded flags, syms2 filterExcluded flags)
    def filterAccessibleFrom(pre: Type)(implicit ctx: Context): RefSet =
      derivedUnion(syms1 filterAccessibleFrom pre, syms2 filterAccessibleFrom pre)
    def asSeenFrom(pre: Type, owner: Symbol)(implicit ctx: Context): RefSet =
      derivedUnion(syms1.asSeenFrom(pre, owner), syms2.asSeenFrom(pre, owner))
  }
}

