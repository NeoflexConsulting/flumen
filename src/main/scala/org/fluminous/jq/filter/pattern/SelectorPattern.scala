package org.fluminous.jq.filter.pattern

import org.fluminous.jq.filter.Selector
import org.fluminous.jq.filter.pattern.dsl.Matcher.{ capture, check }
import org.fluminous.jq.tokens.{ Identifier, Pipe, RawString, Root }
import shapeless.HNil
import shapeless.::

case object SelectorPattern extends ExpressionPattern {
  override val ExpressionCases: PatternCases = PatternCases[Selector](
    (capture[Identifier] ->: check[Root]).ifValidReplaceBy {
      case id :: HNil => Selector(_, Seq(id.value))
    },
    (capture[RawString] ->: check[Root]).ifValidReplaceBy {
      case s :: HNil => Selector(_, Seq(s.value))
    },
    (capture[Selector] ->: capture[Selector]).ifValidReplaceBy {
      case s1 :: s2 :: HNil => Selector(_, s2.path ++ s1.path)
    },
    (capture[Selector] ->: check[Pipe] ->: capture[Selector]).ifValidReplaceBy {
      case s1 :: s2 :: HNil => Selector(_, s2.path ++ s1.path)
    }
  )
}
