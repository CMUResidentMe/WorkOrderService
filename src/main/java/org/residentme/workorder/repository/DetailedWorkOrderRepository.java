package org.residentme.workorder.repository;

import org.residentme.workorder.entity.DetailedWorkOrder;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DetailedWorkOrderRepository extends ReactiveMongoRepository<DetailedWorkOrder, String> {

}
