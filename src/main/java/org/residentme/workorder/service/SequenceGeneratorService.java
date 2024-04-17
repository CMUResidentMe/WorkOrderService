package org.residentme.workorder.service;

import org.residentme.workorder.entity.DatabaseSequence;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Mono;


@Service
public class SequenceGeneratorService {

    @Autowired
    private ReactiveMongoTemplate mongoTemplate;

    public SequenceGeneratorService(ReactiveMongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public Mono<Long> generateSequence(String seqName) {
        return mongoTemplate.findAndModify(
            Query.query(Criteria.where("_id").is(seqName)),
            new Update().inc("seq", 1),
            FindAndModifyOptions.options().returnNew(true).upsert(true),
            DatabaseSequence.class)
        .map(counter -> counter != null ? counter.getSeq() : 1);
    }
}
