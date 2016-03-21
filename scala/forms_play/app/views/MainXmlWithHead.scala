package views
 
import controllers._
import deductions.runtime.html.MainXml

trait MainXmlWithHead extends MainXml {

  val prefix = "urn:ShareCoop/vocab#"

  override def mainPageHeader(implicit lang: String) =
	<div class="container">
		<div class="row">
			<h3>Bienvenue à Share.Coop</h3>
			Participez à Share.Coop et recevez un don en 
			<a href="http://chequesolidaire.org">Chèque solidaire?</a>
			<br/>
			Jeu gratuit et sans engagement
			<br/>
			<a href="http://share.coop">Share.Coop</a>
		</div>
    Créer votre compte Share.Coop
    { creationButton( prefix + "Prospect", "Créer") }
  </div>

  /** bas de page **/
  override def pageBottom = {
    Seq( <div><a href="???">Contact</a></div> , super.linkToToolsPage )   
  }

  /** HTML head */
  override def head(title: String = "")(implicit lang: String = "en") = {
    <head>
      <title>ShareCoop</title>

<!-- TODO copied from semantic_forms -->
      <meta http-equiv="Content-type" content="text/html; charset=UTF-8"></meta>

      <link rel="shortcut icon" type="image/png" href={ routes.Assets.at("images/favicon.png").url }/>
      <!--
        script src={ routes.Assets.at("javascripts/jquery-1.11.2.min.js").url } type="text/javascript"></script
        <script src={ routes.Assets.at("javascripts/jquery-1.12.0.min.js").url } type="text/javascript"></script>
      -->
      <script src={ routes.Assets.at("javascripts/jquery-2.2.0.min.js").url } type="text/javascript"></script>

      <!-- bootstrap -->
  		<link rel="stylesheet" href={ routes.Assets.at("stylesheets/bootstrap.min.css").url } />
  		<link rel="stylesheet" href={ routes.Assets.at("stylesheets/bootstrap-theme.min.css").url } />
  		<script src={ routes.Assets.at("javascripts/bootstrap.min.js").url } type="text/javascript"></script>

      <link rel="stylesheet" href={ routes.Assets.at("stylesheets/select2.css").url }/>
      <link href="//cdnjs.cloudflare.com/ajax/libs/select2/4.0.0-beta.3/css/select2.min.css" rel="stylesheet"/>
      <script src="//cdnjs.cloudflare.com/ajax/libs/select2/4.0.0-beta.3/js/select2.min.js"></script>

      <script src={ routes.Assets.at("javascripts/select2.js").url } type="text/javascript"></script>
      <script src={ routes.Assets.at("javascripts/wikipedia.js").url } type="text/javascript"></script>
      <script src={ routes.Assets.at("javascripts/formInteractions.js").url } type="text/javascript"></script>

<script src="assets/fluidgraph/js/jquery-2.1.4.min.js"></script>
<script src="assets/fluidgraph/js/d3.v3.min.js"></script>
<script src="assets/fluidgraph/js/jquery.mockjax.min.js"></script>
<script src="assets/fluidgraph/js/FileSaver.min.js"></script>
<script src="assets/LDP-framework/mystore.js"></script>

<script src="assets/fluidgraph/js/semantic2.1.2.js"></script>
<link rel="stylesheet" href="assets/fluidgraph/css/semantic2.1.2.css" />

<script src="assets/fluidgraph/js/mockdata.js"></script>
<script src="assets/fluidgraph/js/init.js"></script>
<script src="assets/fluidgraph/js/mygraph.js"></script>
<script src="assets/fluidgraph/js/mynodes.js"></script>
<script src="assets/fluidgraph/js/mylinks.js"></script>
<script src="assets/fluidgraph/js/mybackground.js"></script>
<script src="assets/fluidgraph/js/convert.js"></script>

      <style type="text/css">
        .resize {{ resize: both; width: 100%; height: 100%; }}
        .overflow {{ overflow: auto; width: 100%; height: 100%; }}
      </style>
    </head>
  }
}
