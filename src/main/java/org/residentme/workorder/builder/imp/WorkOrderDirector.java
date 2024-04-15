package org.residentme.workorder.builder.imp;

import org.residentme.workorder.builder.WorkOrderBuilder;
import org.residentme.workorder.data.EntryPermission;
import org.residentme.workorder.data.WorkStatus;
import org.residentme.workorder.entity.BasicWorkOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class WorkOrderDirector {

  @Autowired
  private WorkOrderBuilder builder;

  public void constructBasicWorkOrder(String owner, String workType, String priority, String detail, String assignedStaff) {
    builder.reset();
    builder.setOwner(owner);
    builder.setWorkType(workType);
    builder.setPriority(priority);
    builder.setDetail(detail);
//    builder.setAssignedStaff(assignedStaff);
    builder.setStatus(WorkStatus.OPEN); // Default status
  }

  public void constructDetailedWorkOrder(String owner, String workType, String priority, String detail, String assignedStaff,
                                         Optional<String> accessInstruction, Optional<String> preferredTime, Optional<EntryPermission> entryPermission) {
    constructBasicWorkOrder(owner, workType, priority, detail, assignedStaff);
    accessInstruction.ifPresent(builder::setAccessInstruction);
    preferredTime.ifPresent(builder::setPreferredTime);
    entryPermission.ifPresent(ep -> builder.setEntryPermission(ep));
  }

  public BasicWorkOrder build() {
    return builder.build();
  }
}
