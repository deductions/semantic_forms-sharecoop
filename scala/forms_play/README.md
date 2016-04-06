Play! framework implementations
---

# Explications projet ShareCoop
## Projet forké: pourquoi?

Ce fork qui ne sera jamais fusionné (pas de Pull Request). C'est un moyen de pallier le fait qu'on ne peut pas (AFAIK) redéfinir des éléments d'un projet Play à partir d'un projet indépendant.

Pour mettre à jour par rapport à semantic\_forms :

https://help.github.com/articles/syncing-a-fork/

Et il faut d'abord faire une fois pour toutes à chaque répertoire issu d'un git clone:
https://help.github.com/articles/configuring-a-remote-for-a-fork/
	git remote add upstream https://github.com/jmvanel/semantic_forms.git

## Quoi modifier pour les parties statiques de pages

Haut et bas de page:
[MainXmlWithHead.scala](app/views/MainXmlWithHead.scala)

L'icône pour l'onglet:
[favicon.png](public/images/favicon.png)


# Introduction
Here is a web application with Play! framework around the [form generator](../forms/README.md) that does:
- navigation on the LOD (Linked Open Data) cloud,
- CRUD (CReate, Update, Delete) editing,
- search
- semantic cache

For now, the display looks like this, 
plus a textbox to enter a URL semantics, eg a FOAF profile or DBpedia URI : 
[example.form.foaf.html](http://htmlpreview.github.io/?https://github.com/jmvanel/semantic_forms/blob/master/scala/forms/example.form.foaf.html)

See the [wiki](https://github.com/jmvanel/semantic_forms/wiki) for User manual and Developer manual.
This README is like an administrator Manual. 

## Terminology

Some people speak of "triple store", or "graph database", or "triple database", or "SPARQL database". We will write here just "database".

# How to run
## Run locally from sources

- install dependencies:
  - Java 8 ,
  - [SBT](http://www.scala-sbt.org/) or [Typesafe Activator](http://typesafe.com/platform/getstarted) .
Then SBT or Activator will download the rest.
- download the source from [Banana-RDf fork on github](https://github.com/deductions/banana-rdf) (temporary, until my Pull Request in Banana-RDF is accepted)
  - build this project with SBT or Activator: change directory to `banana-rdf` ; type in the activator console : `publishLocal`
- download the source from [semantic\_forms on github](https://github.com/jmvanel/semantic_forms/)
- build and run the semantic\_forms project itself with SBT or Activator: change directory to `scala/forms_play` within the source downloaded from github; type in the activator console : `~ run`

The default port is 9000, so you can direct your browser to [http://localhost:9000](http://localhost:9000) .
To run on another port than 9000 :

    run 9053

To raise the memory, set this environment variable:

    export SBT_OPTS="-Xmx2G"

To load some common vocabularies (FOAF, ...) and form specifications, see below "Preloading RDF content".
To understand usage of the user interface, see the [User manual wiki](https://github.com/jmvanel/semantic_forms/wiki).

To build the ScalaDoc, run `sbt doc` , and the ScalaDoc will be in

	target/scala-2.11/api/

A convenience link to [local semantic\_forms ScalaDoc](../forms/target/scala-2.11/api/index.html)


Some references on ScalaDoc: http://docs.scala-lang.org/style/scaladoc.html , http://docs.scala-lang.org/overviews/scaladoc/for-library-authors.html , http://stackoverflow.com/questions/15394322/how-to-disambiguate-links-to-methods-in-scaladoc

## Run on a server without development environment

To package to run on a server that has Java only: type in activator : `dist`

Then the archive is found here :
`target/universal/semantic_forms_play-1.0-SNAPSHOT.zip`
Download this zip on the server, unzip and type:
```shell
nohup bin/semantic_forms_play -J-Xmx50M &
```

There is a script that does this, and more: it stops the server, updates the application from sources, and restarts the server :

    ./scripts/update_server.sh

It is advised to deactivate the automatic formatting on a server machine. Just comment out the line `scalariformSettings` in 
scala/forms/build.sbt . 

If you want to change the HTTP ports, etc, look in the Play! documentation:
https://www.playframework.com/documentation/2.3.x/ProductionConfiguration

If you want to change the log settings:
```
cp conf/log4j.properties myconf.properties
vi myconf.properties
nohup bin/semantic_forms_play -Dlog4j.configuration=myconf.properties -mem 50 &
```

To download Java from the server with no browser (see http://stackoverflow.com/questions/10268583/downloading-java-jdk-on-linux-via-wget-is-shown-license-page-instead):
    VERSION=51
    wget --no-check-certificate --no-cookies --header "Cookie: oraclelicense=accept-securebackup-cookie" \
    http://download.oracle.com/otn-pub/java/jdk/8u$VERSION-b16/jdk-8u$VERSION-linux-arm-vfp-hflt.tar.gz

# Setting a IDE project ( eclipse ...)

Please read explanations on the Banana-RDF project:
[ide-setup](https://github.com/w3c/banana-rdf/#ide-setup)

Note that you run
```
sbt eclipse
```
just once in the directory scala/forms\_play/ ; this creates configuration for 2 eclipse projects: one in directory forms\_play/, one in directory forms.

You may have to set by hand the dependency in project forms\_play/ towards the other. For this, go in Properties of project, Java Build Path, Projects tab, and Add...

## Ensime

Here are commands I used for installing Ensime for Vim
https://github.com/ensime/ensime-vim

```shell
sudo apt-get install python-pip
sudo pip install websocket-client
export BROWSER=firefox
echo 'export BROWSER=firefox' >> ~/.bashrc 
mkdir -p ~/.sbt/0.13/plugins/
echo 'addSbtPlugin("org.ensime" % "ensime-sbt" % "0.3.3")'     >> ~/.sbt/0.13/plugins/plugins.sbt
curl -fLo ~/.vim/autoload/plug.vim --create-dirs https://raw.githubusercontent.com/junegunn/vim-plug/master/plug.vim
sbt gen-ensime
```

## Troubleshooting & tips

- FAILED DOWNLOADS messages for the first SBT build: retry later. With SBT, as with all these dependency managers ( Maven, NMP), given the large amount of downloading from multiple sources, it is often the case that the first time, not everything is there.
- in case of troubles in build, delete `target/` directory
- create eclipse configurations with the "eclipse" command in sbt or activator, in the project directory scala/forms\_play/ :

```
    eclipse with-source=true
```
- to remove the red errors in eclipse in Play! project, apply this workaround: http://stackoverflow.com/posts/28551583/revisions

Useful SBT plugins you may install for developppement purposes (add in ~/.sbt/0.13/plugins/plugins.sbt ):

	addSbtPlugin("org.ensime" % "ensime-sbt" % "0.3.3")
	addSbtPlugin("net.virtual-void" % "sbt-dependency-graph" % "0.8.2")
	addSbtPlugin("com.timushev.sbt" % "sbt-updates" % "0.1.10")

Commands to run with these useful plugins:

	dependencyUpdates
	dependencyTree
	test:dependencyGraph

See: https://github.com/jrudolph/sbt-dependency-graph , https://github.com/rtimush/sbt-updates

## Debug
See 
[playframework documentation/2.3.x/IDE](https://www.playframework.com/documentation/2.3.x/IDE)

Activator or SBT work the same way :

    sbt -jvm-debug 9999 run

#Database Administration
## Preloading RDF content

*CAUTION:*
Do not load data in a named graph (argument --graph below) whose name is a relative URI, like "blabla", or "a/b/c" . Use an absolute URI like urn:data/myorganization or an HTTP URL (see URI spec. https://tools.ietf.org/html/rfc3986 ).

The server must not be started, because Jena TDB does not allow access to the database on disk from 2 different processes.

- Preloading common vocabularies, and preloading some pre-defined form specifications ( currently FOAF ) : in activator (or sbt) shell, type:
```
    runMain deductions.runtime.sparql_cache.PopulateRDFCache
    // or, just some forms specs:
    runMain tdb.tdbloader --loc=TDB --graph=urn:form_specs ../forms/form_specs/foaf.form.ttl
```
- Preloading a local file: in activator shell type: for example:

```
    # load a FOAF profile from a local file :
    runMain tdb.tdbloader --loc=TDB --graph=http://jmvanel.free.fr/jmv.rdf#me /home/jmv/data/foaf/jmv.rdf
    # With Jena it is possible to directly load from Internet:
    runMain tdb.tdbloader --loc=TDB --graph=http://jmvanel.free.fr/jmv.rdf#me http://jmvanel.free.fr/jmv.rdf#me 
```
The typical pattern for data is to load in a graph named after the URI source, so that the cache feature works using the URI source timestamp.

CAUTION: do not load data or configuration into the un-named (default) graph. It would not be taken in account by the framework.

- dumping all database:

```
for f in lib/*.jar
do
  JARS=$JARS:$f
done
echo java -cp $JARS
java -cp $JARS tdb.tdbdump --loc=TDB > dump.nq
```
Or with sbt or activator:

    sbt "runMain tdb.tdbdump --loc=TDB" > dump.nq

To re-load the database from N-Triples format (possibly delete the TDB directory before) :

    sbt "runMain tdb.tdbloader --loc=TDB dump.nq"

## Updading RDF content

The server must not be started while updading RDF content, because Jena TDB does not allow access to the database on disk from 2 different processes.

Take inspiration from these scripts in [forms\_play](https://github.com/jmvanel/semantic_forms/tree/master/scala/forms_play)

    dump.sh       graphload.sh	 graphremove.sh     populateRDFCache.sh  tdbsearch.sh
    graphdump.sh  graphnamedlist.sh  tdbquery.sh

For example, to update the I18N translations:

    GRAPH=rdf-i18n
    echo "DROP GRAPH <$GRAPH>" > /tmp/delete_graph.rq    
    java -cp $JARS tdb.tdbupdate --loc=TDB --update=/tmp/delete_graph.rq
    java -cp $JARS deductions.runtime.sparql_cache.RDFI18NLoader

To update the Common Form Specifications :

    GRAPH=form_specs
    echo "DROP GRAPH <$GRAPH>" > /tmp/delete_graph.rq    
    java -cp $JARS tdb.tdbupdate --loc=TDB --update=/tmp/delete_graph.rq
    java -cp $JARS deductions.runtime.sparql_cache.FormSpecificationsLoader

To update everything (but better delete some named graph before, like above, or below) :

    java -cp $JARS deductions.runtime.sparql_cache.PopulateRDFCache

In the case when one wants to override some triples that are already loaded in graph G, and taking in account the language of the literals:

    java -cp $JARS deductions.runtime.jena.DataSourceManagerApp file:///home/user/newTriples.tll G

For each triple `?S ?P "val"@lang.` in newTriples.tll , this will remove the existing triple:  `?S ?P "old val"@lang.` , and add the new triple: `?S ?P "val"@lang.` 

To delete named graph related to common vocabularies, default form specifications, and IN18 translations, run this:

    java -cp $JARS deductions.runtime.jena.ResetRDFCache


## SPARQL queries
There is a web page for SPARQL queries, but not a real SPARL endpoint yet :( .
This is an HTML page for entering queries, in the /tools page.
There are some example showing some queries that you can paste into your browser:

The service
<code>/sparql?query=</code>
is only for CONSTRUCT queries.

and the service
<code>/select?query=</code>
is only for SELECT queries.

## Populating with dbPedia (mirroring dbPedia)

There are 2 scripts, one for downloading, the other for populating the triple database.

    download-dbpedia.sh
    populate_with_dbpedia.sh

In `dbpedia-dbpedia.sh`you should set the LANG variable for the language you want for the rdfs:label and other properties. You can run the script several times with different LANG if several languages are needed.

    LANG=fr
    VERSION=2015-04

After the database if populated with dbPedia data, you should run this program to index with Lucene or SOLR the newly added text (see next paragraph).
[TextIndexer.scala](https://github.com/jmvanel/semantic_forms/blob/master/scala/forms/src/main/scala/deductions/runtime/jena/TextIndexer.scala)

# Text indexing with Lucene or SOLR

For details please look the Jena TDB documentation about text search with Lucene or SOLR in TDB :
https://jena.apache.org/documentation/query/text-query.html

If you run semantic\_forms from scratch, text search with Lucene is activated by default.
You have nothing to do, the necessary Lucene libraries are present, and the RDF content added is indexed in Lucene as soon as it is added.

The settings for text search with Lucene or SOLR in TDB here:
https://github.com/jmvanel/semantic\_forms/blob/master/scala/forms/src/main/scala/deductions/runtime/jena/LuceneIndex.scala
https://github.com/jmvanel/semantic\_forms/blob/master/scala/forms/src/main/scala/deductions/runtime/services/DefaultConfiguration.scala

If you deactivate `useTextQuery` in DefaultConfiguration.scala, the text search is done by a plain SPARLQL search, that considers input as a regular expression.

If the text indexing with Lucene or SOLR is activated *after* adding RDF data, you can run this program to index with Lucene or SOLR the newly added text:
[TextIndexer.scala](https://github.com/jmvanel/semantic_forms/blob/master/scala/forms/src/main/scala/deductions/runtime/jena/TextIndexer.scala)

    runMain deductions.runtime.jena.TextIndexer

*NOTE:*
Since the association between TDB and Lucene is set by launching semantic\_forms, if you just add RDF content with tdb.tdbloader, you must afterwards index this content by running TextIndexer.

To use Lucene with TDB , here is the code to configure (I don't use the TTL config. as its vocab' is not documented in TTL ) :
https://github.com/jmvanel/semantic\_forms/blob/master/scala/forms/src/main/scala/deductions/runtime/jena/LuceneIndex.scala
This code is used in trait RDFStoreLocalJenaProvider in method `createDatabase(database_location: String)` .

Here is were an existing RDF content is indexed:
https://github.com/jmvanel/semantic\_forms/blob/master/scala/forms/src/main/scala/deductions/runtime/jena/TextIndexer.scala

And here is where the text search is called:
https://github.com/jmvanel/semantic\_forms/blob/master/scala/forms/src/main/scala/deductions/runtime/services/StringSearchSPARQL.scala


# How to debug

Start Activator or SBT with -jvm-debug argument; then type run. Then start a remove debug in eclipse or another IDE with port 9999.
```
    ./activator -jvm-debug
```

or:
```
    sbt  -jvm-debug 9999
```

# Vocabulary for forms


Here is the OWL vocabulary for ontolgy aware forms, currently used in this application:
[vocabulary/forms.owl.ttl](../../vocabulary/forms.owl.ttl)

It is useful to have, upstream of the FA (Abstract Form) in defined class FormModule.FormSyntax, an RDF vocabulary for forms.
It is not reusing Fresnel, but it could be aligned to Fresnel is necessary.

See also (in french) :
[doc/fr/formulaires.html](http://htmlpreview.github.io/?https://github.com/jmvanel/semantic_forms/blob/master/doc/fr/formulaires.html)

# Customize the plain vanilla application

semantic\_forms is really a framework, so it is made to override any feature.

The recommended way is to fork the project on github.
This fork that will never be merged (no Pull Request). It is a way to overcome the fact that there is no way (AFAIK) to redefine elements of a Play! project from an independent project.

To update with respect to semantic\_forms, see:

https://help.github.com/articles/syncing-a-fork/

And we must first do once and for all in every working directory from a git clone:
https://help.github.com/articles/configuring-a-remote-for-a-fork/

	git remote add upstream https://github.com/jmvanel/semantic_forms.git

An exemple of such a project is:
[https://github.com/deductions/semantic\_forms-sharecop](https://github.com/deductions/semantic_forms-sharecop)

See also:
[semantic\_forms wiki: Application-development-manual](https://github.com/jmvanel/semantic_forms/wiki/Application-development-manual)

# Community

For discussions that don't fit in the issues tracker, you may try 
the #eulergui IRC channel on freenode using a dedicated IRC client like XChat connecting to 
[irc://irc.freenode.net:6667/eulergui](irc://irc.freenode.net:6667/eulergui)
,
 or using the freenode HTML interface, for quick real time socializing.

Or [https://gitter.im/jmvanel/semantic\_forms](https://gitter.im/jmvanel/semantic_forms)

## Contributing

To ensure that all commits are formatted in a consistent way, it is strongly advised to use scalariform.

Please read how-to on the Banana-RDF project:
[ide-setup](https://github.com/w3c/banana-rdf/#contributions)


# The features 

The features are listed here for convenience, but from now on, we manage features on 
[Github issues](https://github.com/jmvanel/semantic_forms/issues).

## Features list 
- 1. a SPARQL 1.1 server available - DONE
- 2. user enters an URI and form view appears with the data from Internet - DONE
- 2.1 user enters an URI and form view appears with the data from the SPARQL server (RDF cache) - DONE
- 3. URIs in the form can be clicked to display another form with the data from Internet - DONE
- 4. entering new triples for existing properties, as in DataGUI or as in Ontowiki: http://aksw.org/source/edit ; by JavaScript - DONE
- 5. introduce the RDF cache, - implemented - DONE: unit test
- 6. creation of a new URI infering form from its class, as DomainApplication does - DONE
- 10 simple vocab' to specify properties by class in form - DONE
- 9. creating or editing URI's : propose URI's in relation with rdfs:domain class value; by JavaScript; could use the timestamps to order the URI's - DONE
- 13 Migration to Banana 0.7  - DONE ( 0.8.2-SNAPSHOT )
- 17 View SPARQL select & construct results : DONE
- 22 Add a button to edit the currently displayed URI : DONE
- 24   (HTML) : add CSS classes for labels and values; - DONE
- 24.1 (HTML) : new HTML output with CSS rendering instead of explicit HTML table formatting - DONE
- 25   (HTML) : add component to enter a dbpedia URI ( use dbpedia lookup API , by JavaScript ) - DONE
- 27   (HTML) : add component to enter a choice (single or multiple) for owl:OneOf classes ( by JavaScript or HTML 5 ) - DONE
- 28 for each URI, display a summary of the resource (rdfs:label, foaf:name, etc, depending on what is present in instance and of the class) instead of the URI
- 38 RDF Vocabulary for forms : DONE ; with details for each field ( see below ) : TODO
- 8. datatype validation : integer, date, telephone, .. ( by HTML5 CSS )
- 15 ScalaJS migration
- 20 Inferences for forms : implement owl union
- 21 write some JavaScript samples to call the different features
- 31 write a small help page explaining the role of local database in relation with the external data downloaded
- 32 TEST : Gatling stress scenario(s)
- 40 popup an editor for editing large texts

## Unimplemented features 
- 2.2 show statistics about the current document : # of triples, # of properties, # of URI's
- 2.3 when document pointed by URI entered by user has no triple with that URI as subject, show a list of URI's in the document like the search results
- 7. use HTTP HEAD to distinguish RDF content types and others, and have different hyperlinks and styles for HTML, RDF, and image URL's - WIP
- 7.1 have icons to distinguish content types, to display near hyperlinks for HTML, RDF, image, etc URL's
- 11 button to remove a triple; by JavaScript - TODO
- 12 Integrate non-blocking: Future, Iteratee Enumerator -WIP
- 14 Migration to BigData(R) - TODO
- 16 Dashboard : # of triples, # of of resources; # of resources of each type - TODO
- 17.1 add FlintSPARQLEditor, but this implies to launch SPARQL HTTP server like Fuseki, or BigData, or to use the SPARQL protocol in the semantic\_forms server
- 18 Search : search also in URI strings - TODO
- 19 Add simple login : WIP, done in https://github.com/jmvanel/corporate\_risk/
- 19.1 record who did what : a solution is to use a named graph for each user  : done in https://github.com/jmvanel/corporate\_risk/
- 20 Inferences for forms : eliminate archaic properties
- 23 display the "reverse" triples ( called in-going links in BigData workbench ) : TODO , but there is a reverse links button on each triple
- 26 (HTML) : add component to enter an ordered RDF list : use same mechanism as multiple values, but send a Turtle list in parentheses; by JavaScript : TODO
- 28 for each URI, display a summary of the resource (rdfs:label, foaf:name, etc : this can use existing specifications of properties in form by class : [foaf.form.ttl](../forms/form_specs/foaf.form.ttl)
- 29 have a kind of merge in case of diverging modifications in the local endpoint and the original URI : TODO
- 30 (from Dario) : Separation of the attributes of a peer and the list of connected peers: on the left a list with the peer in question and all peers (connected) in its ecosystem and on the right a list of attributes the selected peer : TODO
	* in the left list one should be able to click on a peer so that it becomes the selected peer and its ecosystem appears (and updating on the right with its attributes)
- 32 TEST : write Selenium scenario(s) : TODO

- 33 framework to orchestrate a series of questions to user when the data is not present in database : in https://github.com/jmvanel/corporate\_risk/ there are form groups and automatic navigation to  next form by SAVE button - TODO componentize this
- 35 enforce mandatory properties ( by JavaScript )
- 36 display properties of blank nodes being objects of current form subject : TODO
- 37 datatype date : display a calendar ( by JavaScript ) : TODO
- 39 custom HTML : have an easy way to customize generated HTML forms and fields, by JavaScript or HTML : TODO

