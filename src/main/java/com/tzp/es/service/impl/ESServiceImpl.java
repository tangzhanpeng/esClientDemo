package com.tzp.es.service.impl;

import com.google.gson.Gson;
import com.tzp.es.service.ESService;
import com.tzp.es.service.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.StringUtils;
import org.apache.http.HttpHost;
import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;


@Slf4j
public class ESServiceImpl implements ESService {

    private static final Gson gson = new Gson();

    public static final String HOST = "tommy_es1";

    public boolean esIndexCreate(String indexName) {
        RestHighLevelClient client = getClient();
        CreateIndexRequest request = new CreateIndexRequest(indexName);
        CreateIndexResponse createIndexResponse;
        try {
            createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("esIndexCreate err", e);
            return false;
        } finally {
            closeClient(client);
        }
        return createIndexResponse.isAcknowledged();
    }

    public GetIndexResponse esIndexSearch(String indexName) {
        RestHighLevelClient client = getClient();
        GetIndexRequest request = new GetIndexRequest(indexName);
        GetIndexResponse response;
        try {
            response = client.indices().get(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("esIndexSearch err", e);
            return null;
        } finally {
            closeClient(client);
        }
        return response;
    }

    @Override
    public boolean esIndexDeleted(String indexName) {
        RestHighLevelClient client = getClient();
        DeleteIndexRequest request = new DeleteIndexRequest(indexName);
        AcknowledgedResponse response;
        try {
             response = client.indices().delete(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            log.error("esIndexSearch err", e);
            return false;
        } finally {
            closeClient(client);
        }
        return response.isAcknowledged();
    }

    @Override
    public IndexResponse esDocInsert(String indexName, String docId, String value) {
        RestHighLevelClient client = getClient();

        IndexRequest request = new IndexRequest();
        request.index(indexName).id(docId).source(value, XContentType.JSON);

        IndexResponse response;
        try {
            response = client.index(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            return null;
        } finally {
            closeClient(client);
        }
        return response;
    }

    @Override
    public UpdateResponse esDocUpdate(String indexName, String docId, Map<String, Object> map) {
        RestHighLevelClient client = getClient();

        UpdateRequest request = new UpdateRequest();
        request.index(indexName).id(docId);
        request.doc(map);

        UpdateResponse response;
        try {
            response = client.update(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            return null;
        } finally {
            closeClient(client);
        }
        return response;
    }

    @Override
    public GetResponse esDocGet(String indexName, String docId) {
        RestHighLevelClient client = getClient();

        GetRequest request = new GetRequest(indexName, docId);

        GetResponse response;
        try {
            response = client.get(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            return null;
        } finally {
            closeClient(client);
        }
        return response;
    }

    @Override
    public DeleteResponse esDocDelete(String indexName, String docId) {
        RestHighLevelClient client = getClient();

        DeleteRequest request = new DeleteRequest(indexName, docId);

        DeleteResponse response;
        try {
            response = client.delete(request, RequestOptions.DEFAULT);
        } catch (Exception e) {
            return null;
        } finally {
            closeClient(client);
        }
        return response;
    }

    public BulkResponse esDocInsertBatch(String indexName, List<User> userList) {
        RestHighLevelClient client = getClient();

        BulkRequest request = new BulkRequest();
        userList.forEach(user -> {
            request.add(new IndexRequest().index(indexName).id(user.getId().toString()).source(gson.toJson(user), XContentType.JSON));
        });

        BulkResponse response;
        try {
            response = client.bulk(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            return null;
        } finally {
            closeClient(client);
        }
        return response;
    }

    @Override
    public BulkResponse esDocDeleteBatch(String indexName, List<String> docIds) {
        RestHighLevelClient client = getClient();

        BulkRequest request = new BulkRequest();
        docIds.forEach(docId -> request.add(new DeleteRequest(indexName, docId)));

        BulkResponse response;
        try {
            response = client.bulk(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            return null;
        } finally {
            closeClient(client);
        }
        return response;
    }

    @Override
    public SearchResponse esDocQuery(String indexName, Integer from, Integer size, String sortFiled, SortOrder sort, String[] includes, String[] excludes) {
        RestHighLevelClient client = getClient();

        SearchRequest request = new SearchRequest();
        request.indices(indexName);
        SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery());
        builder.from(from);
        builder.size(size);
        if (!sortFiled.isEmpty()) {
            builder.sort(sortFiled, sort);
        }
        builder.fetchSource(includes, excludes);
        request.source(builder);

        SearchResponse response;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            return null;
        } finally {
            closeClient(client);
        }
        return response;
    }

    @Override
    public SearchResponse esDocQuery(String indexName,String name, Object... objects) {
        RestHighLevelClient client = getClient();

        SearchRequest request = new SearchRequest();
        request.indices(indexName);

        //单条件查询
        //SearchSourceBuilder builder = new SearchSourceBuilder().query(QueryBuilders.termsQuery(name, objects));
        //组合条件查询
        SearchSourceBuilder builder = new SearchSourceBuilder();
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(QueryBuilders.matchQuery("age", 30));
        //boolQueryBuilder.must(QueryBuilders.matchQuery("sex","男"));

        boolQueryBuilder.mustNot(QueryBuilders.matchQuery("sex","男"));

        builder.query(boolQueryBuilder);

        request.source(builder);



        SearchResponse response;
        try {
            response = client.search(request, RequestOptions.DEFAULT);
        } catch (IOException e) {
            return null;
        } finally {
            closeClient(client);
        }
        return response;
    }

    private RestHighLevelClient getClient() {
        RestHighLevelClient client = new RestHighLevelClient(
                RestClient.builder(new HttpHost(HOST, 9200, "http"))
        );
        return client;
    }

    private void closeClient(RestHighLevelClient client) {
        try {
            client.close();
        } catch (IOException e) {
            log.error("closeClient err", e);
        }
    }
}
