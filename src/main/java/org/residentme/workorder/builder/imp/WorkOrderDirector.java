package org.residentme.workorder.builder.imp;

import org.residentme.workorder.builder.WorkOrderBuilder;
import org.residentme.workorder.data.EntryPermission;
import org.residentme.workorder.data.Priority;
import org.residentme.workorder.data.WorkStatus;
import org.residentme.workorder.entity.DetailedWorkOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Director class for building a DetailedWorkOrder object.
 */
@Component
public class WorkOrderDirector {

    /**
     * The builder to use to build the DetailedWorkOrder object.
      */
  @Autowired
  private WorkOrderBuilder builder;

    /**
     * Constructs a DetailedWorkOrder object.
     * @param semanticId The semanticId of the work order.
     * @param owner The owner of the work order.
     * @param workType The type of work order.
     * @param priority The priority of the work order.
     * @param preferredTime The preferred time of the work order.
     * @param entryPermission The entry permission of the work order.
     * @param accessInstruction The access instruction of the work order.
     * @param detail The detail of the work order.
     * @param images The images of the work order.
     */
  public void constructDetailedWorkOrder(String semanticId, String owner, String workType, Priority priority, String preferredTime, EntryPermission entryPermission,
			String accessInstruction, String detail, List<String> images) {
        builder.reset();
        builder.setSemanticId(semanticId);
        builder.setOwner(owner);
        builder.setWorkType(workType);
        builder.setPriority(priority);
        builder.setDetail(detail);
        builder.setStatus(WorkStatus.OPEN); // Default status
        builder.setPreferredTime(preferredTime);
        builder.setEntryPermission(entryPermission);
        builder.setAccessInstruction(accessInstruction);
        builder.setImages(images);
    	  builder.setAssignedStaff(null);
  }

    /**
     * Builds the DetailedWorkOrder object.
     * @return The DetailedWorkOrder object.
     */
  public DetailedWorkOrder build() {
    return builder.build();
  }
}
