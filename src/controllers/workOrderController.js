import {
    createWorkOrder,
    getWorkOrders,
} from "../DAL/workOrderDAL.js";

class WorkOrderController {

    async buildWorkOrder(workType, priority, detail) {
        try {
            let wk = await createWorkOrder(workType, priority, detail);
            await sendWorkOrderEvent(wk);
            return wk;
        } catch (error) {
            console.log("error", error);
            throw "buildWorkOrder failed";
        }
    }

}

const workOrderController = new WorkOrderController();
export default workOrderController;
