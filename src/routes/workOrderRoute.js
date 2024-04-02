import express from 'express';
import { sendWorkOrderEvent } from '../kafka/producer.js';

const router = express.Router();

router.post('/', async (req, res) => {
  const workOrderData = req.body;

  // Here, you would typically save the work order to the database
  // For this example, we'll skip database interaction and simulate work order creation

  // Simulating work order ID and creation process
  const workOrderId = Math.floor(Math.random() * 1000);
  const workOrderDetails = { ...workOrderData, id: workOrderId, status: 'Created' };

  // Send an event to Kafka for the new work order
  await sendWorkOrderEvent(workOrderDetails);

  // Respond to the request indicating success
  return res.status(201).json({ message: 'Work order created', workOrder: workOrderDetails });
});

export default router;
