package org.residentme.workorder.controller;


import graphql.GraphQLError;
import org.residentme.workorder.builder.imp.WorkOrderDirector;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import org.residentme.workorder.data.EntryPermission;
import org.residentme.workorder.data.Priority;
import org.residentme.workorder.data.WorkStatus;
import org.residentme.workorder.dto.WorkOrderDTO;
import org.residentme.workorder.entity.DetailedWorkOrder;
import org.residentme.workorder.exception.NoneWorkOrderException;
import org.residentme.workorder.kafka.MsgProducer;
import org.residentme.workorder.repository.DetailedWorkOrderRepository;
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
	private DetailedWorkOrderRepository detailedWoRepository;
	
	@Autowired
	private MsgProducer msgProducer;

	@Autowired
	WorkOrderDirector workOrderDirector;
	
	@QueryMapping
	public Flux<DetailedWorkOrder> workOrders() {
		return detailedWoRepository.findAll();
	}

	@QueryMapping
	public Mono<DetailedWorkOrder> findWorkOrderById(@Argument String uuid) {
		return detailedWoRepository.findById(uuid)
				.switchIfEmpty(Mono.error(new NoneWorkOrderException("Work order does not exist: " + uuid)));
	}


	@QueryMapping
	public Flux<DetailedWorkOrder> findWorkOrdersByOwner(@Argument String ownerUuid) {
		logger.info("Querying work orders by owner UUID: {}", ownerUuid);
		DetailedWorkOrder workOrder = new DetailedWorkOrder();
		workOrder.setOwner(ownerUuid);
		ExampleMatcher matcher = ExampleMatcher.matchingAll().withMatcher("owner", match -> match.exact()).withIgnorePaths("uuid");
		Flux<DetailedWorkOrder> res = detailedWoRepository.findAll(Example.of(workOrder, matcher));
		logger.debug(res.toString());
		return res;
	}
	
	@QueryMapping
	public Flux<DetailedWorkOrder> findWorkOrdersUnassined(){
		DetailedWorkOrder workOrder = new DetailedWorkOrder();
		workOrder.setAssignedStaff(null);
		ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("assignedStaff", match -> match.exact()).withIgnorePaths("uuid");
		return detailedWoRepository.findAll(Example.of(workOrder, matcher));
	}

	@QueryMapping
	public Flux<DetailedWorkOrder> findWorkOrdersByAssignedStaff(@Argument String assignedStaffUuid) {
		logger.info("Querying work orders by assigned staff UUID: {}", assignedStaffUuid);
		DetailedWorkOrder workOrder = new DetailedWorkOrder();
		workOrder.setAssignedStaff(assignedStaffUuid);
		ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("assignedStaff", match -> match.exact()).withIgnorePaths("uuid");
		return detailedWoRepository.findAll(Example.of(workOrder, matcher));
	}


	@MutationMapping
	public Mono<DetailedWorkOrder> createWorkOrder(@Argument String owner, @Argument String workType, @Argument Priority priority,
												@Argument String preferredTime, @Argument EntryPermission entryPermission,  @Argument String accessInstruction, 
												@Argument String detail, @Argument List<String> images) {
		workOrderDirector.constructDetailedWorkOrder(owner, workType, priority, preferredTime, entryPermission, accessInstruction, detail, images);
		DetailedWorkOrder wk = new DetailedWorkOrder(workOrderDirector.build());
		wk = detailedWoRepository.save(wk).block();
		WorkOrderDTO res = new WorkOrderDTO(wk);
		msgProducer.sendWorkOrderCreated(res);
		return Mono.just(wk);
	}
	
	@MutationMapping
	public Mono<DetailedWorkOrder> changeWorkOrder(@Argument String uuid, @Argument Optional<String> workType, @Argument Optional<Priority> priority,
												@Argument Optional<String> detail, @Argument Optional<String> accessInstruction, 
												@Argument Optional<String> preferredTime, @Argument Optional<EntryPermission> entryPermission, 
												@Argument Optional<List<String>> images) {
		return detailedWoRepository.findById(uuid).map(Optional::of).defaultIfEmpty(Optional.empty()).flatMap(optionalWk -> {
			if (optionalWk.isPresent()) {
				DetailedWorkOrder findWk = optionalWk.get();
				if(workType.isPresent()) findWk.setWorkType(workType.get());
				if(priority.isPresent()) findWk.setPriority(priority.get().value());
				if(detail.isPresent()) findWk.setDetail(detail.get());
				if(accessInstruction.isPresent()) findWk.setAccessInstruction(accessInstruction.get());
				if(preferredTime.isPresent()) findWk.setPreferredTime(preferredTime.get());
				if(entryPermission.isPresent()) findWk.setEntryPermission(entryPermission.get().value());
				if(images.isPresent()) findWk.setImages(images.get());
				msgProducer.sendWorkOrderChanged(new WorkOrderDTO(findWk));
				return detailedWoRepository.save(findWk);
			}
			throw new NoneWorkOrderException("unexist workorder: " + uuid);
		});
	}

	@MutationMapping
	public Mono<DetailedWorkOrder> updateWorkOrderStatus(@Argument String uuid, @Argument WorkStatus status) {
		return detailedWoRepository.findById(uuid).map(Optional::of).defaultIfEmpty(Optional.empty()).flatMap(optionalWk -> {
			if (optionalWk.isPresent()) {
				DetailedWorkOrder findWk = optionalWk.get();
				findWk.setStatus(status.value());
				msgProducer.sendWorkOrderChanged(new WorkOrderDTO(findWk));
				return detailedWoRepository.save(findWk);
			}
			throw new NoneWorkOrderException("unexist workorder: " + uuid);
		});
	}


	@MutationMapping
	public Mono<DetailedWorkOrder> assignedStaff(@Argument String uuid, @Argument Optional<String> assignedStaff) {
		return detailedWoRepository.findById(uuid).map(Optional::of).defaultIfEmpty(Optional.empty()).flatMap(optionalWk -> {
			if (optionalWk.isPresent()) {
				DetailedWorkOrder findWk = optionalWk.get();
				if(assignedStaff.isPresent()) {
					findWk.setAssignedStaff(assignedStaff.get());
				}else {
					findWk.setAssignedStaff(null);
				}
				msgProducer.sendWorkOrderChanged(new WorkOrderDTO(findWk));
				return detailedWoRepository.save(findWk);
			}
			throw new NoneWorkOrderException("unexist workorder: " + uuid);
		});
	}


	@MutationMapping
	public Mono<String> cancelWorkOrder(@Argument String uuid) {
		return detailedWoRepository.findById(uuid).map(Optional::of).defaultIfEmpty(Optional.empty()).flatMap(optionalWk -> {
			if (optionalWk.isPresent()) {
				DetailedWorkOrder findWk = optionalWk.get();
				msgProducer.sendWorkOrderDeleted(new WorkOrderDTO(findWk));
				return detailedWoRepository.deleteById(uuid).thenReturn(uuid);
			}
			throw new NoneWorkOrderException("unexist workorder: " + uuid);
		});
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
