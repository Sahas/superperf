package con.turvo.perf.mongo;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import com.mongodb.BasicDBObject;

@Component
public class BaseMongoService {
	
	private MongoTemplate mongoTemplate;

	public MongoTemplate getMongoTemplate() {
		return mongoTemplate;
	}

	@Autowired
	public void setMongoTemplate(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}
	
	
	public void createCollection(String collectionName) {
		this.mongoTemplate.createCollection(collectionName);
	}
	
	public List<BasicDBObject> getList(String collectionName, List<Criteria> criterias){
		Query searchQuery = new Query();
		for (Criteria criteria : criterias) {
			searchQuery.addCriteria(criteria);
		}
		return get(collectionName,searchQuery);
	}
	
	public List<BasicDBObject> get(String collectionName, Query searchQuery){
		List<BasicDBObject> docs = getMongoTemplate().find(searchQuery, BasicDBObject.class, collectionName);
		return docs;
	}
	
}
