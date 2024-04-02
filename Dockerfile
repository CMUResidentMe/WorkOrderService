# Use an official Node runtime as a parent image
FROM node:16

# Set the working directory in the container
WORKDIR /usr/src/app

# Install app dependencies
# A wildcard is used to ensure both package.json AND package-lock.json are copied
COPY package*.json ./

# Install any needed packages specified in package.json
RUN npm install

# Copy the rest of your application's source code from your host to your image's filesystem.
COPY . .

# Make port available to the world outside this container
EXPOSE 8080

# Run app when the container launches
CMD ["npm", "start"]
