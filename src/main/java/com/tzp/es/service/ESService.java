package com.tzp.es.service;

import com.tzp.es.service.entity.User;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.indices.GetIndexResponse;
import org.elasticsearch.search.sort.SortOrder;

import java.util.List;
import java.util.Map;

public interface ESService {

    boolean esIndexCreate(String indexName);

    GetIndexResponse esIndexSearch(String indexName);

    boolean esIndexDeleted(String indexName);

    IndexResponse esDocInsert(String indexName, String docId, String value);

    UpdateResponse esDocUpdate(String indexName, String docId, Map<String, Object> map);

    GetResponse esDocGet(String indexName, String docId);

    DeleteResponse esDocDelete(String indexName, String docId);

    BulkResponse esDocInsertBatch(String indexName, List<User> userList);

    BulkResponse esDocDeleteBatch(String indexName, List<String> docIds);

    SearchResponse esDocQuery(String indexName, Integer from, Integer size, String sortFiled, SortOrder sort, String[] includes, String[] excludes);

    SearchResponse esDocQuery(String indexName, String name, Object ... objects);
}
