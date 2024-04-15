package org.residentme.workorder.controller;


import graphql.GraphQLError;
import org.residentme.workorder.builder.imp.WorkOrderDirector;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import org.residentme.workorder.data.EntryPermission;
import org.residentme.workorder.data.WorkStatus;
import org.residentme.workorder.dto.WorkOrderDTO;
import org.residentme.workorder.entity.BasicWorkOrder;
import org.residentme.workorder.entity.DetailedWorkOrder;
import org.residentme.workorder.exception.NoneWorkOrderException;
import org.residentme.workorder.kafka.MsgProducer;
import org.residentme.workorder.repository.WorkOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Controller;

@Controller
public class WorkOrderCtrl {
	
	private static final Logger logger = LoggerFactory.getLogger(WorkOrderCtrl.class);
	
	@Autowired
	private WorkOrderRepository woRepository;
	
	@Autowired
	private MsgProducer msgProducer;

	@Autowired
	WorkOrderDirector workOrderDirector;
	
	@QueryMapping
	public Flux<BasicWorkOrder> workOrders() {
		return woRepository.findAll();
	}

	@QueryMapping
	public Mono<BasicWorkOrder> findWorkOrder(@Argument String uuid) {
		return woRepository.findById(uuid)
				.switchIfEmpty(Mono.error(new NoneWorkOrderException("Work order does not exist: " + uuid)));
	}


	@QueryMapping
	public Flux<BasicWorkOrder> findWorkOrdersByOwner(@Argument String ownerUuid) {
		logger.info("Querying work orders by owner UUID: {}", ownerUuid);
		BasicWorkOrder workOrder = new BasicWorkOrder();
		workOrder.setOwner(ownerUuid);
		ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("owner", match -> match.exact());
		return woRepository.findAll(Example.of(workOrder, matcher));
	}

	@QueryMapping
	public Flux<BasicWorkOrder> findWorkOrdersByAssignedStaff(@Argument String assignedStaffUuid) {
		logger.info("Querying work orders by assigned staff UUID: {}", assignedStaffUuid);
		BasicWorkOrder workOrder = new BasicWorkOrder();
		workOrder.setAssignedStaff(assignedStaffUuid);
		ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("assignedStaff", match -> match.exact());
		return woRepository.findAll(Example.of(workOrder, matcher));
	}


	@MutationMapping
	public Mono<WorkOrderDTO> createWorkOrder(@Argument String owner, @Argument String workType, @Argument String priority,
																						@Argument String detail, @Argument String assignedStaff,
																						@Argument Optional<String> accessInstruction, @Argument Optional<String> preferredTime,
																						@Argument Optional<EntryPermission> entryPermission) {

		if (accessInstruction.isPresent() || preferredTime.isPresent() || entryPermission.isPresent()) {
			workOrderDirector.constructDetailedWorkOrder(owner, workType, priority, detail, assignedStaff, accessInstruction, preferredTime, entryPermission);
		} else {
			workOrderDirector.constructBasicWorkOrder(owner, workType, priority, detail, assignedStaff);
		}

		return Mono.fromCallable(workOrderDirector::build)
				.flatMap(woRepository::save)
				.doOnSuccess(workOrder -> {
					WorkOrderDTO res = new WorkOrderDTO(workOrder);
					msgProducer.sendWorkOrderCreated(res);
				})
				.map(WorkOrderDTO::new);
	}



	@MutationMapping
	public Mono<BasicWorkOrder> changeWorkOrder(@Argument String uuid, @Argument String workType, @Argument String priority,
																							@Argument String detail, @Argument String assignedStaff,
																							@Argument String accessInstruction, @Argument String preferredTime, @Argument EntryPermission entryPermission) {
		return woRepository.findById(uuid)
				.flatMap(workOrder -> {
					if (workType != null && !workType.isEmpty())
						workOrder.setWorkType(workType);
					if (priority != null)
						workOrder.setPriority(priority);
					if (detail != null && !detail.isEmpty())
						workOrder.setDetail(detail);
					if (assignedStaff != null && !assignedStaff.isEmpty())
						workOrder.setAssignedStaff(assignedStaff);

					if (workOrder instanceof DetailedWorkOrder) {
						DetailedWorkOrder detailed = (DetailedWorkOrder) workOrder;
						if (accessInstruction != null && !accessInstruction.isEmpty())
							detailed.setAccessInstruction(accessInstruction);
						if (preferredTime != null && !preferredTime.isEmpty())
							detailed.setPreferredTime(preferredTime);
						if (entryPermission != null)
							detailed.setEntryPermission(entryPermission.value());
					}

					msgProducer.sendWorkOrderChanged(new WorkOrderDTO(workOrder));
					return woRepository.save(workOrder);
				})
				.switchIfEmpty(Mono.error(new NoneWorkOrderException("unexist workorder: " + uuid)));
	}


	@MutationMapping
	public Mono<BasicWorkOrder> updateWorkOrderStatus(@Argument String uuid, @Argument WorkStatus status) {
		return woRepository.findById(uuid)
				.flatMap(workOrder -> {
					workOrder.setStatus(status.value());
					msgProducer.sendWorkOrderChanged(new WorkOrderDTO(workOrder));
					return woRepository.save(workOrder);
				})
				.switchIfEmpty(Mono.error(new NoneWorkOrderException("Work order does not exist: " + uuid)));
	}


	@MutationMapping
	public Mono<DetailedWorkOrder> uploadImages(@Argument String uuid, @Argument List<String> images) {
		return woRepository.findById(uuid)
				.flatMap(workOrder -> {
					if (workOrder instanceof DetailedWorkOrder) {
						DetailedWorkOrder detailedOrder = (DetailedWorkOrder) workOrder;
						// Assuming method to set images exists
						detailedOrder.setImages(images);
						msgProducer.sendWorkOrderChanged(new WorkOrderDTO(detailedOrder));
						return woRepository.save(detailedOrder);
					} else {
						return Mono.error(new IllegalStateException("Cannot upload images to a basic work order"));
					}
				})
				.switchIfEmpty(Mono.error(new NoneWorkOrderException("Work order does not exist: " + uuid)));
	}


	@MutationMapping
	public Mono<String> cancelWorkOrder(@Argument String uuid) {
		return woRepository.findById(uuid)
				.flatMap(workOrder -> {
					msgProducer.sendWorkOrderDeleted(new WorkOrderDTO(workOrder));
					return woRepository.deleteById(uuid).thenReturn(uuid);
				})
				.switchIfEmpty(Mono.error(new NoneWorkOrderException("Work order does not exist: " + uuid)));
	}

	
	@GraphQlExceptionHandler
    public GraphQLError handle(BindException ex) {
        return GraphQLError.newError().errorType(ErrorType.BAD_REQUEST).message(ex.getMessage()).build();
    }
	
	@GraphQlExceptionHandler
    public GraphQLError handle(NoneWorkOrderException ex) {
        return GraphQLError.newError().errorType(ErrorType.BAD_REQUEST).message(ex.getMessage()).build();
    }
	
}
