package example

import com.raquo.laminar.api.L.{*, given}

final case class Item(id: Int, name: String)

object ShoppingList:

  def apply(): HtmlElement =
    val itemsVar = Var(List.empty[Item])
    val inputVar = Var("")
    var nextId = 0

    def addItem(): Unit =
      val name = inputVar.now().trim
      if name.nonEmpty then
        nextId += 1
        itemsVar.update(_ :+ Item(nextId, name))
        inputVar.set("")

    def removeItem(id: Int): Unit =
      itemsVar.update(_.filterNot(_.id == id))

    div(
      form(
        onSubmit.preventDefault --> (_ => addItem()),
        input(
          placeholder := "Add an item…",
          controlled(
            value <-- inputVar,
            onInput.mapToValue --> inputVar
          )
        ),
        button(typ := "submit", "Add")
      ),
      ul(
        children <-- itemsVar.signal.split(_.id) { (id, _, itemSignal) =>
          li(
            span(child.text <-- itemSignal.map(_.name)),
            button(cls := "remove", "✕", onClick --> (_ => removeItem(id)))
          )
        }
      ),
      p(
        cls := "summary",
        child.text <-- itemsVar.signal.map {
          case Nil   => "Nothing to buy yet."
          case items => s"${items.size} item(s) to buy"
        }
      )
    )
