package org.residentme.workorder.repository;

import org.residentme.workorder.entity.BasicWorkOrder;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
//import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface WorkOrderRepository extends ReactiveMongoRepository<BasicWorkOrder, String> {

  Flux<BasicWorkOrder> findAllByAssignedStaffIsNullOrderByPriorityDesc();
}
