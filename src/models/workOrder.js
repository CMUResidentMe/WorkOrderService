import mongoose from "mongoose";

const WorkOrderSchema = new mongoose.Schema(
  {
    owner: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "Resident",
    },
    workType: { type: String },
    priority: { type: Number },
    detail: { type: String },
    assignedStaff: { 
      type: mongoose.Schema.Types.ObjectId,
      ref: "Staff",
    },
    status:  { type: String },
    preferredTime: { type: String },
    images: [{ type: mongoose.Schema.Types.ObjectId }],
    voiceMomos: [{ type: mongoose.Schema.Types.ObjectId }],
    entryPermission:  { type: String },
    accessInstruction: { type: String },
  },
  {
    timestamps: true,
  }
);

const WorkOrder = mongoose.model("WorkOrder", WorkOrderSchema);

export default WorkOrder;
