import { gql } from 'apollo-server-express';

export const typeDefs = gql`
  enum WorkStatus {
    OPEN
    ASSIGNED
    ONGOING
    FIXED
    ACCEPTED
    CLOSED
  }

  type Staff{
    id: ID!
    name: String
    role: String
    availability: Boolean
  }

  type Resident{
    id: ID!
    name: String
    roomNumber: String
  }

  type WorkOrder{
    id: ID!
    workType: String!
    priority: Int
    detail: String
    assignedStaff: Staff
    status: WorkStatus!
  }

  type DetailedWorkOrder{
    id: ID!
    preferredTime: String
    images: [String!]
    voiceMomos: [String!]
    entryPermission: String
    accessInstruction: String
  }

  input WorkOrderContent {
    workType: String!
    priority: Int
    detail: String
  }

  type Query {
    workOrders: [WorkOrder]
  }

  type Mutation {
    buildWorkOrder(workType: String!, priority: Int, detail: String): WorkOrder
  }
`;
