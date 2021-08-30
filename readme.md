# Property graph version of the JOB

This maven project contains the code to transform the relational data from the Join Order Benchmark (JOB)
into a property graph instance.

All queries of the JOB are manually translated into openCypher variants
that can be executed against the property graph instance.

The property graph instance and openCypher queries are used in the experiments of:
"A General Cardinality Estimation Framework for Subgraph Matching in Property Graphs."
Wilco van Leeuwen, George Fletcher, and Nikolay Yakovets.
arXiv preprint arXiv:2108.05275 (2021).
https://arxiv.org/pdf/2108.05275.pdf


## Obtaining the property graph instance
Obtain the relational data that is used by the JOB (IMDB data set from May 2013):
 - https://github.com/gregrahn/join-order-benchmark
 - https://homepages.cwi.nl/~boncz/job/readme.txt
 - https://homepages.cwi.nl/~boncz/job/imdb.tgz


Use the file ``PrepForNeo4J.java`` to translate the relational data into a property graph instance.
Specify the directory of the uncompressed imdb.tgz using cmd option '-dataDir'.
Specify the output directory using cmd option '-outDir'.

```
mvn clean compile
mvn exec:java -Dexec.mainClass=PrepForNeo4J -Dexec.args="-dataDir D:\\dataSets\\imdb\\ -outDir D:\\dataSets\\pgImdb\\"
```

## Loading the property graph instance in Neo4j
The generated property graph instance can be loaded into Neo4j.
See for details: https://neo4j.com/docs/operations-manual/current/tutorial/neo4j-admin-import/

- Place files of the property graph instance in the import folder of a Neo4j Database (Database should be newly created and not containing any data)
- Execute using the neo4j-admin.bat script:
```
bin\neo4j-admin.bat import --database=neo4j --id-type=STRING --ignore-empty-strings --nodes=import/akaName.csv --nodes=import/akaTitle.csv --nodes=import/castInfoVertices.csv --nodes=import/character.csv --nodes=import/company_name.csv --nodes=import/complCastInfoVertices.csv --nodes=import/infoVertices.csv --nodes=import/keyword.csv --nodes=import/person.csv --nodes=import/personInfoVertices.csv --nodes=import/titles.csv --relationships=import/akaNameEdges.csv --relationships=import/akaTitleEdges.csv --relationships=import/castInfoEdges.csv --relationships=import/complCastInfoEdges.csv --relationships=import/episodeOfEdges.csv --relationships=import/infoEdges.csv --relationships=import/keywordEdges.csv --relationships=import/linkTypeEdges.csv --relationships=import/movie_companies.csv --relationships=import/personInfoEdges.csv
```








