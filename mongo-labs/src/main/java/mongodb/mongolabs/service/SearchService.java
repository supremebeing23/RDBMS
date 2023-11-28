package mongodb.mongolabs.service;



import mongodb.mongolabs.persistence.entity.Institute;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SearchService {

    private final MongoTemplate mongoTemplate;

    public SearchService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Institute> findInstituteBySpeciality(final String specialityName) {
        final Criteria criteria = Criteria.where("chairs.specialities.name")
                .regex(String.format(".*%s.*", specialityName), "i");
        final Query query = Query.query(criteria);
        return mongoTemplate.find(query, Institute.class);
    }
}
