FROM node:8.16.0-jessie-slim

ADD . /trends-fe
WORKDIR /trends-fe
RUN rm -rf node_modules

RUN npm --version
RUN ls -al
RUN pwd

RUN npm install react-scripts@latest

CMD ["npm", "start"]

