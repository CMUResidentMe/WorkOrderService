import { createWorkOrder } from '../DAL/workOrderDAL.js'
import { sendWorkOrderEvent } from '../kafka/producer.js'

export const resolvers = {
  Mutation: {
    buildWorkOrder: (parent, args, contextValue, info) => {
      //let wo = createWorkOrder(args);
      let wo = args;
      console.log(args)
      sendWorkOrderEvent(wo);
      return wo;
    },
    },
  };
  