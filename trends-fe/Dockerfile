FROM node:14.13-alpine3.12

ADD . /trends-fe
WORKDIR /trends-fe
RUN rm -rf node_modules

RUN npm --version
RUN ls -al
RUN pwd

RUN npm install 
# npm install react-scripts@latest

CMD ["npm", "start"]

