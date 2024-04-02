import { Kafka } from 'kafkajs';

const kafkaConfig = {
  clientId: 'work-order-service',
  brokers: ['kafka:9092'],
  sasl: {
    mechanism: 'scram-sha-256',
    username: 'workorderuser',
    password: 'workorderpassword'
  },
  ssl: false,
};

const kafka = new Kafka(kafkaConfig);
export default kafka;
