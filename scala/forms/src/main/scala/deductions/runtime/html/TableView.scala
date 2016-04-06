package deductions.runtime.html

import scala.concurrent.ExecutionContext.Implicits
import scala.util.Failure
import scala.util.Success
import scala.util.Try
import scala.xml.NodeSeq
import scala.xml.PrettyPrinter
import org.apache.log4j.Logger
import org.w3.banana.RDF
import deductions.runtime.abstract_syntax.FormModule
import deductions.runtime.abstract_syntax.FormSyntaxFactory
import deductions.runtime.sparql_cache.RDFCacheAlgo
import deductions.runtime.utils.Timer
import deductions.runtime.services.ConfigurationCopy
import deductions.runtime.services.Configuration

/**
 * Form for a subject URI with existing triples;
 *  a facade that blends:
 *  - the RDF cache [[deductions.runtime.sparql_cache.RDFCacheAlgo]],
 *  - the generic Form Factory [[deductions.runtime.abstract_syntax.FormSyntaxFactory]],
 *  - the HTML renderer [[deductions.runtime.html.Form2HTML]];
 *  transactional
 *
 * named TableView because originally it was an HTML table.
 */
trait TableViewModule[Rdf <: RDF, DATASET]
    extends RDFCacheAlgo[Rdf, DATASET]
    with FormSyntaxFactory[Rdf, DATASET]
    with Timer {
  import ops._
  import rdfStore.transactorSyntax._

//  val nullURI: Rdf#URI = URI("")
  import scala.concurrent.ExecutionContext.Implicits.global

  /**
   * wrapper for htmlForm that shows Failure's ;
   *  non TRANSACTIONAL
   */
  def htmlFormElemRaw(uri: String, unionGraph: Rdf#Graph=allNamedGraph, hrefPrefix: String = "", blankNode: String = "",
    editable: Boolean = false,
    actionURI: String = "/save",
    lang: String = "en",
    graphURI: String = "",
    actionURI2: String = "/save",
    formGroup: String = fromUri(nullURI),
    formuri: String=""): NodeSeq = {

    htmlFormRaw(uri, unionGraph, hrefPrefix, blankNode, editable, actionURI,
      lang, graphURI, actionURI2, URI(formGroup), formuri) match {
        case Success(e) => e
        case Failure(e) => <p>htmlFormElem: Exception occured: { e }</p>
      }
  }
  
  /**
   * wrapper for htmlForm that shows Failure's ;
   *  TRANSACTIONAL
   */
  def htmlFormElem(uri: String, hrefPrefix: String = "", blankNode: String = "",
    editable: Boolean = false,
    actionURI: String = "/save",
    lang: String = "en",
    graphURI: String = "",
    actionURI2: String = "/save",
    formGroup: String = fromUri(nullURI)): NodeSeq = {

    htmlForm(uri, hrefPrefix, blankNode, editable, actionURI,
      lang, graphURI, actionURI2, URI(formGroup)) match {
        case Success(e) => e
        case Failure(e) => <p>htmlFormElem: Exception occured: { e }</p>
      }
  }

  /**
   * wrapper for htmlForm, but generates Just Fields; also shows Failure's;
   * see [[deductions.runtime.html.Form2HTML#generateHTMLJustFields]].
   * TRANSACTIONAL
   */
  def htmlFormElemJustFields(uri: String, hrefPrefix: String = "", blankNode: String = "",
    editable: Boolean = false,
    lang: String = "en",
    graphURI: String = "",
    formGroup: String = fromUri(nullURI),
    formuri: String="" )
    : NodeSeq = {

    // TODO for comprehension like in htmlForm()

    val (graphURIActual, _) = doRetrieveURI(uri, blankNode, graphURI)
    val htmlFormTry = dataset.rw({
      implicit val graph: Rdf#Graph = allNamedGraph
      val ops1 = ops;
      val form = createAbstractForm(
          uri, editable, lang, blankNode,
        URI(formGroup), formuri )
      new Form2HTMLBanana[Rdf] with ConfigurationCopy {
        val ops = ops1
        lazy val original:Configuration = TableViewModule.this
      } .
        generateHTMLJustFields(form,
          hrefPrefix, editable, graphURIActual)
    })
    htmlFormTry match {
      case Success(e) => e
      case Failure(e) => <p class="error">htmlFormElemJustFields: Exception occured: {
        e.printStackTrace()
      }</p>
      throw e
    }
  }

  /**
   * create a form for given URI with background knowledge in RDFStoreObject.store;
   *  by default user inputs will be saved in named graph uri, except if given graphURI argument;
   *  NON TRANSACTIONAL;
   *  
   *  Note: first try to retrieve from Internet at given URI,
   *  then eventually save in TDB,
   *  then read again <uri> ?P ?O.	from TDB, in any named graph,
   *  to catch 1) triples downloaded from URI, 2) triples preloaded,
   *  3) triples coming from user edits
   *  @param blankNode if "true" given uri is a blanknode
   *  
   */
  private def htmlFormRaw(uri: String, unionGraph: Rdf#Graph = allNamedGraph,
                          hrefPrefix: String = "", blankNode: String = "",
                          editable: Boolean = false,
                          actionURI: String = "/save",
                          lang: String = "en",
                          graphURI: String = "",
                          actionURI2: String = "/save",
                          formGroup: Rdf#URI = nullURI,
                          formuri: String=""): Try[NodeSeq] = {

    println(s"htmlFormRaw dataset $dataset, graphURI <$graphURI>")
    val tryGraph = if (blankNode != "true") {
      val res = retrieveURINoTransaction(makeUri(uri), dataset)
      Logger.getRootLogger().info(s"After retrieveURINoTransaction(makeUri($uri), store)")
      res
    } else Success(emptyGraph)
    val graphURIActual = if (graphURI == "") uri else graphURI
    Success(graf2form(unionGraph, uri, hrefPrefix, blankNode, editable,
      actionURI, lang, graphURIActual, actionURI2, formGroup, formuri))
  }
  
  /**
   * create a form for given URI with background knowledge in RDFStoreObject.store;
   *  by default user inputs will be saved in named graph uri, except if given graphURI argument;
   *  @param blankNode if "true" given uri is a blanknode
   *  TRANSACTIONAL
   */
  private def htmlForm(uri: String, hrefPrefix: String = "", blankNode: String = "",
    editable: Boolean = false,
    actionURI: String = "/save",
    lang: String = "en",
    graphURI: String = "",
    actionURI2: String = "/save",
    formGroup: Rdf#URI = nullURI)
    : Try[NodeSeq] = {

    println( s"htmlForm dataset $dataset" )

    for {
      (graphURIActual, tryGraph) <- Try { time("doRetrieveURI", doRetrieveURI(uri, blankNode, graphURI)) }

      // TODO find another way of reporting download failures: 
      //      graphDownloaded <- tryGraph
      
      form <- dataset.rw({
        graf2form(allNamedGraph, uri, hrefPrefix, blankNode, editable,
          actionURI, lang, graphURIActual, actionURI2, formGroup)
      })
    } yield form
  }

  /** Retrieve URI from Internet or triples cache;
   * with transaction
   *
   *  @return Actual graph URI: given graph URI or if not specified given uri
   */
  private def doRetrieveURI(uri: String, blankNode: String, graphURI: String): (String, Try[Rdf#Graph]) = {
    val tryGraph = if (blankNode != "true") {
      val res = retrieveURI(makeUri(uri), dataset)
      Logger.getRootLogger().info(s"After retrieveURI(makeUri($uri), dataset) isSuccess ${res.isSuccess}")

//      println("Search duplicate graph rooted at blank node: size " + getTriples(res.get).size )
//      manageBlankNodesReload(res.getOrElse(emptyGraph), URI(uri), dataset: DATASET)
      res
    } else Success(emptyGraph)
    val graphURIActual = if (graphURI == "") uri else graphURI
    (graphURIActual, tryGraph)
  }

  /**
   * create a form for given URI resource (instance) with background knowledge
   *  in given graph
   *  TODO non blocking
   */
  private def graf2form(graphe: Rdf#Graph, uri: String,
    hrefPrefix: String = "", blankNode: String = "",
    editable: Boolean = false,
    actionURI: String = "/save",
    lang: String = "en", graphURI: String,
    actionURI2: String = "/save",
    formGroup: Rdf#URI = nullURI,
    formuri: String="")
    : NodeSeq = {

    implicit val graph: Rdf#Graph = graphe
    println(s"TableViewModule.graf2form(graph: graph size: ${graph.size}, graphURI <$graphURI>")
    val form = time("createAbstractForm",
      createAbstractForm(
          uri, editable, lang, blankNode, formGroup, formuri))
    val ops1 = ops;
    val htmlFormGen = time("new Form2HTML",
      new Form2HTMLBanana[Rdf] with ConfigurationCopy {
        val ops = ops1
        lazy val original:Configuration = TableViewModule.this
//        override def showPlusButtons = TableViewModule.this.showPlusButtons   
      }
    )
    val htmlForm = htmlFormGen.
      generateHTML(form, hrefPrefix, editable, actionURI, graphURI,
        actionURI2, lang)
    htmlForm
  }

  private def createAbstractForm(
//      graphArg: Rdf#Graph, 
      uri: String, editable: Boolean,
    lang: String, blankNode: String, formGroup: Rdf#URI, formuri: String="")
    (implicit graph: Rdf#Graph)
    : FormModule[Rdf#Node, Rdf#URI]#FormSyntax = {
    val subjectNode = if (blankNode == "true")
      /* Jena TDB specific:
           * Jena supports "concrete bnodes" in SPARQL syntax as pseudo URIs in the "_" URI scheme
           * (it's an illegal name for a URI scheme) */
      BNode(uri)
    else URI(uri)
    val factory = this
//      new FormSyntaxFactory[Rdf, DATASET] {
//     val graph=graphArg
//     val preferedLanguage = lang }
    preferedLanguage = lang
    factory.createForm(subjectNode, editable, formGroup, formuri)
  }

  def htmlFormString(uri: String,
    editable: Boolean = false,
    actionURI: String = "/save", graphURI: String)(implicit allNamedGraphs: Rdf#Graph): String = {
    val f = htmlFormElem(uri, editable = editable, actionURI = actionURI)
    val pp = new PrettyPrinter(80, 2)
    pp.formatNodes(f)
  }

  def graf2formString(graph1: Rdf#Graph, uri: String, graphURI: String): String = {
    graf2form(graph1, uri, graphURI = graphURI).toString
  }

  //  override 
//  def toPlainString(n: Rdf#Node): String = {
//    val v = foldNode(n)(
//      uri => fromUri(uri),
//      bn => fromBNode(bn),
//      lit => { val (v, typ, langOption) = fromLiteral(lit); v }
//    )
//    v
//  }

}
