package deductions.runtime.html

import scala.xml.Elem
import org.apache.log4j.Logger
import org.w3.banana.FOAFPrefix
import org.w3.banana.RDFModule
import org.w3.banana.jena.Jena
import org.w3.banana.jena.JenaModule
import deductions.runtime.abstract_syntax.FormSyntaxFactory
import deductions.runtime.jena.RDFStoreObject
import deductions.runtime.sparql_cache.RDFCacheJena
import scala.xml.PrettyPrinter

/** Table View of a form */
trait TableView
  extends RDFModule
  with JenaModule // TODO depend on generic Rdf
  with Form2HTML[Jena#URI]
  with RDFCacheJena // TODO depend on generic Rdf
{
  import Ops._
  val nullURI : Rdf#URI = Ops.URI( "" )
  val foaf = FOAFPrefix[Rdf]
//  val foafURI = foaf.prefixIri

  /** create a form for given uri with background knowledge ??? TODO */
  def htmlForm(uri:String) : Elem = {
    val store =  RDFStoreObject.store
//    RDFStoreObject.printGraphList
    // TODO load ontologies from local SPARQL; probably use a pointed graph
    storeURI(makeUri(uri), store)
    Logger.getRootLogger().info(s"After storeURI(makeUri($uri), store)")
//    RDFStoreObject.printGraphList
    store.readTransaction {
      val allNamedGraphs = store.getGraph(makeUri("urn:x-arq:UnionGraph"))
      graf2form(allNamedGraphs, uri)
    }
  }

  /** create a form for given URI resource (instance) with background knowledge in graph1 */
  def graf2form(graph1: Rdf#Graph, uri:String): Elem = {
    val graph = graph1 // .union(vocabGraph)

    val factory = new FormSyntaxFactory[Rdf](graph)
//    println("graf2form " + " " + graph.hashCode() + "\n" 
////        + (graph.toIterable).mkString("\n")
//                + factory.printGraph(graph)
//    )
    val form = factory.createForm(
        URI(uri)
        // find properties from instance
//       , Seq(
//          foaf.title, 
//          foaf.givenName,
//          foaf.familyName, 
//          foaf.currentProject,
//          foaf.img,
//          foaf.mbox
//      )
    )
    println("form:\n" + form)
    val htmlForm = generateHTML(form)
    println(htmlForm)
    htmlForm
  }
  
  def htmlFormString(uri:String) : String = {
    val f = htmlForm(uri)
    val pp = new PrettyPrinter(80, 2)
    pp.format(f)
  }

  def graf2formString(graph1: Rdf#Graph, uri:String): String = {
    graf2form(graph1, uri).toString
  }
}