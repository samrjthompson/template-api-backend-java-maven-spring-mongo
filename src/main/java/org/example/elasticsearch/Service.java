package org.example.elasticsearch;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.GetResponse;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import java.io.IOException;
import org.springframework.stereotype.Component;

@Component
public class Service {

    private final ElasticsearchClient esClient;

    public Service(ElasticsearchClient esClient) {
        this.esClient = esClient;
    }

    // Creates new index
    public void createIndex() throws IOException {
        esClient.indices()
                .create(c -> c.index("primary_search"));
    }

    // Indexes document (create a doc in the index)
    public void indexDocument() throws IOException {
        Officer officer = new Officer();

        esClient.index(i -> i
                .index("primary_search")
                .id(officer.getId())
                .document(officer)
        );
    }

    // Gets document from index
    public Officer getOfficer() throws IOException {
        GetResponse<Officer> response =
                esClient.get(g -> g
                                .index("primary_search")
                                .id("officer_id"),
                        Officer.class
                );

        if (response.found()) {
            return response.source();
        }
        throw new RuntimeException("Not found");
    }

    public void updateDocument() throws IOException {
        Officer officer = new Officer();

        esClient.update(u -> u
                        .index("primary_search")
                        .id(officer.getId())
                        .upsert(officer),
                Officer.class
        );
    }

    public void deleteDocument() throws IOException {
        esClient.delete(d -> d
                .index("primary_search")
                .id("officer_id"));
    }

    public void deleteIndex() throws IOException {
        esClient.indices()
                .delete(d -> d
                        .index("primary_search"));
    }

    // Searches for a document
    public SearchResponse<Officer> searchDocument() throws IOException {
        String searchText = "officer_name";

        return esClient.search(s -> s
                .index("primary_search")
                .query(q -> q
                        .match(m -> m
                                .field("name")
                                .query(searchText)
                        )
                ), Officer.class
        );
    }

    // Demo Officer class
    public class Officer {

        private String id;

        public String getId() {
            return id;
        }
    }
}
