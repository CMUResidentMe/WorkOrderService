package org.residentme.workorder.repository;

import org.residentme.workorder.entity.DetailedWorkOrder;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
//import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface DetailedWorkOrderRepository extends ReactiveMongoRepository<DetailedWorkOrder, String> {

}
