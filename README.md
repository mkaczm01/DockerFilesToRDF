# DockerFilesToRDF
Translate **Docker** config files into **RDF triples** format and save it to **RDF4J** database for querying in **SPARQL**.

## Requirments
- SWI Prolog enviroment (tested on version **7.6.3**)
- Java installed

## Usage
1. Prepare Docker files.
1. Run application and at first use configure Prolog (set path to Prolog and path to Prolog dockerfiles.pl script).
1. Load Docker files to process.
1. Choose which files you are going to process.
1. Process files.
1. Wait for a Prolog operations.
1. Now you can look at the Prolog output section to view results.
1. Choose which RDF files you want to insert to RDF4J Database.
1. Insert files to database.
1. Use default SPARQL query or write own one and run it.
1. In the table you can see result of query.

## Important 
Each one you insert files to database, there is a new instation so you add files to brand new, clear database.

## Links
- [Riccardo Tommasini homepage](http://riccardotommasini.com/dockeronto/)
- [Dockeronto repositorium](https://github.com/docker-ontologies/dockeronto)
