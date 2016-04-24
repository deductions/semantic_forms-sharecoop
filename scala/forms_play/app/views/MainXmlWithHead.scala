package views
 
import controllers._
import deductions.runtime.html.MainXml

/** main page for ShareCoop */
trait MainXmlWithHead extends MainXml {

  val prefix = "urn:ShareCoop/vocab#"
  val prefix_sform = "urn:ShareCoop/forms#"

  override def mainPageHeader(implicit lang: String) =
	<div class="container">
		<div class="row">
			<h3>Bienvenue à Share.Coop</h3>
			Participez à Share.Coop !
			<br/>
			<a href="http://share.coop">Share.Coop</a>
		</div>
    Créer votre compte Share.Coop
    { creationButton( "http://xmlns.com/foaf/0.1/Person", "Créer",
        s"${prefix_sform}exchangePersonForm" ) }
  </div>

  /** bas de page **/
  override def pageBottom = {
    Seq( <div><a href="mailto:gmail@7738.net">Contact</a></div> , super.linkToToolsPage )   
  }

  /** HTML head */
  override def head(title: String = "")(implicit lang: String = "en") = {
    <head>
      <title>ShareCoop</title>

      <meta http-equiv="Content-type" content="text/html; charset=UTF-8"></meta>
      <link rel="shortcut icon" type="image/png" href={ routes.Assets.at("images/favicon.png").url }/>

      { javascriptCSSImports }
      
      <style type="text/css">
        .resize {{ resize: both; width: 100%; height: 100%; }}
        .overflow {{ overflow: auto; width: 100%; height: 100%; }}
				.unselectable {{
  user-select: none;
  -moz-user-select: none;
  -webkit-user-select: none;
  -ms-user-select: none;
}}
.selectable {{
  user-select: text;
  -moz-user-select: text;
  -webkit-user-select: text;
  -ms-user-select: text;
}}
      </style>
    </head>
  }
}
