package org.residentme.workorder.controller;


import graphql.GraphQLError;
import org.residentme.workorder.builder.imp.WorkOrderDirector;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

import org.residentme.workorder.data.EntryPermission;
import org.residentme.workorder.data.Priority;
import org.residentme.workorder.data.WorkStatus;
import org.residentme.workorder.dto.WorkOrderDTO;
import org.residentme.workorder.entity.DetailedWorkOrder;
import org.residentme.workorder.service.SequenceGeneratorService;
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
    private SequenceGeneratorService sequenceGenerator;
	
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
	public Mono<DetailedWorkOrder> workOrder(@Argument String uuid) {
		return detailedWoRepository.findById(uuid)
				.switchIfEmpty(Mono.error(new NoneWorkOrderException("Work order does not exist: " + uuid)));
	}


	@QueryMapping
	public Flux<DetailedWorkOrder> workOrdersByOwner(@Argument String ownerUuid) {
		logger.info("Querying work orders by owner UUID: {}", ownerUuid);
		DetailedWorkOrder workOrder = new DetailedWorkOrder();
		workOrder.setOwner(ownerUuid);
		ExampleMatcher matcher = ExampleMatcher.matchingAll().withMatcher("owner", match -> match.exact()).withIgnorePaths("uuid");
		Flux<DetailedWorkOrder> res = detailedWoRepository.findAll(Example.of(workOrder, matcher));
		logger.debug(res.toString());
		return res;
	}
	
	@QueryMapping
	public Flux<DetailedWorkOrder> workOrdersUnassigned(){
		DetailedWorkOrder workOrder = new DetailedWorkOrder();
		workOrder.setAssignedStaff(null);
		ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("assignedStaff", match -> match.exact()).withIgnorePaths("uuid");
		return detailedWoRepository.findAll(Example.of(workOrder, matcher));
	}

	@QueryMapping
	public Flux<DetailedWorkOrder> workOrdersByAssignedStaff(@Argument String assignedStaffUuid) {
		logger.info("Querying work orders by assigned staff UUID: {}", assignedStaffUuid);
		DetailedWorkOrder workOrder = new DetailedWorkOrder();
		workOrder.setAssignedStaff(assignedStaffUuid);
		ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("assignedStaff", match -> match.exact()).withIgnorePaths("uuid");
		return detailedWoRepository.findAll(Example.of(workOrder, matcher));
	}

	@QueryMapping
	public Flux<DetailedWorkOrder> workOrdersByStatus(@Argument WorkStatus status) {
		logger.info("Querying work orders by status: {}", status);
        DetailedWorkOrder workOrder = new DetailedWorkOrder();
        workOrder.setStatus(status.value());
        ExampleMatcher matcher = ExampleMatcher.matching().withMatcher("status", match -> match.exact()).withIgnorePaths("uuid");
        return detailedWoRepository.findAll(Example.of(workOrder, matcher));
    }

	@MutationMapping
	public Mono<DetailedWorkOrder> createWorkOrder(@Argument String owner, @Argument String workType, @Argument Priority priority,
                                                   @Argument String preferredTime, @Argument EntryPermission entryPermission, @Argument String accessInstruction,
                                                   @Argument String detail, @Argument List<String> images) {
        Long semanticId = sequenceGenerator.generateSequence("workorder_sequence").block();
        workOrderDirector.constructDetailedWorkOrder("WO-" + semanticId, owner, workType, priority, preferredTime, entryPermission, accessInstruction, detail, images);
        DetailedWorkOrder workOrder = workOrderDirector.build();
        workOrder = detailedWoRepository.save(workOrder).block();
		WorkOrderDTO res = new WorkOrderDTO(workOrder);
		msgProducer.sendWorkOrderCreated(res);
		return Mono.just(workOrder);
    }

	@MutationMapping
	public Mono<DetailedWorkOrder> changeWorkOrder(@Argument String uuid, @Argument Optional<String> workType, @Argument Optional<Priority> priority,
													@Argument Optional<String> detail, @Argument Optional<String> accessInstruction,
													@Argument Optional<String> preferredTime, @Argument Optional<EntryPermission> entryPermission,
													@Argument Optional<List<String>> images) {
		return detailedWoRepository.findById(uuid).flatMap(existingWorkOrder -> {
			StringBuilder changeDetails = new StringBuilder();
			workType.ifPresent(newType -> {
				if (!newType.equals(existingWorkOrder.getWorkType())) {
					existingWorkOrder.setWorkType(newType);
					changeDetails.append("Work Type changed to ").append(newType).append(". ");
				}
			});
			priority.ifPresent(newPriority -> {
				if (!newPriority.value().equals(existingWorkOrder.getPriority())) {
					existingWorkOrder.setPriority(newPriority.value());
					changeDetails.append("Priority changed from ")
								 .append(existingWorkOrder.getPriority())
								 .append(" to ")
								 .append(newPriority.value())
								 .append(". ");
				}
			});
			detail.ifPresent(newDetail -> {
				if (!newDetail.equals(existingWorkOrder.getDetail())) {
					existingWorkOrder.setDetail(newDetail);
					changeDetails.append("Detail changed. ");
				}
			});
			accessInstruction.ifPresent(newAccessInstruction -> {
				if (!newAccessInstruction.equals(existingWorkOrder.getAccessInstruction())) {
					existingWorkOrder.setAccessInstruction(newAccessInstruction);
					changeDetails.append("Access Instruction changed. ");
				}
			});
			preferredTime.ifPresent(newPreferredTime -> {
				if (!newPreferredTime.equals(existingWorkOrder.getPreferredTime())) {
					existingWorkOrder.setPreferredTime(newPreferredTime);
					changeDetails.append("Preferred Time changed. ");
				}
			});
			entryPermission.ifPresent(newEntryPermission -> {
				if (!newEntryPermission.value().equals(existingWorkOrder.getEntryPermission())) {
					existingWorkOrder.setEntryPermission(newEntryPermission.value());
					changeDetails.append("Entry Permission changed from ")
								 .append(existingWorkOrder.getEntryPermission())
								 .append(" to ")
								 .append(newEntryPermission.value())
								 .append(". ");
				}
			});
			images.ifPresent(newImages -> {
				if (!newImages.equals(existingWorkOrder.getImages())) {
					existingWorkOrder.setImages(newImages);
					changeDetails.append("Images changed. ");
				}
			});
			if (changeDetails.length() > 0) {
				WorkOrderDTO workOrderDTO = new WorkOrderDTO(existingWorkOrder);
				// Use semanticId for user-friendly notification
				String changeMsg = "Changes to Work Order #" + existingWorkOrder.getSemanticId() + ": " + changeDetails.toString();
				workOrderDTO.setChangeDescription(changeMsg);
				msgProducer.sendWorkOrderChanged(workOrderDTO);
			}
			return detailedWoRepository.save(existingWorkOrder);
		}).switchIfEmpty(Mono.error(new NoneWorkOrderException("Work order does not exist: " + uuid)));
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
	public Mono<DetailedWorkOrder> assignWorkOrderStaff(@Argument String uuid, @Argument String assignedStaff) {
		return detailedWoRepository.findById(uuid).flatMap(workOrder -> {
			if (workOrder.getStatus().equals(WorkStatus.OPEN.value())) {
				System.out.println("staffId is " + assignedStaff);
				workOrder.setAssignedStaff(assignedStaff);
				workOrder.setStatus(WorkStatus.ASSIGNED.value());
				msgProducer.sendWorkOrderChanged(new WorkOrderDTO(workOrder)); // Notify change
				return detailedWoRepository.save(workOrder);
			}
			return Mono.error(new IllegalStateException("Work order is already assigned or closed"));
		}).switchIfEmpty(Mono.error(new NoneWorkOrderException("Work order does not exist: " + uuid)));
	}
	
	@MutationMapping
	public Mono<DetailedWorkOrder> unassignWorkOrderStaff(@Argument String uuid) {
		return detailedWoRepository.findById(uuid).flatMap(workOrder -> {
			if (workOrder.getStatus().equals(WorkStatus.ASSIGNED.value())) {
				workOrder.setAssignedStaff(null);
				workOrder.setStatus(WorkStatus.OPEN.value());
				msgProducer.sendWorkOrderChanged(new WorkOrderDTO(workOrder));
				return detailedWoRepository.save(workOrder);
			}
			return Mono.error(new IllegalStateException("Work order is not assigned"));
		}).switchIfEmpty(Mono.error(new NoneWorkOrderException("Work order does not exist: " + uuid)));
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
