import express from 'express';
import dotenv from 'dotenv';
import { ApolloServer } from 'apollo-server-express';
import { typeDefs } from './graphql/schema.js';
import { resolvers } from './graphql/resolvers.js';
import { connectProducer } from './kafka/producer.js';

dotenv.config();
const app = express();

const startServer = async () => {
  //databaseContext = await initializeDatabase();
  connectProducer();
  const app = express();
  const apolloServer = new ApolloServer({
    typeDefs,
    resolvers,
  });
  await apolloServer.start(); // Make sure to start Apollo Server before applying middleware
  
  apolloServer.applyMiddleware({ app });
};

startServer();

export default app;
