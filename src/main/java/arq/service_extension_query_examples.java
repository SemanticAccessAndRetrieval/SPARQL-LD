/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package arq;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

/**
 *
 * @author Pavlos Fafalios (fafalios@ics.forth.gr, fafalios@csd.uoc.gr)
 */
public class service_extension_query_examples {

    private static String QUERY_TO_RUN; // The query to run

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {


        // Create an empty in-memory model and populate it with the DBpedia triples describing Yellowfin tuna
        Model model = ModelFactory.createDefaultModel();
        model.read("http://dbpedia.org/data/Yellowfin_tuna.n3");

        String get_all_local_triples =
                "SELECT *  "
                + " WHERE { "
                + "      ?x ?y ?z "
                + " } ";

        String get_all_triples_from_rdfa_web_page =
                "SELECT * "
                + "WHERE { "
                + "  SERVICE <http://users.ics.forth.gr/~fafalios/> { "
                + "    ?x ?y ?z  "
                + "  } "
                + "} ";

        String query_the_result_of_an_annotation_service =
                "PREFIX oa: <http://www.w3.org/ns/oa#> "
                + "PREFIX oae: <http://www.ics.forth.gr/isl/oae/core#> "
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> "
                + "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "PREFIX dbpedia: <http://dbpedia.org/resource/> "
                + "SELECT DISTINCT ?detectedEntity ?categoryName (count(?position) as ?NumOfOccurrences) "
                + "WHERE { "
                + "   SERVICE <http://dbpedia.org/resource/Thunnus> { "
                + "       dbpedia:Thunnus foaf:isPrimaryTopicOf ?page  "
                + "   } "
                + "   VALUES ?templ { <http://139.91.183.72/x-link-marine/api?categories=fish;country&url=PAGE> }  BIND(REPLACE(str(?templ), 'PAGE', str(?page), 'i') as ?x) BIND(URI(?x) as ?serv) "
                + "   SERVICE ?serv { "
                + "     ?annot oa:hasBody ?ent . "
                + "     ?ent oae:regardsEntityName ?detectedEntity ; "
                + "          oae:position ?position ; "
                + "          oae:belongsTo ?category . ?category rdfs:label ?categoryName } "
                + "} GROUP BY ?detectedEntity ?categoryName ORDER BY DESC(?NumOfOccurrences) ";


        String query_rdfa_and_derefUri =
                "PREFIX foaf: <http://xmlns.com/foaf/0.1/> "
                + "SELECT DISTINCT ?authorName (count(?paper2) as ?numOfPapers) "
                + "WHERE { "
                + "  SERVICE <http://users.ics.forth.gr/~fafalios/> { "
                + "    ?paper1 <http://purl.org/dc/terms/creator> ?author "
                + "    FILTER(?author != <http://dblp.l3s.de/d2r/resource/authors/Pavlos_Fafalios>) } "
                + "  SERVICE ?author { "
                + "    ?author foaf:name ?authorName . "
                + "    ?paper2 <http://purl.org/dc/elements/1.1/creator> ?author } "
                + "} GROUP BY ?authorName ORDER BY DESC(?numOfPapers) ";


        String query_rdfa_and_derefUri_and_endpoint =
                "SELECT DISTINCT ?authorURI (count(?paper) AS ?numOfPapers) (count(distinct ?series) AS ?numOfDiffConfs) WHERE { "
                + "  SERVICE <http://users.ics.forth.gr/~fafalios> { "
                + "   ?p <http://purl.org/dc/terms/creator> ?authorURI } "
                + "  SERVICE ?authorURI { "
                + "   ?paper <http://purl.org/dc/elements/1.1/creator> ?authorURI } "
                + "  SERVICE <http://dblp.l3s.de/d2r/sparql> { "
                + "   ?p2 <http://purl.org/dc/elements/1.1/creator> ?authorURI . "
                + "   ?p2 <http://swrc.ontoware.org/ontology#series> ?series } "
                + "} GROUP BY ?authorURI ORDER BY ?numOfPapers ";

        // Set the query to run
        QUERY_TO_RUN = get_all_triples_from_rdfa_web_page;

        // Create a Query object
        Query query = QueryFactory.create(QUERY_TO_RUN);

        // Execute the query and obtain results
        QueryExecution qe = QueryExecutionFactory.create(query, model);
        ResultSet results = qe.execSelect();

        // Output query results	
        ResultSetFormatter.out(System.out, results, query);

        // Free up resources used running the query
        qe.close();
    }
}
