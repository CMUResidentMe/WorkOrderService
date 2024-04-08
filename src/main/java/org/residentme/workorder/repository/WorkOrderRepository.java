package org.residentme.workorder.repository;

import org.residentme.workorder.entity.WorkOrder;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
//import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkOrderRepository extends ReactiveMongoRepository<WorkOrder, String> {

}
