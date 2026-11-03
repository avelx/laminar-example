package example

import com.raquo.laminar.api.L.{*, given}
import com.raquo.laminar.nodes.RootNode
import org.scalajs.dom

/** Integration test: mounts the real ShoppingList component into a jsdom
  * document and drives it through DOM events, as a user would.
  */
class ShoppingListSpec extends munit.FunSuite:

  private var container: dom.html.Div = null
  private var root: RootNode = null

  override def beforeEach(context: BeforeEach): Unit =
    container = dom.document.createElement("div").asInstanceOf[dom.html.Div]
    dom.document.body.appendChild(container)
    root = render(container, ShoppingList())

  override def afterEach(context: AfterEach): Unit =
    root.unmount()
    dom.document.body.removeChild(container)

  // --- DOM helpers ---------------------------------------------------------

  private def inputEl: dom.html.Input =
    container.querySelector("input").asInstanceOf[dom.html.Input]

  private def items: List[String] =
    val spans = container.querySelectorAll("li span")
    (0 until spans.length).map(i => spans(i).textContent).toList

  private def summary: String =
    container.querySelector(".summary").textContent

  private def removeButtons: List[dom.html.Button] =
    val buttons = container.querySelectorAll("li button.remove")
    (0 until buttons.length).map(i => buttons(i).asInstanceOf[dom.html.Button]).toList

  private def typeText(text: String): Unit =
    inputEl.value = text
    inputEl.dispatchEvent(new dom.Event("input", new dom.EventInit { bubbles = true }))

  private def submit(): Unit =
    container
      .querySelector("form")
      .dispatchEvent(new dom.Event("submit", new dom.EventInit { bubbles = true; cancelable = true }))

  private def addItem(name: String): Unit =
    typeText(name)
    submit()

  // --- Tests ---------------------------------------------------------------

  test("starts with an empty list"):
    assertEquals(items, Nil)
    assertEquals(summary, "Nothing to buy yet.")

  test("adding an item shows it in the list and clears the input"):
    addItem("Milk")
    assertEquals(items, List("Milk"))
    assertEquals(inputEl.value, "")
    assertEquals(summary, "1 item(s) to buy")

  test("items are trimmed and kept in insertion order"):
    addItem("  Bread  ")
    addItem("Eggs")
    assertEquals(items, List("Bread", "Eggs"))
    assertEquals(summary, "2 item(s) to buy")

  test("blank input is ignored"):
    addItem("   ")
    assertEquals(items, Nil)
    assertEquals(summary, "Nothing to buy yet.")

  test("removing an item takes only that item out of the list"):
    addItem("Milk")
    addItem("Eggs")
    addItem("Butter")
    removeButtons(1).click()
    assertEquals(items, List("Milk", "Butter"))
    assertEquals(summary, "2 item(s) to buy")
