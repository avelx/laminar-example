package example

import com.raquo.laminar.api.L.{*, given}
import org.scalajs.dom

@main def main(): Unit =
  renderOnDomContentLoaded(dom.document.getElementById("app"), App())

object App:

  def apply(): HtmlElement =
    div(
      cls := "app",
      h1("Shopping list"),
      sectionTag(ShoppingList())
    )
