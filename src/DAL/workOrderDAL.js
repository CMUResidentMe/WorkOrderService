import WorkOrder from "../models/workOrder.js";

const createWorkOrder = (userId, username, message, status) => {
  const wo = new WorkOrder({
    userId: userId,
    username: username,
    message: message,
    status: status,
  });
  return wo.save();
};

const getWorkOrders = () => {
    return Message.find({});
};

export {
  createWorkOrder,
  getWorkOrders,
};
