package utils

import io.gatling.core.Predef._
import io.gatling.core.check.CheckBuilder
import io.gatling.core.check.css.CssCheckType
import jodd.lagarto.dom.NodeSelector

object AuthCheck {
  def save: CheckBuilder[CssCheckType, NodeSelector] = css("input[name='authenticity_token']", "value").saveAs("auth")

  def csrfParameter: String = "authenticity_token"
  def csrfTemplate: String = "${auth}"
}
