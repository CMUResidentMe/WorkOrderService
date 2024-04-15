package org.residentme.workorder.builder.imp;

import org.residentme.workorder.builder.WorkOrderBuilder;
import org.residentme.workorder.data.EntryPermission;
import org.residentme.workorder.data.Priority;
import org.residentme.workorder.data.WorkStatus;
import org.residentme.workorder.entity.DetailedWorkOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class WorkOrderDirector {

  @Autowired
  private WorkOrderBuilder builder;

  public void constructDetailedWorkOrder(String owner, String workType, Priority priority, String preferredTime, EntryPermission entryPermission,
			String accessInstruction, String detail, List<String> images) {
        builder.reset();
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

  public DetailedWorkOrder build() {
    return builder.build();
  }
}
