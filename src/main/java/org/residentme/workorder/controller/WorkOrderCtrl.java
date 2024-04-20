package org.residentme.workorder.controller;

import graphql.GraphQLError;
import org.residentme.workorder.builder.imp.WorkOrderDirector;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.residentme.workorder.data.EntryPermission;
import org.residentme.workorder.data.Priority;
import org.residentme.workorder.data.WorkStatus;
import org.residentme.workorder.dto.RmNotification;
import org.residentme.workorder.entity.DetailedWorkOrder;
import org.residentme.workorder.service.SequenceGeneratorService;
import org.residentme.workorder.exception.NoneWorkOrderException;
import org.residentme.workorder.kafka.MsgProducer;
import org.residentme.workorder.repository.DetailedWorkOrderRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
	private SequenceGeneratorService sequenceGenerator;

	@Autowired
	private DetailedWorkOrderRepository detailedWoRepository;

	@Autowired
	private MsgProducer msgProducer;

	@Autowired
	WorkOrderDirector workOrderDirector;
	
    @Value("${workorderServer.kafkaTopic}")
    private String kafkaTopic;
    
    @Value("${workorderServer.workorderCreated}")
    private String workorderCreated;
    
    @Value("${workorderServer.workorderChanged}")
    private String workorderChanged;
    
    @Value("${workorderServer.workorderDeleted}")
    private String workorderDeleted;

	@QueryMapping
	public Flux<DetailedWorkOrder> workOrders() {
		return detailedWoRepository.findAll();
	}

	@QueryMapping
	public Mono<DetailedWorkOrder> workOrder(@Argument String uuid) {
		return detailedWoRepository.findById(uuid)
				.switchIfEmpty(Mono.error(new NoneWorkOrderException("Work order does not exist: " + uuid)));
	}

	@QueryMapping
	public Flux<DetailedWorkOrder> workOrdersByOwner(@Argument String ownerUuid) {
		logger.info("Querying work orders by owner UUID: {}", ownerUuid);
		DetailedWorkOrder workOrder = new DetailedWorkOrder();
		workOrder.setOwner(ownerUuid);
		ExampleMatcher matcher = ExampleMatcher.matchingAll().withMatcher("owner", match -> match.exact())
				.withIgnorePaths("uuid");
		Flux<DetailedWorkOrder> res = detailedWoRepository.findAll(Example.of(workOrder, matcher));
		logger.debug(res.toString());
		return res;
	}

	@QueryMapping
	public Flux<DetailedWorkOrder> workOrdersUnassigned() {
		DetailedWorkOrder workOrder = new DetailedWorkOrder();
		workOrder.setAssignedStaff(null);
		ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("assignedStaff", match -> match.exact())
				.withIgnorePaths("uuid");
		return detailedWoRepository.findAll(Example.of(workOrder, matcher));
	}

	@QueryMapping
	public Flux<DetailedWorkOrder> workOrdersByAssignedStaff(@Argument String assignedStaffUuid) {
		logger.info("Querying work orders by assigned staff UUID: {}", assignedStaffUuid);
		DetailedWorkOrder workOrder = new DetailedWorkOrder();
		workOrder.setAssignedStaff(assignedStaffUuid);
		ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("assignedStaff", match -> match.exact())
				.withIgnorePaths("uuid");
		return detailedWoRepository.findAll(Example.of(workOrder, matcher));
	}

	@QueryMapping
	public Flux<DetailedWorkOrder> workOrdersByStatus(@Argument WorkStatus status) {
		logger.info("Querying work orders by status: {}", status);
		DetailedWorkOrder workOrder = new DetailedWorkOrder();
		workOrder.setStatus(status.value());
		ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("status", match -> match.exact())
				.withIgnorePaths("uuid");
		return detailedWoRepository.findAll(Example.of(workOrder, matcher));
	}

	@MutationMapping
	public Mono<DetailedWorkOrder> createWorkOrder(@Argument String owner, @Argument String workType,
			@Argument Priority priority,
			@Argument String preferredTime, @Argument EntryPermission entryPermission,
			@Argument String accessInstruction,
			@Argument String detail, @Argument List<String> images) {
		Long semanticId = sequenceGenerator.generateSequence("workorder_sequence").block();
		workOrderDirector.constructDetailedWorkOrder("WO-" + semanticId, owner, workType, priority, preferredTime,
				entryPermission, accessInstruction, detail, images);
		DetailedWorkOrder workOrder = workOrderDirector.build();
		workOrder = detailedWoRepository.save(workOrder).block();
		
		RmNotification woInfo = new RmNotification(workorderCreated);
		StringBuilder sb = new StringBuilder();
		woInfo.setOwner(owner);
		woInfo.setSourceID(workOrder.getUuid());
		sb.append("Work Order #");
		sb.append(workOrder.getSemanticId());
		sb.append(", Work Type:");
		sb.append(workType);
		sb.append(", Priority:");
		sb.append(priority);
		sb.append(", Preferred Time:");
		sb.append(preferredTime);
		sb.append(", Entry Permission:");
		sb.append(entryPermission);
		sb.append(", Detail:");
		sb.append(detail);
		woInfo.setMessage(sb.toString());
		msgProducer.sendWorkOrderCreated(woInfo);
		
		return Mono.just(workOrder);
	}

	@MutationMapping
	public Mono<DetailedWorkOrder> changeWorkOrder(@Argument String uuid, @Argument Optional<String> workType,
			@Argument Optional<Priority> priority,
			@Argument Optional<String> detail, @Argument Optional<String> accessInstruction,
			@Argument Optional<String> preferredTime, @Argument Optional<EntryPermission> entryPermission,
			@Argument Optional<List<String>> images) {
		
		return detailedWoRepository.findById(uuid).flatMap(existingWorkOrder -> {
					RmNotification changeInfo = new RmNotification(workorderChanged);
					changeInfo.setOwner(existingWorkOrder.getOwner());
					changeInfo.setSourceID(existingWorkOrder.getUuid());
					updateExistingWorkOrder(workType, priority, detail, accessInstruction,
							preferredTime, entryPermission, images, existingWorkOrder, changeInfo);
					msgProducer.sendWorkOrderChanged(changeInfo);
					if(existingWorkOrder.getAssignedStaff() != null) {
						RmNotification noti = new RmNotification(changeInfo);
						noti.setOwner(existingWorkOrder.getAssignedStaff());
						msgProducer.sendWorkOrderChanged(noti);
					}
					
					return detailedWoRepository.save(existingWorkOrder);
				}).switchIfEmpty(Mono.error(new NoneWorkOrderException("Work order does not exist: " + uuid)));
	}

	private boolean updateExistingWorkOrder(Optional<String> workType, Optional<Priority> priority,
			Optional<String> detail,
			Optional<String> accessInstruction, Optional<String> preferredTime,
			Optional<EntryPermission> entryPermission,
			Optional<List<String>> images, DetailedWorkOrder existingWorkOrder, RmNotification changeInfo) {
		boolean isChanged = false;
		StringBuilder sb = new StringBuilder();
		sb.append("Work Order #");
		sb.append(existingWorkOrder.getSemanticId());
		sb.append(", ");
		if (workType.isPresent() && !workType.get().equals(existingWorkOrder.getWorkType())) {
			sb.append("Work Type changed from ");
			sb.append(existingWorkOrder.getWorkType());
			sb.append(" to ");
			sb.append(workType.get());
			sb.append(", ");
			existingWorkOrder.setWorkType(workType.get());
			isChanged = true;
		}

		if (priority.isPresent() && !priority.get().value().equals(existingWorkOrder.getPriority())) {
			sb.append("Priority changed from ");
			sb.append(existingWorkOrder.getPriority());
			sb.append(" to ");
			sb.append(priority.get().value());
			sb.append(", ");
			existingWorkOrder.setPriority(priority.get().value());
			isChanged = true;
		}

		if (detail.isPresent() && !detail.get().equals(existingWorkOrder.getDetail())) {
			sb.append("Detail changed.");
			// sb.append(existingWorkOrder.getDetail());
			// sb.append(" to ");
			// sb.append(detail.get());
			// sb.append(", ");
			existingWorkOrder.setDetail(detail.get());
			isChanged = true;
		}

		if (accessInstruction.isPresent()
				&& !accessInstruction.get().equals(existingWorkOrder.getAccessInstruction())) {
			sb.append("Access Instruction changed from ");
			sb.append(existingWorkOrder.getAccessInstruction());
			sb.append(" to ");
			sb.append(accessInstruction.get());
			sb.append(", ");
			existingWorkOrder.setAccessInstruction(accessInstruction.get());
			isChanged = true;
		}

		if (preferredTime.isPresent() && !preferredTime.get().equals(existingWorkOrder.getPreferredTime())) {
			sb.append("Preferred Time changed from ");
			sb.append(existingWorkOrder.getPreferredTime());
			sb.append(" to ");
			sb.append(preferredTime.get());
			sb.append(", ");
			
			existingWorkOrder.setPreferredTime(preferredTime.get());
			isChanged = true;
		}

		if (entryPermission.isPresent()
				&& !entryPermission.get().value().equals(existingWorkOrder.getEntryPermission())) {
			
			sb.append("Entry Permission changed from ");
			sb.append(existingWorkOrder.getEntryPermission());
			sb.append(" to ");
			sb.append(entryPermission.get().value());
			sb.append(", ");
			existingWorkOrder.setEntryPermission(entryPermission.get().value());
			isChanged = true;
		}

		if (images.isPresent() && !images.get().equals(existingWorkOrder.getImages())) {
			sb.append("images change.");
			isChanged = true;
		}
		if(isChanged) {
			changeInfo.setMessage(sb.toString());
		}
		return isChanged;
	}

	@MutationMapping
	public Mono<DetailedWorkOrder> updateWorkOrderStatus(@Argument String uuid, @Argument WorkStatus status) {
		return detailedWoRepository.findById(uuid).flatMap(workOrder -> {
			RmNotification woInfo = new RmNotification(workorderChanged);
			woInfo.setOwner(workOrder.getOwner());
			woInfo.setSourceID(workOrder.getUuid());
			StringBuilder sb = new StringBuilder();
			sb.append("Work Order #");
			sb.append(workOrder.getSemanticId());
			sb.append(", ");
			sb.append("Status changed from ");
			sb.append(workOrder.getStatus());
			sb.append(" to ");
			sb.append(status.value());
			sb.append(". ");
			woInfo.setMessage(sb.toString());
			msgProducer.sendWorkOrderChanged(woInfo);			
			if(workOrder.getAssignedStaff() != null) {
				RmNotification noti = new RmNotification(woInfo);
				noti.setOwner(workOrder.getAssignedStaff());
				msgProducer.sendWorkOrderChanged(noti);
			}
			
			workOrder.setStatus(status.value());
			return detailedWoRepository.save(workOrder);
		}).switchIfEmpty(Mono.error(new NoneWorkOrderException("Work order does not exist: " + uuid)));
	}

	@MutationMapping
	public Mono<DetailedWorkOrder> assignWorkOrderStaff(@Argument String uuid, @Argument String assignedStaff) {
		return detailedWoRepository.findById(uuid).flatMap(workOrder -> {
			RmNotification woInfo = new RmNotification(workorderChanged);
			woInfo.setOwner(workOrder.getOwner());
			woInfo.setSourceID(workOrder.getUuid());
			StringBuilder sb = new StringBuilder();
			sb.append("Work Order #");
			sb.append(workOrder.getSemanticId());
			sb.append(", ");
			sb.append("Status changed to ");
			sb.append(WorkStatus.ASSIGNED.value());
			sb.append(", ");
			sb.append("Assigned staff is  ");
			sb.append(assignedStaff);
			sb.append(". ");
			woInfo.setMessage(sb.toString());
			msgProducer.sendWorkOrderChanged(woInfo);			
			RmNotification noti = new RmNotification(woInfo);
			noti.setOwner(assignedStaff);
			msgProducer.sendWorkOrderChanged(noti);
			
			workOrder.setAssignedStaff(assignedStaff);
			workOrder.setStatus(WorkStatus.ASSIGNED.value());
			return detailedWoRepository.save(workOrder);
		}).switchIfEmpty(Mono.error(new NoneWorkOrderException("Work order does not exist: " + uuid)));
	}

	@MutationMapping
	public Mono<DetailedWorkOrder> unassignWorkOrderStaff(@Argument String uuid) {
		return detailedWoRepository.findById(uuid).flatMap(workOrder -> {
			RmNotification woInfo = new RmNotification(workorderChanged);
			woInfo.setOwner(workOrder.getOwner());
			woInfo.setSourceID(workOrder.getUuid());
			StringBuilder sb = new StringBuilder();
			sb.append("Work Order #");
			sb.append(workOrder.getSemanticId());
			sb.append(": ");
			sb.append("Status changed to ");
			sb.append(WorkStatus.OPEN.value());
			sb.append(", ");
			sb.append(workOrder.getAssignedStaff());
			sb.append(" will not take this ticket.");
			woInfo.setMessage(sb.toString());
			msgProducer.sendWorkOrderChanged(woInfo);			
			if(workOrder.getAssignedStaff() != null) {
				RmNotification noti = new RmNotification(woInfo);
				noti.setOwner(workOrder.getAssignedStaff());
				msgProducer.sendWorkOrderChanged(noti);
			}
			
			workOrder.setAssignedStaff(null);
			workOrder.setStatus(WorkStatus.OPEN.value());
			
			return detailedWoRepository.save(workOrder);
		}).switchIfEmpty(Mono.error(new NoneWorkOrderException("Work order does not exist: " + uuid)));
	}

	@MutationMapping
	public Mono<CancelWorkOrderOutPut> cancelWorkOrder(@Argument String uuid) {
		DetailedWorkOrder workOrder = detailedWoRepository.findById(uuid).block();
		if (workOrder == null)
			Mono.error(new NoneWorkOrderException("Work order does not exist: " + uuid));

		RmNotification woInfo = new RmNotification(workorderDeleted);
		woInfo.setOwner(workOrder.getOwner());
		woInfo.setSourceID(workOrder.getUuid());
		StringBuilder sb = new StringBuilder();
		sb.append("Work Order #");
		sb.append(workOrder.getSemanticId());
		sb.append(" is deleted.");
		woInfo.setMessage(sb.toString());
		msgProducer.sendWorkOrderChanged(woInfo);			
		if(workOrder.getAssignedStaff() != null) {
			RmNotification noti = new RmNotification(woInfo);
			noti.setOwner(workOrder.getAssignedStaff());
			msgProducer.sendWorkOrderChanged(noti);
		}
		
		detailedWoRepository.deleteById(uuid).block();
		CancelWorkOrderOutPut out = new CancelWorkOrderOutPut();
		out.setUuid(uuid);
		return Mono.just(out);
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
