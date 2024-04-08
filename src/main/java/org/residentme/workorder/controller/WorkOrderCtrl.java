package org.residentme.workorder.controller;


import graphql.GraphQLError;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import org.residentme.workorder.data.EntryPermission;
import org.residentme.workorder.data.WorkStatus;
import org.residentme.workorder.entity.WorkOrder;
import org.residentme.workorder.exception.NoneWorkOrderException;
import org.residentme.workorder.kafka.MsgProducer;
import org.residentme.workorder.repository.WorkOrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.GraphQlExceptionHandler;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.execution.ErrorType;
import org.springframework.stereotype.Controller;

import com.mongodb.MongoWriteException;

@Controller
public class WorkOrderCtrl {
	
	@Autowired
	private WorkOrderRepository woRepository;
	
	@Autowired
	private MsgProducer msgProducer;
	
	@QueryMapping
	public Flux<WorkOrder> workOrders() {
		return woRepository.findAll();
	}
	
	@QueryMapping
	public Mono<WorkOrder> workOrder(@Argument String uuid){		
		return woRepository.findById(uuid).map(Optional::of).defaultIfEmpty(Optional.empty()).flatMap(optionalWk -> {
			if (optionalWk.isPresent()) {
				return Mono.just(optionalWk.get());
			}
			throw new NoneWorkOrderException("unexist workorder: " + uuid);
		});
	}
	
	@MutationMapping
	public WorkOrder buildWorkOrder(@Argument String owner, @Argument String workType, @Argument int priority, 
			@Argument String detail, @Argument String preferredtime, @Argument EntryPermission entryPermission, @Argument String accessInstruction){
		WorkOrder wk = new WorkOrder(owner, workType, priority, detail, preferredtime, entryPermission.value(), accessInstruction);
		wk = woRepository.save(wk).block();
		msgProducer.sendWorkOrderCreated(wk);
		return wk;
	}
	
	@MutationMapping
	public Mono<WorkOrder> changeWorkOrder(@Argument String uuid, @Argument String workType, @Argument int priority,
			@Argument String detail) {
		return woRepository.findById(uuid).map(Optional::of).defaultIfEmpty(Optional.empty()).flatMap(optionalWk -> {
			if (optionalWk.isPresent()) {
				WorkOrder findWk = optionalWk.get();
				if (workType != null && !workType.isEmpty())
					findWk.setWorktype(workType);
				if (priority != -1)
					findWk.setPriority(priority);
				if (detail != null && !detail.isEmpty())
					findWk.setDetail(detail);
				msgProducer.sendWorkOrderChanged(findWk);
				return woRepository.save(findWk);
			}
			throw new NoneWorkOrderException("unexist workorder: " + uuid);
		});
	}
	
	@MutationMapping
	public Mono<WorkOrder> setPreferredtime(@Argument String uuid, @Argument String preferredtime) {
		Mono<WorkOrder> wk = woRepository.findById(uuid);
		return woRepository.findById(uuid).map(Optional::of).defaultIfEmpty(Optional.empty()).flatMap(optionalWk -> {
			if (optionalWk.isPresent()) {
				WorkOrder findWk = optionalWk.get();
				findWk.setPreferredtime(preferredtime);
				msgProducer.sendWorkOrderChanged(findWk);
				return woRepository.save(findWk);
			}
			throw new NoneWorkOrderException("unexist workorder: " + uuid);
		});
	}
	
	@MutationMapping
	public Mono<WorkOrder> setEntryPermission(@Argument String uuid, @Argument EntryPermission entryPermission) {
		Mono<WorkOrder> wk = woRepository.findById(uuid);
		return woRepository.findById(uuid).map(Optional::of).defaultIfEmpty(Optional.empty()).flatMap(optionalWk -> {
			if (optionalWk.isPresent()) {
				WorkOrder findWk = optionalWk.get();
				findWk.setEntryPermission(entryPermission.value());
				msgProducer.sendWorkOrderChanged(findWk);
				return woRepository.save(findWk);
			}
			throw new NoneWorkOrderException("unexist workorder: " + uuid);
		});
	}
	
	@MutationMapping
	public Mono<WorkOrder> setAccessInstruction(@Argument String uuid, @Argument String accessInstruction) {
		Mono<WorkOrder> wk = woRepository.findById(uuid);
		return woRepository.findById(uuid).map(Optional::of).defaultIfEmpty(Optional.empty()).flatMap(optionalWk -> {
			if (optionalWk.isPresent()) {
				WorkOrder findWk = optionalWk.get();
				findWk.setAccessInstruction(accessInstruction);
				msgProducer.sendWorkOrderChanged(findWk);
				return woRepository.save(findWk);
			}
			throw new NoneWorkOrderException("unexist workorder: " + uuid);
		});
	}
	
	@MutationMapping
	public Mono<WorkOrder> setStatus(@Argument String uuid, @Argument WorkStatus status) {
		Mono<WorkOrder> wk = woRepository.findById(uuid);
		return woRepository.findById(uuid).map(Optional::of).defaultIfEmpty(Optional.empty()).flatMap(optionalWk -> {
			if (optionalWk.isPresent()) {
				WorkOrder findWk = optionalWk.get();
				findWk.setStatus(status.value());
				msgProducer.sendWorkOrderChanged(findWk);
				return woRepository.save(findWk);
			}
			throw new NoneWorkOrderException("unexist workorder: " + uuid);
		});
	}
	
	@MutationMapping
	public Mono<WorkOrder> setPriority(@Argument String uuid, @Argument int priority) {
		Mono<WorkOrder> wk = woRepository.findById(uuid);
		return woRepository.findById(uuid).map(Optional::of).defaultIfEmpty(Optional.empty()).flatMap(optionalWk -> {
			if (optionalWk.isPresent()) {
				WorkOrder findWk = optionalWk.get();
				findWk.setPriority(priority);
				msgProducer.sendWorkOrderChanged(findWk);
				return woRepository.save(findWk);
			}
			throw new NoneWorkOrderException("unexist workorder: " + uuid);
		});
	}
	
	@MutationMapping
	public Mono<WorkOrder> setAssignedstaff(@Argument String uuid, @Argument String assignedStaff) {
		Mono<WorkOrder> wk = woRepository.findById(uuid);
		return woRepository.findById(uuid).map(Optional::of).defaultIfEmpty(Optional.empty()).flatMap(optionalWk -> {
			if (optionalWk.isPresent()) {
				WorkOrder findWk = optionalWk.get();
				findWk.setAssignedstaff(assignedStaff);
				msgProducer.sendWorkOrderChanged(findWk);
				return woRepository.save(findWk);
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
